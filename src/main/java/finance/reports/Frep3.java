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

 




import java.util.ArrayList;
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

@ManagedBean(name = "frep3Bean", eager = true)
@ViewScoped
public class Frep3 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP3";
	private final static Long transaction_id = (long) 373;
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
			//Calendar curDate = Calendar.getInstance();
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Frep3.transaction_id);
			
			//p_searchTable.setBudat_to(curDate.getTime());
			
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
  				dynamicWhereClause = dynamicWhereClause + " and b.brnch in "+br_list;
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			
  			if (p_searchTable.bldat_from==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(13L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and b.bldat between '"+GeneralUtil.getSQLDate(p_searchTable.bldat_from)+"' ";
	  			
	  		
	  		if (p_searchTable.bldat_to==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(14L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and '"+GeneralUtil.getSQLDate(p_searchTable.bldat_to)+"' ";
  			
	  		
  			RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolDao.dynamicFrep3Result(dynamicWhereClause, userData.getU_language());
  			for (Object[] wa_result:results)
  			{
  				OutputTable wa_out = new OutputTable();
  				//a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s.text45 as hname
  				if (wa_result[0]!=null) wa_out.setWaers(String.valueOf(wa_result[0]));
  				if (wa_result[1]!=null) wa_out.setBranch_id(Long.parseLong(String.valueOf(wa_result[1])));
  				if (wa_result[2]!=null) wa_out.setHkont(String.valueOf(wa_result[2]));
  				if (wa_result[3]!=null) wa_out.setDmbtr(Double.parseDouble(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setWrbtr(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setBranchName(String.valueOf(wa_result[5]));
  				if (wa_result[6]!=null) wa_out.setHkontName(String.valueOf(wa_result[6]));
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
		private Long branch_id = 0L;
		private Date budat_from;
		private Date budat_to;
		private Date bldat_from;
		private Date bldat_to;
		public Date getBudat_from() {
			return budat_from;
		}
		public void setBudat_from(Date budat_from) {
			this.budat_from = budat_from;
		}
		public Date getBudat_to() {
			return budat_to;
		}
		public void setBudat_to(Date budat_to) {
			this.budat_to = budat_to;
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
		
		public Date getBldat_from() {
			return bldat_from;
		}
		public void setBldat_from(Date bldat_from) {
			this.bldat_from = bldat_from;
		}
		public Date getBldat_to() {
			return bldat_to;
		}
		public void setBldat_to(Date bldat_to) {
			this.bldat_to = bldat_to;
		}
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private String bukrs = "";
		private Long branch_id = 0L;
		private String waers;
		private String hkont;
		private double dmbtr;
		private double wrbtr;
		private String hkontName;
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
		public String getHkont() {
			return hkont;
		}
		public void setHkont(String hkont) {
			this.hkont = hkont;
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
		public String getHkontName() {
			return hkontName;
		}
		public void setHkontName(String hkontName) {
			this.hkontName = hkontName;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
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
		private String hkont;
		private double dmbtr;
		private double wrbtr;
		private String hkontName;
		private String branchName;
		private String cusName = "";
		
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
		public String getHkont() {
			return hkont;
		}
		public void setHkont(String hkont) {
			this.hkont = hkont;
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
		public String getHkontName() {
			return hkontName;
		}
		public void setHkontName(String hkontName) {
			this.hkontName = hkontName;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public String getCusName() {
			return cusName;
		}
		public void setCusName(String cusName) {
			this.cusName = cusName;
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
                	if (i2==5||i2==4)
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
                	if (i2==5||i2==6)
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
	public void getDetail(Long a_branch_id,String a_waers,String a_hkont)
	{
		try 
		{
			detailDmbtrTotal = 0;
			detailWrbtrTotal = 0;
			l_detailTable.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";

  			dynamicWhereClause = dynamicWhereClause + " and b.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			dynamicWhereClause = dynamicWhereClause + " and b.brnch in ("+a_branch_id+") ";
  			dynamicWhereClause = dynamicWhereClause + " and b.waers = '"+a_waers+"' ";
  			dynamicWhereClause = dynamicWhereClause + " and bsr.hkont = '"+a_hkont+"' ";
  			dynamicWhereClause = dynamicWhereClause + " and b.bldat between '"+GeneralUtil.getSQLDate(p_searchTable.bldat_from)+"' and '"+GeneralUtil.getSQLDate(p_searchTable.bldat_to)+"' ";
  				
  			
  			
  			
	  		
  			RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolDao.dynamicFrep3Detail(dynamicWhereClause, userData.getU_language());
  			for (Object[] wa_result:results)
  			{
  				DetailTable wa_out = new DetailTable();
  				//b.waers,bsr.hkont,bsr.dmbtr,bsr.wrbtr,br.text45 as bname,s.text45 as hname,b.bukrs,b.belnr,b.gjahr
  				if (wa_result[0]!=null) wa_out.setWaers(String.valueOf(wa_result[0]));  				
  				if (wa_result[1]!=null) wa_out.setHkont(String.valueOf(wa_result[1]));
  				if (wa_result[2]!=null) wa_out.setDmbtr(Double.parseDouble(String.valueOf(wa_result[2])));
  				if (wa_result[3]!=null) wa_out.setWrbtr(Double.parseDouble(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setBranchName(String.valueOf(wa_result[4]));
  				if (wa_result[5]!=null) wa_out.setHkontName(String.valueOf(wa_result[5]));
  				if (wa_result[6]!=null) wa_out.setBukrs(String.valueOf(wa_result[6]));
  				if (wa_result[7]!=null) wa_out.setBelnr(Long.parseLong(String.valueOf(wa_result[7])));
  				if (wa_result[8]!=null) wa_out.setGjahr(Integer.parseInt(String.valueOf(wa_result[8])));
  				
  				if (wa_result[10]!=null) wa_out.setCusName(String.valueOf(wa_result[10]));
  				l_detailTable.add(wa_out);
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
