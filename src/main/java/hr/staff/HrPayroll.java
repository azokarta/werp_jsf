package hr.staff;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import f4.BukrsF4;
import general.AppContext; 
import general.GeneralUtil;
import general.dao.DAOException; 
import general.dao.PayrollDao;
import general.dao.UserRoleDao;
import general.services.ZreportService;
import general.tables.Bukrs; 
import general.tables.Payroll;
import general.tables.Staff;
import general.tables.UserRole;
import general.tables.Zreport;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext; 

import user.User;

@ManagedBean(name = "hrPayrollBean", eager = true)
@ViewScoped
public class HrPayroll implements Serializable{
	/**
	 * 
	 */
	private Calendar curDate = Calendar.getInstance();

	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "HRPAYROLL";

	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2; 
	//***************************Application Context********************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
	    return appContext;
	}

	public void setAppContext(AppContext appContext) {
	    this.appContext = appContext;
	}
	//******************************************************************
	//***************************User session***************************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
	    return userData;
	}
	public void setUserData(User userData) {
	    this.userData = userData;
	}
	//******************************************************************	

	// ******************************************************************	
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;
	public BukrsF4 getP_bukrsF4Bean() {
	    return p_bukrsF4Bean;
	}
	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
	    this.p_bukrsF4Bean = p_bukrsF4Bean;
	}
	

	@PostConstruct
	public void init() { 
		try
		{  
			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) { 
	            return; // Skip ajax requests.
	        }
			
			
			
			
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	public void loadItems(){
		try{
			//List<Salary> l_salary = new ArrayList<Salary>();
			p_outputTablePayroll.l_payroll_schet = new ArrayList<Payroll>();
			p_outputTablePayroll.l_payroll_deposit = new ArrayList<Payroll>();
			p_outputTablePayroll.l_payroll_avans_zapros = new ArrayList<Payroll>();			
			p_outputTablePayroll.l_payroll_zablok = new ArrayList<Payroll>();
			p_outputTablePayroll.l_payroll_dolg = new ArrayList<Payroll>();
			//List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();
			
			PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");		
			List<Long> l_staff_id = new ArrayList<Long>();
			l_staff_id.add(selectedStaff.getStaff_id());
			
			if (canRead())
			{
				for(Bukrs wa_bukrs:p_bukrsF4Bean.getBukrs_list())
				{
					List<Payroll> l_payroll = new ArrayList<Payroll>();
					List<Payroll> l_payroll1 = new ArrayList<Payroll>();
					List<Payroll> l_payroll2 = new ArrayList<Payroll>();
					List<Payroll> l_payroll3 = new ArrayList<Payroll>();
					List<Payroll> l_payroll4 = new ArrayList<Payroll>();
					List<Payroll> l_payroll5 = new ArrayList<Payroll>();
					l_payroll1 = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),wa_bukrs.getBukrs());
					p_outputTablePayroll.l_payroll_schet.addAll(l_payroll1);
					
					l_payroll2 = payrollDao.findByBukrsBranchAllDeposit(l_staff_id,wa_bukrs.getBukrs());
					p_outputTablePayroll.l_payroll_deposit.addAll(l_payroll2);
					
					l_payroll3 = payrollDao.findByBukrsBranchAvansZapros(l_staff_id,  GeneralUtil.getSQLDate(curDate),wa_bukrs.getBukrs());
					p_outputTablePayroll.l_payroll_avans_zapros.addAll(l_payroll3);
					
					l_payroll4 = payrollDao.findByBukrsBranchAllZablok(l_staff_id,  GeneralUtil.getSQLDate(curDate),wa_bukrs.getBukrs());
					p_outputTablePayroll.l_payroll_zablok.addAll(l_payroll4);
					
					l_payroll5 = payrollDao.findByBukrsBranchAllDolg(l_staff_id,  GeneralUtil.getSQLDate(curDate),wa_bukrs.getBukrs());
					p_outputTablePayroll.l_payroll_dolg.addAll(l_payroll5);
					l_payroll.addAll(l_payroll1);
					l_payroll.addAll(l_payroll2);
					l_payroll.addAll(l_payroll3);
					l_payroll.addAll(l_payroll4);
					l_payroll.addAll(l_payroll5);
					
					for (Payroll wa_prl:l_payroll)
					{
						wa_prl.setBukrs(wa_bukrs.getBukrs());
					}
				}
			}
			
			
			
			
			
			
			
			
			//System.out.println(count);
		}
		catch (DAOException ex)
		{
			addMessage("Info  ",ex.getMessage());  
		}
		
	}

	private boolean canRead()
	{
		if (selectedStaff!=null && selectedStaff.getStaff_id()!=null)
		{
			if (userData.getStaff().getStaff_id().equals(selectedStaff.getStaff_id()))
			{
				return true;
			}
			UserRoleDao userRoleDao = (UserRoleDao) appContext.getContext().getBean("userRoleDao");	
			for (UserRole wa:userRoleDao.findUserRoles(userData.getUserid()))
			{
				if (wa.getRoleId().equals(1L) || wa.getRoleId().equals(8L) || wa.getRoleId().equals(52L) || wa.getRoleId().equals(115L))
				{
					return true;
				}
			}
			
		}
		return false;
	}
	
	private OutputTablePayroll p_outputTablePayroll = new OutputTablePayroll();	
	
	

	public OutputTablePayroll getP_outputTablePayroll() {
		return p_outputTablePayroll;
	}

	public void setP_outputTablePayroll(OutputTablePayroll p_outputTablePayroll) {
		this.p_outputTablePayroll = p_outputTablePayroll;
	}

	public class OutputTablePayroll{
		public OutputTablePayroll()
		{
			
		}
		private List<Payroll> l_payroll_schet = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_deposit = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_zablok = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_avans_zapros = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_dolg = new ArrayList<Payroll>();
		private Long staff_id;
		private String staff_name;
		
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public String getStaff_name() {
			return staff_name;
		}
		public void setStaff_name(String staff_name) {
			this.staff_name = staff_name;
		}
		public List<Payroll> getL_payroll_schet() {
			return l_payroll_schet;
		}
		public void setL_payroll_schet(List<Payroll> l_payroll_schet) {
			this.l_payroll_schet = l_payroll_schet;
		}
		public List<Payroll> getL_payroll_deposit() {
			return l_payroll_deposit;
		}
		public void setL_payroll_deposit(List<Payroll> l_payroll_deposit) {
			this.l_payroll_deposit = l_payroll_deposit;
		}
		public List<Payroll> getL_payroll_zablok() {
			return l_payroll_zablok;
		}
		public void setL_payroll_zablok(List<Payroll> l_payroll_zablok) {
			this.l_payroll_zablok = l_payroll_zablok;
		}
		public List<Payroll> getL_payroll_avans_zapros() {
			return l_payroll_avans_zapros;
		}
		public void setL_payroll_avans_zapros(List<Payroll> l_payroll_avans_zapros) {
			this.l_payroll_avans_zapros = l_payroll_avans_zapros;
		}
		public List<Payroll> getL_payroll_avans_odob() {
			return l_payroll_avans_odob;
		}
		public void setL_payroll_avans_odob(List<Payroll> l_payroll_avans_odob) {
			this.l_payroll_avans_odob = l_payroll_avans_odob;
		}
		public List<Payroll> getL_payroll_dolg() {
			return l_payroll_dolg;
		}
		public void setL_payroll_dolg(List<Payroll> l_payroll_dolg) {
			this.l_payroll_dolg = l_payroll_dolg;
		}
		
	}
	
  	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
	
	
	


	
	//************************Variables local***************************
	
	//******************************************************************

	

	

	//********************************************************************************************
	//************************Save Bonus*****************************


	
	
	
	
	
	public void addMessage(String summary, String detail) {
	   
       FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
       FacesContext.getCurrentInstance().addMessage(null, message);
       //System.out.println(message.getDetail());
       RequestContext reqCtx = RequestContext.getCurrentInstance();
       reqCtx.update("form:messages");
   }

	//*****************************************************************	
	// **************************Employee*******************************
	
	private Staff selectedStaff = new Staff();
	public Staff getSelectedStaff() {
		return selectedStaff;
	}
	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	
	
	private DetailSearchTable p_detailSearchTable = new DetailSearchTable();	
	public DetailSearchTable getP_detailSearchTable() {
		return p_detailSearchTable;
	}
	public void setP_detailSearchTable(DetailSearchTable p_detailSearchTable) {
		this.p_detailSearchTable = p_detailSearchTable;
	}

	public class DetailSearchTable {
		public DetailSearchTable()
		{
			
		};
		private String bukrs = "";
		private Long branch_id;
		private Long staff_id;
		private String waers;
		private java.util.Date begDate = new  java.util.Date();
		private java.util.Date endDate = new  java.util.Date();
		private int detailType;
		public int getDetailType() {
			return detailType;
		}
		public void setDetailType(int detailType) {
			this.detailType = detailType;
		}
		
		public java.util.Date getBegDate() {
			return begDate;
		}

		public void setBegDate(java.util.Date begDate) {
			this.begDate = begDate;
		}

		public java.util.Date getEndDate() {
			return endDate;
		}

		public void setEndDate(java.util.Date endDate) {
			this.endDate = endDate;
		}
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
			 
		
	}
	
	
	private double total_detail = 0;
	
	public double getTotal_detail() {
		return total_detail;
	}

	public void setTotal_detail(double total_detail) {
		this.total_detail = total_detail;
	}
	private List<Payroll> detailedInfo = new ArrayList<Payroll>();

	public void getDatiledInfo()
	{
		try
		{
			if (selectedStaff!=null && selectedStaff.getStaff_id()!=null && canRead())
			{
				total_detail = 0;
				detailedInfo = new ArrayList<Payroll>();
				List<Long> l_staff_id = new ArrayList<Long>();
				l_staff_id.add(selectedStaff.getStaff_id());
				Calendar firstDay = Calendar.getInstance(); 
				Calendar lastDay = Calendar.getInstance();			  
				firstDay.setTime(p_detailSearchTable.getBegDate());
				lastDay.setTime(p_detailSearchTable.getEndDate());
				PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
				detailedInfo = payrollDao.findByBukrsBranchAllPeriod(l_staff_id,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),
						p_detailSearchTable.getBukrs(),p_detailSearchTable.getDetailType(),p_detailSearchTable.getWaers());
//				if (p_detailSearchTable.getDetailType()==1) detailedInfo = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
//				else if (p_detailSearchTable.getDetailType()==2) detailedInfo = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
//				else if (p_detailSearchTable.getDetailType()==3) detailedInfo = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
//				else if (p_detailSearchTable.getDetailType()==4) detailedInfo = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
//				else if (p_detailSearchTable.getDetailType()==5) detailedInfo = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
//				
				for (Payroll wa_prl:detailedInfo)
				{
					if (wa_prl.getDrcrk().equals("H") && (p_detailSearchTable.getDetailType()==1 || p_detailSearchTable.getDetailType()==2 || p_detailSearchTable.getDetailType()==3))
					{
						wa_prl.setDmbtr(wa_prl.getDmbtr()*-1);
					}
					
					if (wa_prl.getDrcrk().equals("S") && (p_detailSearchTable.getDetailType()==4 || p_detailSearchTable.getDetailType()==5))
					{
						wa_prl.setDmbtr(wa_prl.getDmbtr()*-1);
					}
					
					total_detail = total_detail + wa_prl.getDmbtr();
				}
				//p_searchTable.setBukrs(bukrs);
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("empTabs:formHrPayroll:detInfoTable");
			}
		} catch (DAOException ex) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");
			addMessage("Info", ex.getMessage());
		}
		
	}
	public void initDetailSearchTable()
	{
		try
		{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 1);
			lastDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			p_detailSearchTable.setBegDate(firstDay.getTime());
			p_detailSearchTable.setEndDate(lastDay.getTime());
			p_detailSearchTable.setDetailType(1);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:p_detailType");
			reqCtx.update("form:begDate");
			reqCtx.update("form:endDate");
			
			getDatiledInfo();
			
		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
		}
		
	}
	public List<Payroll> getDetailedInfo() {
		return detailedInfo;
	}

	public void setDetailedInfo(List<Payroll> detailedInfo) {
		this.detailedInfo = detailedInfo;
	}
	
	public void downloadExcel() {
        try {
//        	if (detailedInfo.size()>0)
//        	{	
        	Long a_rep_id=44L;
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(2);
            HSSFCell r2c1 = row2.getCell(1); HSSFCellStyle st1 = r2c1.getCellStyle();
        	HSSFCell r2c2 = row2.getCell(2); HSSFCellStyle st2 = r2c2.getCellStyle();
        	HSSFCell r2c3 = row2.getCell(3); HSSFCellStyle st3 = r2c3.getCellStyle();
        	HSSFCell r2c4 = row2.getCell(4); HSSFCellStyle st4 = r2c4.getCellStyle();
        	HSSFCell r2c5 = row2.getCell(5); HSSFCellStyle st5 = r2c5.getCellStyle();
        	HSSFCell r2c6 = row2.getCell(6); HSSFCellStyle st6 = r2c6.getCellStyle();
        	
        	
            int rowNum = 2;
            for (Payroll wa_prl:detailedInfo)
			{
            	HSSFRow row = sheet.getRow(rowNum);
            	if (row==null)
            	{
            		sheet.createRow(rowNum);
            		row = sheet.getRow(rowNum);
            	}
            	HSSFCell cell1 = row.createCell(1); cell1.setCellStyle(st1);
            	HSSFCell cell2 = row.createCell(2); cell2.setCellStyle(st2);
            	HSSFCell cell3 = row.createCell(3); cell3.setCellStyle(st3);
            	HSSFCell cell4 = row.createCell(4); cell4.setCellStyle(st4);
            	HSSFCell cell5 = row.createCell(5); cell5.setCellStyle(st5); 
            	HSSFCell cell6 = row.createCell(6); cell6.setCellStyle(st6);
            	
            	if (wa_prl.getPayroll_date()!=null) cell1.setCellValue(wa_prl.getPayroll_date());
            	if (wa_prl.getBldat()!=null) cell2.setCellValue(wa_prl.getBldat());
            	if (wa_prl.getContract_number()!=null) cell3.setCellValue(String.valueOf(wa_prl.getContract_number()));
            	if (wa_prl.getWaers()!=null) cell4.setCellValue(wa_prl.getWaers());
            	cell5.setCellValue(wa_prl.getDmbtr());
            	if (wa_prl.getText45()!=null) cell6.setCellValue(wa_prl.getText45());
            	
            	rowNum++;
            	 
				
				
			}
            HSSFRow row = sheet.createRow(rowNum);
        	
        	HSSFCell cell5 = row.createCell(5);
        	cell5.setCellValue(total_detail);
                 
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
