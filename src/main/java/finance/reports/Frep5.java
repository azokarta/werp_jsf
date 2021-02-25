package finance.reports;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.RfcolDao;
import general.tables.Branch;

import java.io.Serializable;

 






import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;
 









import user.User;

@ManagedBean(name = "frep5Bean", eager = true)
@ViewScoped
public class Frep5 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP5";
	private final static Long transaction_id = (long) 377;
	public static Long getTransactionId() {
		return transaction_id;
	}
	
	
	
	//***************************Application Context***************************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
	  return appContext;
	}
	public void setAppContext(AppContext appContext) {
	  this.appContext = appContext;
	}
	//*************************************************************************
	//**********************Getter Setter for sessionData**********************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
	  return userData;
	}
	public void setUserData(User userData) {
	  this.userData = userData;
	}
	//*************************************************************************
	 //*****************************Branch**********************************
  	@ManagedProperty(value="#{branchF4Bean}")
  	private BranchF4 p_branchF4Bean;
  	public void setP_branchF4Bean(BranchF4 p_branchF4) {
  	      this.p_branchF4Bean = p_branchF4;
  	}
  	public BranchF4 getP_branchF4Bean() {
		  return p_branchF4Bean;
	}
	 //*****************************Branch**********************************
  	@ManagedProperty(value="#{exchangeRateF4Bean}")
  	private ExchangeRateF4 p_exchangeRateF4Bean;
  	
	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}
	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{ 
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Frep5.transaction_id);
			p_searchTable.getL_type().add("0");
			
			Calendar curDate = Calendar.getInstance();
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 1);
			lastDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			p_searchTable.setCon_date_from(firstDay.getTime());
			p_searchTable.setCon_date_to(lastDay.getTime());
