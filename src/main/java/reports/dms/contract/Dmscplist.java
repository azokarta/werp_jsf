package reports.dms.contract;

import f4.BranchF4;
import f4.BukrsF4;
import finance.reports.Frep8.OutputTable;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.comparators.ConPaymentGraphicComparator;
import general.dao.AddressDao;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.PaymentScheduleDao;
import general.dao.StaffDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.services.ZreportService;
import general.tables.Address;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Contract;
import general.tables.ContractStatus;
import general.tables.PaymentSchedule;
import general.tables.Role_action;
import general.tables.Staff;
import general.tables.Zreport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.directory.SearchControls;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

import user.User;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;

@ManagedBean(name = "dmscplistBean", eager = true)
@ViewScoped
public class Dmscplist {
	private final static String transaction_code = "DMSCPLIST";
	private final static Long transaction_id = (long) 126;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

	// *********************************************************************

	public void search() {
		try {
			if (search_contract != null) {
				if (search_contract.getBranch_id() != null
						&& search_contract.getBranch_id() > 0) {

					Calendar cal = Calendar.getInstance();
					int selMon = -1;
					int selYear = -1;
					if (search_contract.getContract_date() != null) {
						cal.setTime(search_contract.getContract_date());
						selMon = cal.getTime().getMonth();
						selYear = cal.getTime().getYear();
					} else {
						throw new DAOException("Please select month!");
					}

					outputTable = new ArrayList<ContractPaymentGraphic>();
					conPaymentList = new ArrayList<ContractPaymentGraphic>();

					ContractDao conDao = (ContractDao) appContext.getContext()
							.getBean("contractDao");
					PaymentScheduleDao psDao = (PaymentScheduleDao) appContext
							.getContext().getBean("paymentScheduleDao");
					CustomerDao cusDao = (CustomerDao) appContext.getContext()
							.getBean("customerDao");
					StaffDao staffDao = (StaffDao) appContext.getContext()
							.getBean("staffDao");
					BkpfDao bkpfDao = (BkpfDao) appContext.getContext()
							.getBean("bkpfDao");
					AddressDao addrDao = (AddressDao) appContext.getContext()
							.getBean("addressDao");

					String wcl = " finBranchId = "
							+ search_contract.getBranch_id()
							+ " and paid < price and payment_schedule > 0"
							+ " and contract_status_id = "
							+ search_contract.getContract_status_id();
					// + " and contract_status_id <> " +
					// ContractStatus.STATUS_GIFT
					// + " and contract_status_id <> " +
					// ContractStatus.STATUS_CLOSED;
					// + " and contract_status_id <> " +
					// ContractStatus.STATUS_LAWYER;
					// + " and contract_status_id <> " +
					// ContractStatus.STATUS_PRECOURT;
					if (search_contract.getCollector() > 0) {
						wcl += " and collector = "
								+ search_contract.getCollector();
					}

					// wcl += " order by ";
					List<Contract> cl = conDao.dynamicFindContracts(wcl);

					// System.out.println("Where clause for contracts query: "+wcl);
					System.out.println("Contracts found: " + cl.size());

					for (Contract c : cl) {
						List<PaymentSchedule> psL = new ArrayList<PaymentSchedule>();
						psL = psDao.findAllByAwkey(c.getAwkey(),c.getBukrs());

						int jj = 0;
						for (PaymentSchedule ps : psL) {
							int year = selYear - ps.getPayment_date().getYear();
							int mon = (selMon + year * 12) - ps.getPayment_date().getMonth();
							
							if (mon >= 0 && (ps.getSum() - ps.getPaid() > 0)
									&& jj > 0) {
								// insert into outputTable;
								ContractPaymentGraphic cpg = new ContractPaymentGraphic(
										c);
								cpg.setBranch(p_branchF4Bean.getL_branch_map()
										.get(c.getBranch_id()));
								cpg.setCustomer(cusDao.find(c.getCustomer_id()));
								
								if (cpg.getCustomer() != null && cpg.getCustomer().getBirthday() != null) {
									SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
									cpg.setBday(f.format(cpg.getCustomer().getBirthday()));
								}
								
								if (!Validation.isEmptyLong(c.getDealer()))
									cpg.setDealer(staffDao.find(c.getDealer()));
								// String dwcl = "";
								// dwcl = dwcl + "contract_number = "
								// + c.getContract_number()
								// + " and blart= 'GC'"
								// + " and storno = 0";
								// Bkpf bkpf =
								// bkpfDao.dynamicFindSingleBkpf(dwcl);
								cpg.setCurrency(c.getWaers());
								// System.out.println("BKPF blart type: " +
								// bkpf.getBlart());
								// System.out.println(cpg.getCurrency());
								cpg.setPsL(psL);

								if (!Validation.isEmptyLong(c.getAddrHomeId())) {
									Address addr = addrDao.find(c
											.getAddrHomeId());
									if (addr != null) {
										cpg.setAddress(addr.getAddress());
										cpg.setTel("");
										if (!Validation.isEmptyString(addr
												.getTelDom()))
											cpg.setTel("" + addr.getTelDom());

										if (!Validation.isEmptyString(addr
												.getTelMob1())) {
											if (cpg.getTel().length() > 0)
												cpg.setTel(cpg.getTel() + " ");
											cpg.setTel(cpg.getTel()
													+ addr.getTelMob1());
										}

										if (!Validation.isEmptyString(addr
												.getTelMob2())) {
											if (cpg.getTel().length() > 0)
												cpg.setTel(cpg.getTel() + " ");
											cpg.setTel(cpg.getTel()
													+ addr.getTelMob2());
										}
									}
								}
								if (!Validation.isEmptyLong(c.getAddrWorkId())) {
									Address addr = addrDao.find(c
											.getAddrWorkId());
									if (addr != null
											&& !Validation.isEmptyString(addr
													.getTelDom()))
										cpg.setRab(addr.getTelDom());
								}
								
								if (!Validation.isEmptyString(cpg.getCustomer().getFullPhone())) {
									cpg.setTel(cpg.getCustomer().getFullPhone());
									cpg.setRab("");
								}

								SimpleDateFormat format1 = new SimpleDateFormat(
										"dd.MM.YYYY");
								for (PaymentSchedule wa_ps : psL) {
									String wa_pdate = format1.format(wa_ps
											.getPayment_date());
									cpg.pdate.add(wa_pdate);
								}

								Calendar dv = Calendar.getInstance();
								if (c.getContract_date() != null)
									dv.setTime(c.getContract_date());

								cpg.data_vyd = format1.format(dv.getTime());

								outputTable.add(cpg);
								break;
							}
							jj++;
						}
					}

					outputTable.sort(new ConPaymentGraphicComparator());
					calcPlan(selMon, selYear);
					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("form:outputTable");
					reqCtx.update("form:summPlan");
					reqCtx.update("form:planWaers");
					reqCtx.update("form:qPlan");
				} else {
					throw new DAOException("Please select branch!");
				}
			}
		} catch (DAOException ex) {
			addMessage("Error!", ex.getMessage());
		}
	}

	public void calcPlan(int selMonth, int selYear) {
		summPlan = 0;
		search_contract.getContract_date();
		for (ContractPaymentGraphic cpg : outputTable) {
			int jj = 0;
			List<PaymentSchedule> psL = cpg.getPsL();
			for (PaymentSchedule ps : psL) {
				int year = selYear - ps.getPayment_date().getYear();
				int mon = (selMonth + year * 12) - ps.getPayment_date().getMonth();
				if (mon >= 0 && (ps.getSum() - ps.getPaid() > 0)
						&& jj > 0) {
					summPlan += ps.getSum() - ps.getPaid();
				}
				jj++;
			}
		}
		p_waers = "";
		if (outputTable.size() > 0)
			p_waers = outputTable.get(0).getContract().getWaers();
	}

	public double summPlan;
	public String p_waers;

	public String getP_waers() {
		return p_waers;
	}

	public void setP_waers(String p_waers) {
		this.p_waers = p_waers;
	}

	public double getSummPlan() {
		return summPlan;
	}

	public void setSummPlan(double summPlan) {
		this.summPlan = summPlan;
	}

	// *********************************************************************

	@PostConstruct
	public void init() {
		try {
			search_contract = new Contract();
			bukrs_list = new ArrayList<Bukrs>();

			search_contract.setContract_status_id(new Long(
					ContractStatus.STATUS_STANDARD));

			checkPermission(Dmscplist.read);

			if (userData.isMain() || userData.isSys_admin()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
			} else {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					System.out.println(wa_bukrs.getBukrs());
					if (wa_bukrs.getBukrs().equals(userData.getBukrs())) {
						bukrs_list.add(wa_bukrs);

						search_contract.setBranch_id((long) userData
								.getBranch_id());
						search_contract.setBukrs(userData.getBukrs());
						loadBranch2();
						loadStaff();
						break;
					}
				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} catch (DAOException ex) {
			addMessage("Error", ex.getMessage());
			toMainPage();
		}
	}

	// ******************************************************************
	// ***************** LOAD BRANCH LIST *******************************

	public void loadBranch2() {
		branch_list = new ArrayList<Branch>();

		Long branchId = userData.getBranch_id();
		if (userData.isMain() || userData.isSys_admin()) {
			if (search_contract.getBukrs().equals("1000"))
				branchId = Branch.AURA_MAIN_BRANCH_ID;
			else if (search_contract.getBukrs().equals("2000"))
				branchId = Branch.GREEN_LIGHT_MAIN_BRANCH_ID;
		}

		branch_list = loadBranchListByUserBranch(search_contract.getBukrs(),
				branchId, BusinessArea.AREA_ALL_EXCEPT_SERVICE);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
	}

	public List<Branch> loadBranchListByUserBranch(String a_bukr,
			Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));

		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		Branch inBranch = brDao.find(branchId);