//			List<String> items = new ArrayList<>();
//			items.add("A");
//			items.add("B");
//			items.add("C");
//			items.add("D");
//			items.add("E");
//
//			//lambda
//			//Output : A,B,C,D,E
//			items.forEach(item->System.out.println(item));
//
//			//Output : C
//			items.forEach(item->{
//				if("C".equals(item)){
//					System.out.println(item);
//				}
//			});
//
//			//method reference
//			//Output : A,B,C,D,E
//			items.forEach(System.out::println);
//
//			//Stream and filter
//			//Output : B
//			items.stream()
//				.filter(s->s.contains("B"))
//				.forEach(System.out::println);
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	

	//*************************************************************************
	
	
  	//*******************************************************************************************
	
    //***********************************Others***********************************************************

  	public void toMainPage() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath()  + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");	
    } 	
	
	//*****************************************************************	
	//****************************Search Parameters**********************************
  	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
	

	
  	public void to_search()
  	{
  		try 
		{
  			l_outputTable.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			
  			
  			
  			
  			//date_to.setTime(GeneralUtil.getLastDayOfMonth(p_searchTable.getP_year(), p_searchTable.getP_month()).getTime());
  			
//  			p_searchTable.setBudat_from(date_from.getTime());
//  			p_searchTable.setBudat_to(date_to.getTime());
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and b.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  			
  			if (p_searchTable.getSelectedBranches().size()!=0)
  			{
  				int count=0;
  				String br_list = "(";
  				for(String a_br_id:p_searchTable.getSelectedBranches())
  				{
  					count++;
  					if (count==1) br_list = br_list+a_br_id;
  					else br_list = br_list+", "+a_br_id;	
  				}
  				br_list = br_list+")";
  				dynamicWhereClause = dynamicWhereClause + " and c.branch_id in "+br_list;
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			
  			if (p_searchTable.con_date_from==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(13L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and c.contract_date between '"+GeneralUtil.getSQLDate(p_searchTable.con_date_from)+"' ";
	  			
	  		
	  		if (p_searchTable.con_date_to==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(14L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and '"+GeneralUtil.getSQLDate(p_searchTable.con_date_to)+"' ";
	  		
	  		
	  		if (p_searchTable.getL_type().size()!=0)
  			{
  				int count=0;
  				String l_type = "(";
  				for(String wa:p_searchTable.getL_type())
  				{
  					count++;
  					if (count==1) l_type = l_type+wa;
  					else l_type = l_type+", "+wa;	
  				}
  				l_type = l_type+")";
  				dynamicWhereClause = dynamicWhereClause + " and c.MARKED_TYPE in "+l_type;
  			}
  			
	  		
  			RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolDao.dynamicFrep5Result(dynamicWhereClause);
  			for (Object[] wa_result:results)
  			{
//  				br.text45,a2.* from "
//  						+" ("
//  						+" select a1.* "
//  						+" ,rownum rsort from ("
//  						+" select c.branch_id,"
//  						+" c.waers,"
//  						+" sum(CASE WHEN c.payment_schedule < 2 THEN 1 ELSE 0 END) nal_kol,"
//  						+" sum(CASE WHEN c.payment_schedule < 2 THEN dmbtr ELSE 0 END) nal_dmbtr,"
//  						+" sum(CASE WHEN c.payment_schedule < 2 THEN wrbtr ELSE 0 END) nal_wrbtr,"
//  						+" sum(CASE WHEN c.payment_schedule > 1 THEN 1 ELSE 0 END) ras_kol,"
//  						+" sum(CASE WHEN c.payment_schedule > 1 THEN dmbtr ELSE 0 END) ras_dmbtr,"
//  						+" sum(CASE WHEN c.payment_schedule > 1 THEN wrbtr ELSE 0 END) ras_wrbtr,"
//  						+" sum(1) tot_kolm,"
//  						+" sum(dmbtr) tot_dmbtr,"
//  						+" sum(wrbtr) tot_wrbtr"
  				OutputTable wa_out = new OutputTable();
  				//a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s.text45 as hname
  				if (wa_result[0]!=null) wa_out.setBranchName(String.valueOf(wa_result[0]));
  				if (wa_result[1]!=null) wa_out.setBranch_id(Long.parseLong(String.valueOf(wa_result[1])));
  				if (wa_result[2]!=null) wa_out.setWaers(String.valueOf(wa_result[2]));
  				
  				if (wa_result[3]!=null) wa_out.setNal_kol(Integer.parseInt(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setNal_dmbtr(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setNal_wrbtr(Double.parseDouble(String.valueOf(wa_result[5])));
  				
  				if (wa_result[6]!=null) wa_out.setRas_kol(Integer.parseInt(String.valueOf(wa_result[6])));
  				if (wa_result[7]!=null) wa_out.setRas_dmbtr(Double.parseDouble(String.valueOf(wa_result[7])));
  				if (wa_result[8]!=null) wa_out.setRas_wrbtr(Double.parseDouble(String.valueOf(wa_result[8])));
  				
  				if (wa_result[9]!=null) wa_out.setTot_kol(Integer.parseInt(String.valueOf(wa_result[9])));
  				if (wa_result[10]!=null) wa_out.setTot_dmbtr(Double.parseDouble(String.valueOf(wa_result[10])));
  				if (wa_result[11]!=null) wa_out.setTot_wrbtr(Double.parseDouble(String.valueOf(wa_result[11])));
  				
  				l_outputTable.add(wa_out);
  				
  			}
//  			l_outputTable = rfcolService.search(p_searchTable.getBukrs(),p_searchTable.getP_year(),p_searchTable.getP_month(),
//  					dynamicWhereClause,  GeneralUtil.getSQLDate(cal_budat_from), GeneralUtil.getSQLDate(cal_budat_to),1,p_searchTable.getP_status());
  			

  			
  			//l_outputTable = bkpfDao.findResultTableRfdocrow(dynamicWhereClause,true);
  			
  			
  			
  			
			
  			tabindex = 1;
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");				
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
  	}
  	//*******************************************************************************
	//******************************************************************
	

	
	//private List<bsid> l_bsid2 = new ArrayList<bsid>();

	
	
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}

	
	private SearchTable p_searchTable = new SearchTable();
	public SearchTable getP_searchTable() {
		return p_searchTable;
	}
	public void setP_searchTable(SearchTable p_searchTable) {
		this.p_searchTable = p_searchTable;
	}

	
	
	public class SearchTable {
		public SearchTable()
		{
			
		};
		private String bukrs = "";
		private List<Branch> l_branch = new ArrayList<Branch>();
		private List<String> selectedBranches = new ArrayList<String>();
		private List<String> l_type = new ArrayList<String>();
		
		private Long branch_id = 0L;
		private Date con_date_from;
		private Date con_date_to;
		
		
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
		public List<Branch> getL_branch() {
			return l_branch;
		}
		public void setL_branch(List<Branch> l_branch) {
			this.l_branch = l_branch;
		}
		public List<String> getSelectedBranches() {
			return selectedBranches;
		}
		public void setSelectedBranches(List<String> selectedBranches) {
			this.selectedBranches = selectedBranches;
		}
		
		
		public Date getCon_date_from() {
			return con_date_from;
		}
		public void setCon_date_from(Date con_date_from) {
			this.con_date_from = con_date_from;
		}
		public Date getCon_date_to() {
			return con_date_to;
		}
		public void setCon_date_to(Date con_date_to) {
			this.con_date_to = con_date_to;
		}
		public List<String> getL_type() {
			return l_type;
		}
		public void setL_type(List<String> l_type) {
			this.l_type = l_type;
		}
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private String bukrs = "";
		private Long branch_id = 0L;
		private String waers;
		private int nal_kol;
		private double nal_dmbtr;
		private double nal_wrbtr;
		private int ras_kol;
		private double ras_dmbtr;
		private double ras_wrbtr;
		private int tot_kol;
		private double tot_dmbtr;
		private double tot_wrbtr;
		private String branchName;
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
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public int getNal_kol() {
			return nal_kol;
		}
		public void setNal_kol(int nal_kol) {
			this.nal_kol = nal_kol;
		}
		public double getNal_dmbtr() {
			return nal_dmbtr;
		}
		public void setNal_dmbtr(double nal_dmbtr) {
			this.nal_dmbtr = nal_dmbtr;
		}
		public double getNal_wrbtr() {
			return nal_wrbtr;
		}
		public void setNal_wrbtr(double nal_wrbtr) {
			this.nal_wrbtr = nal_wrbtr;
		}
		public int getRas_kol() {
			return ras_kol;
		}
		public void setRas_kol(int ras_kol) {
			this.ras_kol = ras_kol;
		}
		public double getRas_dmbtr() {
			return ras_dmbtr;
		}
		public void setRas_dmbtr(double ras_dmbtr) {
			this.ras_dmbtr = ras_dmbtr;
		}
		public double getRas_wrbtr() {
			return ras_wrbtr;
		}
		public void setRas_wrbtr(double ras_wrbtr) {
			this.ras_wrbtr = ras_wrbtr;
		}
		public int getTot_kol() {
			return tot_kol;
		}
		public void setTot_kol(int tot_kol) {
			this.tot_kol = tot_kol;
		}
		public double getTot_dmbtr() {
			return tot_dmbtr;
		}
		public void setTot_dmbtr(double tot_dmbtr) {
			this.tot_dmbtr = tot_dmbtr;
		}
		public double getTot_wrbtr() {
			return tot_wrbtr;
		}
		public void setTot_wrbtr(double tot_wrbtr) {
			this.tot_wrbtr = tot_wrbtr;
		}
		
	}
	
	private List<DetailTable> l_detailTable = new ArrayList<DetailTable>();
	public List<DetailTable> getL_detailTable() {
		return l_detailTable;
	}
	public void setL_detailTable(List<DetailTable> l_detailTable) {
		this.l_detailTable = l_detailTable;
	}
	public class DetailTable {
		public DetailTable()
		{
			
		};
		private Long belnr = 0L;
		private int gjahr;
		private String bukrs;
		private String waers;
		private double dmbtr;
		private double wrbtr;
		private String branchName;
		private Long contract_number;
		private Long old_sn;
		private Date con_date;
		
		public Long getBelnr() {
			return belnr;
		}
		public void setBelnr(Long belnr) {
			this.belnr = belnr;
		}
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getDmbtr() {
			return dmbtr;
		}
		public void setDmbtr(double dmbtr) {
			this.dmbtr = dmbtr;
		}
		public double getWrbtr() {
			return wrbtr;
		}
		public void setWrbtr(double wrbtr) {
			this.wrbtr = wrbtr;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public Long getContract_number() {
			return contract_number;
		}
		public void setContract_number(Long contract_number) {
			this.contract_number = contract_number;
		}
		public Long getOld_sn() {
			return old_sn;
		}
		public void setOld_sn(Long old_sn) {
			this.old_sn = old_sn;
		}
		public Date getCon_date() {
			return con_date;
		}
		public void setCon_date(Date con_date) {
			this.con_date = con_date;
		}
		
		
	}
	
	public void updateBranch()
	{
		try{
			Long ba = 0L;
			Long br = 0L;
			p_searchTable.getL_branch().clear();
			for(Branch wa:p_branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(p_searchTable.bukrs,userData.getBukrs(),ba,ba, br,userData.getBranch_id()))
			{
				p_searchTable.getL_branch().add(wa);
//				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(1L) || wa.getBusiness_area_id().equals(2L) || wa.getBusiness_area_id().equals(3L) || wa.getBusiness_area_id().equals(4L))
//				{
//					p_searchTable.getL_branch().add(wa);
//				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView:p_branch_id");	
  			reqCtx.update("branchForm:branchFormTable");	
  			
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		} 
	}
	
	public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);
         
        HSSFCellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        
        HSSFCellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setDataFormat((short)2); 
        
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
        for(int i=0; i < sheet.getPhysicalNumberOfRows();i++) {
        	if (i>0)
        	{
        		HSSFRow row = sheet.getRow(i);
                for(int i2=0; i2 < row.getPhysicalNumberOfCells();i2++) {
                	HSSFCell cell = row.getCell(i2);
                	if (i2==3||i2==4 || i2==6||i2==7 || i2==9||i2==10)
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		String value = cell.getStringCellValue();
                		
                		value = value.replaceAll("\u00A0", "");
                		value = value.replace(",", ".");
                		double dvalue = Double.valueOf(value);
                		
                		value = value.replace("\\s","");
                		value = value.trim();
                		
                		cell.setCellValue(dvalue);
                	}
                	if (i2==6)
                	{                		
                		cell.setCellValue("");
                	}
                    
                }
        	}
            
        }
    }
	public void postProcessXLSDetail(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);
         
        HSSFCellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        
        HSSFCellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setDataFormat((short)2); 
        
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
        for(int i=0; i < sheet.getPhysicalNumberOfRows();i++) {
        	if (i>0)
        	{
        		HSSFRow row = sheet.getRow(i);
                for(int i2=0; i2 < row.getPhysicalNumberOfCells();i2++) {
                	HSSFCell cell = row.getCell(i2);
                	if (i2==3||i2==4)
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		String value = cell.getStringCellValue();
                		
                		value = value.replaceAll("\u00A0", "");
                		value = value.replace(",", ".");
                		double dvalue = Double.valueOf(value);
                		
                		value = value.replace("\\s","");
                		value = value.trim();
                		
                		cell.setCellValue(dvalue);
                	}
                    
                }
        	}
            
        }
    }
	private int detailKolTotal = 0;
	private double detailDmbtrTotal = 0;
	private double detailWrbtrTotal = 0;	
	public double getDetailDmbtrTotal() {
		return detailDmbtrTotal;
	}
	public void setDetailDmbtrTotal(double detailDmbtrTotal) {
		this.detailDmbtrTotal = detailDmbtrTotal;
	}
	public double getDetailWrbtrTotal() {
		return detailWrbtrTotal;
	}
	public void setDetailWrbtrTotal(double detailWrbtrTotal) {
		this.detailWrbtrTotal = detailWrbtrTotal;
	}
	public int getDetailKolTotal() {
		return detailKolTotal;
	}
	public void setDetailKolTotal(int detailKolTotal) {
		this.detailKolTotal = detailKolTotal;
	}
	public void getDetail(Long a_branch_id,String a_waers,int a_ps)
	{
		try 
		{
			detailDmbtrTotal = 0;
			detailWrbtrTotal = 0;
			detailKolTotal = 0;
			l_detailTable.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";

  			dynamicWhereClause = dynamicWhereClause + " and b.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			dynamicWhereClause = dynamicWhereClause + " and c.branch_id in ("+a_branch_id+") ";
  			dynamicWhereClause = dynamicWhereClause + " and c.waers = '"+a_waers+"' ";
  			if (a_ps==1) dynamicWhereClause = dynamicWhereClause + " and c.payment_schedule < 2";
  			else if (a_ps==2) dynamicWhereClause = dynamicWhereClause + " and c.payment_schedule > 1";
  			dynamicWhereClause = dynamicWhereClause + " and c.contract_date between '"+GeneralUtil.getSQLDate(p_searchTable.con_date_from)+"' and '"+GeneralUtil.getSQLDate(p_searchTable.con_date_to)+"' ";
  				
  			
  			if (p_searchTable.getL_type().size()!=0)
  			{
  				int count=0;
  				String l_type = "(";
  				for(String wa:p_searchTable.getL_type())
  				{
  					count++;
  					if (count==1) l_type = l_type+wa;
  					else l_type = l_type+", "+wa;	
  				}
  				l_type = l_type+")";
  				dynamicWhereClause = dynamicWhereClause + " and c.MARKED_TYPE in "+l_type;
  			}
  			
  			
  			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  			RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolDao.dynamicFrep5Detail(dynamicWhereClause);
  			for (Object[] wa_result:results)
  			{
  				DetailTable wa_out = new DetailTable();
//  				b.belnr,"
//						+" b.gjahr,"
//						+" b.bukrs,"
//						+" b.waers,"
//						+" b.dmbtr,"
//						+" b.wrbtr,"
//						+" br.text45,"
//						+" c.old_sn,"
//						+" c.contract_date,"
//						+" c.contract_number"
  				if (wa_result[0]!=null) wa_out.setBelnr(Long.parseLong(String.valueOf(wa_result[0])));
  				if (wa_result[1]!=null) wa_out.setGjahr(Integer.parseInt(String.valueOf(wa_result[1])));
  				if (wa_result[2]!=null) wa_out.setBukrs(String.valueOf(wa_result[2]));  				
  				if (wa_result[3]!=null) wa_out.setWaers(String.valueOf(wa_result[3]));
  				if (wa_result[4]!=null) wa_out.setDmbtr(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setWrbtr(Double.parseDouble(String.valueOf(wa_result[5])));
  				if (wa_result[6]!=null) wa_out.setBranchName(String.valueOf(wa_result[6]));
  				if (wa_result[7]!=null) wa_out.setOld_sn(Long.parseLong(String.valueOf(wa_result[7])));
  				if (wa_result[8]!=null)
					try {
						wa_out.setCon_date(formatter.parse(String.valueOf(wa_result[8])));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
  				if (wa_result[9]!=null) wa_out.setContract_number(Long.parseLong(String.valueOf(wa_result[9])));
  				
  				
  				
  				
  				
  				//if (wa_result[10]!=null) wa_out.setBldat(wa_result[10]));
  				
  				l_detailTable.add(wa_out);
  				detailKolTotal = detailKolTotal + 1;
  				detailDmbtrTotal = detailDmbtrTotal+wa_out.getDmbtr();
  				detailWrbtrTotal = detailWrbtrTotal+wa_out.getWrbtr();
  				
  			}  	
  			tabindex = 2;
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");
//  			reqCtx.update("form:tabView:l_outputTable2");
		}
  		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
	}
	
	

	
	//*****************************************************************************************************
		
}