		List<Branch> brL = new ArrayList<Branch>();
		if (inBranch.getType() != Branch.TYPE_BRANCH
				|| inBranch.getBusiness_area_id() == 0) {
			bf.addFilter(new BranchParentFilter(inBranch.getBranch_id()));
			brL = bf.filterBranch(p_branchF4Bean.getBranch_list());
		} else
			bf.addFilter(new BranchParentFilter(inBranch.getParent_branch_id()));

		bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));

		output = bf.filterBranch(p_branchF4Bean.getBranch_list());

		for (Branch br : brL) {
			if (br.getType() != Branch.TYPE_BRANCH
					|| br.getBusiness_area_id() == 0)
				output.addAll(loadBranchListByUserBranch(a_bukr,
						br.getBranch_id(), ba));
		}
		return output;
	}

	// ****************************LOAD_STAFF**********************************

	public void loadStaff() {
		try {
			outputTable = new ArrayList<ContractPaymentGraphic>();
			l_collector = new ArrayList<Staff>();

			String dwcl = "";
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean(
					"staffDao");
			dwcl = dwcl
					+ " and sal.position_id in (9, 62) and sal.branch_id = "
					+ search_contract.getBranch_id() + " and sal.bukrs = "
					+ search_contract.getBukrs();

			l_collector = staffDao.dynamicFindStaffSalary(dwcl);
			if (l_collector.size() > 0) {
				for (Staff wa_staff : l_collector) {
					wa_staff.setFirstname(wa_staff.getLF());
				}
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:collector");
			reqCtx.update("form:outputTable");

		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
		}
	}

	// ******************************************************************

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void toMainPage() {
		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			context.redirect(context.getRequestContextPath()
					+ "/general/mainPage.xhtml");
		} catch (Exception ex) {
			addMessage("Error", ex.getMessage());
		}
	}

	// *****************************************************************************
	// *****************************************************************************

	public void checkPermission(Long a_action_id) {
		boolean hasPermission = false;
		if (userData.isSys_admin()) {
			hasPermission = true;
		} else {
			for (Role_action ra : userData.getUserRoleActions()) {
				if (ra.getTransaction_id().longValue() == this.transaction_id
						&& (ra.getAction_id() == a_action_id.intValue())
						|| ra.getAction_id() == 3) {
					hasPermission = true;
					break;
				}
			}
		}
		if (hasPermission == false) {
			throw new DAOException("No Permission");
		}
	}

	public void preProcessPDF(Object doc) throws IOException,
			BadElementException, DocumentException {
		Document pdf = (Document) doc;
		Rectangle a4 = new Rectangle(PageSize.A4);
		a4 = a4.rotate();
		pdf.setPageSize(a4);
		pdf.setMargins(15, 15, 15, 15);

		pdf.open();

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		String logo = servletContext.getRealPath("") + File.separator
				+ "resources" + File.separator + "img" + File.separator
				+ "werp_logo_wide.png";

		// C:\workspace\werp\src\main\webapp\resources\img
		Image img = Image.getInstance(logo);
		// img.rectangle(10, 5);
		img.setWidthPercentage(1);
		pdf.add(img);

		String fontpath = servletContext.getRealPath("") + File.separator
				+ "resources" + File.separator + "font" + File.separator
				+ "arial.ttf";
		// BaseFont bf = BaseFont.createFont( fontpath, BaseFont.IDENTITY_H,
		// BaseFont.EMBEDDED);
		// Font f1 = new Font(bf, 9);

		Font f1 = FontFactory.getFont(fontpath, "CP1251", true);
		f1.setColor(0, 0, 0);
		f1.setStyle(Font.NORMAL);
		f1.setSize(9);

		Paragraph p = new Paragraph("Contract Payment schedule for branch: "
				+ p_branchF4Bean.getL_branch_map()
						.get(search_contract.getBranch_id()).getText45(), f1);
		pdf.add(p);
		String str = "Список платежей на филиал: "
				+ p_branchF4Bean.getL_branch_map()
						.get(search_contract.getBranch_id()).getText45();
		pdf.add(new Paragraph(str, f1));
		// pdf.add(new Paragraph(str, FontFactory.getFont(FontFactory.HELVETICA,
		// 12, Font.BOLD, new Color(15, 128, 255))));
		p = new Paragraph("Collector: All", f1);
		pdf.add(p);
		p = new Paragraph(" ");
		pdf.add(p);

	}

	// **********************************************************************************

	public void printPDF(Object document) {

	}

	// **********************************************************************************

	// ******************************************************************
	// ******************************************************************
	// ******************************************************************
	// ******************************************************************
	// ******************************************************************
	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ******************************************************************
	// ************************** BranchF4 ******************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;

	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}

	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	List<Branch> branch_list = new ArrayList<Branch>();

	public List<Branch> getBranch_list() {
		return branch_list;
	}

	// ******************************************************************
	// ***************************BukrsF4*******************************
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;

	public BukrsF4 getP_bukrsF4Bean() {
		return p_bukrsF4Bean;
	}

	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
		this.p_bukrsF4Bean = p_bukrsF4Bean;
	}

	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrs_list() {
		return bukrs_list;
	}

	// ******************************************************************

	public Contract search_contract = new Contract();

	public Contract getSearch_contract() {
		return search_contract;
	}

	public void setSearch_contract(Contract search_contract) {
		this.search_contract = search_contract;
	}

	// ******************************************************************
	private List<Staff> l_collector = new ArrayList<Staff>();

	public List<Staff> getL_collector() {
		return l_collector;
	}

	public void setL_collector(List<Staff> l_collector) {
		this.l_collector = l_collector;
	}

	// ******************************************************************

	public List<ContractPaymentGraphic> outputTable;
	public List<ContractPaymentGraphic> conPaymentList;

	public List<ContractPaymentGraphic> getOutputTable() {
		return outputTable;
	}

	public void setOutputTable(List<ContractPaymentGraphic> outputTable) {
		this.outputTable = outputTable;
	}

	public static List<ContractPaymentGraphic> getVznosListTable() {
		List<ContractPaymentGraphic> vznosTable = new ArrayList<ContractPaymentGraphic>();

		Dmscplist d = new Dmscplist();
		vznosTable.addAll(d.outputTable);

		return vznosTable;
	}

	// ********************************************************************************

	public void postProcessXLSDetail(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		header.setHeightInPoints(30);
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		HSSFFont fontHeader = wb.createFont();
		fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellStyle.setFont(fontHeader);
		
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		cellStyle2.setDataFormat((short) 2);

		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
			String value = cell.getStringCellValue();
			value = value.replaceAll("<br/>", " ");
			cell.setCellValue(value);
		}
		
		HSSFCellStyle cellStyle3 = wb.createCellStyle();
		cellStyle3.cloneStyleFrom(cellStyle2);
		cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		//cellStyle3.setShrinkToFit(true);					
		cellStyle3.setWrapText(true);
		
		HSSFFont fontCell = wb.createFont();
		fontCell.setFontHeightInPoints((short)9);
		cellStyle3.setFont(fontCell);
		
		
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			if (i > 0) {
				HSSFRow row = sheet.getRow(i);
				
				for (int i2 = 0; i2 < row.getPhysicalNumberOfCells(); i2++) {
					HSSFCell cell = row.getCell(i2);
					cell.setCellStyle(cellStyle3);
					
					String value = cell.getStringCellValue();
					value = value.replaceAll("<br/>", " ");
					// System.out.println(cell.get);
					// System.out.println(value);
					// value = value.replace(",", ".");
					// double dvalue = Double.valueOf(value);
					// value = value.replace("\\s","");
					
					if (i2==0) {
						Branch br = p_branchF4Bean.getL_branch_map().get(search_contract.getBranch_id());
						if (br != null && !Validation.isEmptyLong(br.getBranch_id()))
							value = value.replaceAll(br.getText45(), " ");
					}
					value = value.trim();
					cell.setCellValue(value);
				}
			}

		}
		HSSFRow row = sheet.getRow(1);
		for (int i2 = 0; i2 < row.getPhysicalNumberOfCells(); i2++) {
			sheet.autoSizeColumn(i2);
		}
	}

	//*****************************************************************************************
	
	public void downloadExcelResult() {
        try {
        	Long a_rep_id=106L;
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(1);
            HSSFCell stringCell = row2.getCell(1); HSSFCellStyle stringStyle = stringCell.getCellStyle();
//        	HSSFCell dateCell = row2.getCell(5); HSSFCellStyle dateStyle = dateCell.getCellStyle();
//        	HSSFCell doubleCell = row2.getCell(7); HSSFCellStyle doubleStyle = doubleCell.getCellStyle();
        	
            int rowNum = 1;
            for (ContractPaymentGraphic wa_out:outputTable)
			{
            	HSSFRow row = sheet.getRow(rowNum);
            	if (row==null)
            	{
            		sheet.createRow(rowNum);
            		row = sheet.getRow(rowNum);
            	}
            	
            	HSSFCell cell0 = row.createCell(0); 
            	HSSFCell cell1 = row.createCell(1); 
            	HSSFCell cell2 = row.createCell(2); 
            	HSSFCell cell3 = row.createCell(3); 
            	HSSFCell cell4 = row.createCell(4); 
            	HSSFCell cell5 = row.createCell(5); 
            	HSSFCell cell6 = row.createCell(6); 
            	HSSFCell cell7 = row.createCell(7); 
            	HSSFCell cell8 = row.createCell(8);
            	
            	HSSFCell cell9 = row.createCell(9);
            	HSSFCell cell10 = row.createCell(10);
            	HSSFCell cell11 = row.createCell(11);
            	HSSFCell cell12 = row.createCell(12);
            	HSSFCell cell13 = row.createCell(13);
            	HSSFCell cell14 = row.createCell(14);
            	HSSFCell cell15 = row.createCell(15);
            	HSSFCell cell16 = row.createCell(16);
            	HSSFCell cell17 = row.createCell(17);
            	HSSFCell cell18 = row.createCell(18);
            	HSSFCell cell19 = row.createCell(19);
            	HSSFCell cell20 = row.createCell(20);
            	HSSFCell cell21 = row.createCell(21);
            	HSSFCell cell22 = row.createCell(22);
            	HSSFCell cell23 = row.createCell(23);
            	
            	cell0.setCellStyle(stringStyle);
            	cell1.setCellStyle(stringStyle);
            	cell2.setCellStyle(stringStyle);
            	cell3.setCellStyle(stringStyle);
            	cell4.setCellStyle(stringStyle);
            	cell5.setCellStyle(stringStyle);
            	cell6.setCellStyle(stringStyle);
            	cell7.setCellStyle(stringStyle);
            	cell8.setCellStyle(stringStyle);
            	cell9.setCellStyle(stringStyle);
            	cell10.setCellStyle(stringStyle);
            	cell11.setCellStyle(stringStyle);
            	cell12.setCellStyle(stringStyle);
            	cell13.setCellStyle(stringStyle);
            	cell14.setCellStyle(stringStyle);
            	cell15.setCellStyle(stringStyle);
            	cell16.setCellStyle(stringStyle);
            	cell17.setCellStyle(stringStyle);
            	cell18.setCellStyle(stringStyle);
            	cell19.setCellStyle(stringStyle);
            	cell20.setCellStyle(stringStyle);
            	cell21.setCellStyle(stringStyle);
            	cell22.setCellStyle(stringStyle);
            	cell23.setCellStyle(stringStyle);
            	
            	SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
            	
            	cell0.setCellValue(wa_out.getBranch().getText45() + " " + wa_out.getContract().getContract_number());
            	cell1.setCellValue(wa_out.getCustomer().getFullFIO());
            	cell2.setCellValue(wa_out.getBday());
            	cell3.setCellValue(wa_out.getDealer().getLF());
            	cell4.setCellValue(f.format(wa_out.getContract().getContract_date()));
            	cell5.setCellValue(wa_out.getContract().getPrice() + " " + wa_out.getContract().getWaers());
            	
            	cell6.setCellValue(wa_out.getContract().getFirst_payment());
            	
            	for (int i = 0; i< wa_out.getPsL().size(); i++) {
            		PaymentSchedule ps = wa_out.getPsL().get(i);
        			Double sum = ps.getSum()-ps.getPaid();
        			switch (i) {
            			case 0: break;
            			case 1: cell7.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 2: cell8.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 3: cell9.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 4: cell10.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 5: cell11.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 6: cell12.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 7: cell13.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 8: cell14.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 9: cell15.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 10: cell16.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 11: cell17.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 12: cell18.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 13: cell19.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 14: cell20.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 15: cell21.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;
            			case 16: cell22.setCellValue(f.format(ps.getPayment_date()) + " " + sum.toString()); break;            			
            		}
            	}
            	cell23.setCellValue(wa_out.getAddress()+" "+wa_out.getTel()+" "+wa_out.getRab());
            	rowNum++;
			}
   
            //calling servlet to download
            String contentType = "application/vnd.ms-excel";
            FacesContext fc = FacesContext.getCurrentInstance();                     
            HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
            response.setHeader("Content-disposition", "attachment; filename=" + wa_zr.getName());
            response.setContentType(contentType);
                    
            //writing excel to outputstream
            ServletOutputStream out = response.getOutputStream(); 
            wb.write(out); 
            
            //flushing and closing
            out.flush(); 
            out.close(); 
            fc.responseComplete();
//        	}
//        	else throw new DAOException("Нет записей");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
    
    
	}
}
