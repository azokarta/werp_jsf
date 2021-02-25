package finance.reports;

import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.GeneralUtil;
import general.PermissionController;
import general.dao.BsegDao;
import general.dao.DAOException; 
import general.output.tables.Frep1OutputTable;

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

@ManagedBean(name = "frep1Bean", eager = true)
@ViewScoped
public class Frep1 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP1";
	private final static Long transaction_id = (long) 315;
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
			PermissionController.canRead(userData,Frep1.transaction_id);
			

			
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
	
	private double totremain=0;
	private double totin=0;
	private double totout=0;
	
	
	
  	public double getTotremain() {
		return totremain;
	}
	public void setTotremain(double totremain) {
		this.totremain = totremain;
	}
	public double getTotin() {
		return totin;
	}
	public void setTotin(double totin) {
		this.totin = totin;
	}
	public double getTotout() {
		return totout;
	}
	public void setTotout(double totout) {
		this.totout = totout;
	}
	public void to_search()
  	{
  		try 
		{
  			l_outputTable.clear();
  			totremain=0;
  			totin=0;
  			totout=0;
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and br.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException("Выберите компанию");
  			
  			if (p_searchTable.getBranch_id()!=null && !p_searchTable.getBranch_id().equals(0L))
  			{
  				if (!p_searchTable.getBranch_id().equals(-1L))
  				{
  					dynamicWhereClause = dynamicWhereClause + " and br.branch_id = "+p_searchTable.getBranch_id();
  				}
  				
  			}
  			else
  				throw new DAOException("Выберите филиал");
  			
  			if (p_searchTable.getWaers()!=null && !p_searchTable.getWaers().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and s.waers = '"+p_searchTable.getWaers()+"' ";
  			}
  			else
  				throw new DAOException("Выберите валюту");
  			
  			if (p_searchTable.getS_date_from()==null)
  			{
  				throw new DAOException("Выберите дату документа с");
  			}
  			else dynamicWhereClause = dynamicWhereClause + " and b.budat >= '"+GeneralUtil.getSQLDate(p_searchTable.getS_date_from())+"' ";
  				
  			
  			if (p_searchTable.getS_date_to()==null)
  			{
  				throw new DAOException("Выберите дату документа по");
  			}
  			else dynamicWhereClause = dynamicWhereClause + " and b.budat <= '"+GeneralUtil.getSQLDate(p_searchTable.getS_date_to())+"' ";

  			//System.out.println(dynamicWhereClause);
  			BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
  			l_outputTable = bsegDao.dynamicFindFrep1(dynamicWhereClause);
  			if (l_outputTable.size()==0)
  			{
  				throw new DAOException("Не найдено");
  			}
  			
  			for(Frep1OutputTable wa_rt:l_outputTable)
  			{
  				
  				if (wa_rt.getSumma()>0) totin = totin + wa_rt.getSumma();
  				else totout = totout + wa_rt.getSumma();  				
  			}
  			
  			totremain = totin + totout;
  			
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
		private Long branch_id = 0L;
		private String waers = "";
		private Date s_date_from;
		private Date s_date_to;
		public Date getS_date_from() {
			return s_date_from;
		}
		public void setS_date_from(Date s_date_from) {
			this.s_date_from = s_date_from;
		}
		public Date getS_date_to() {
			return s_date_to;
		}
		public void setS_date_to(Date s_date_to) {
			this.s_date_to = s_date_to;
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
	


	
	
	private List<Frep1OutputTable> l_outputTable = new ArrayList<Frep1OutputTable>();
	
	public List<Frep1OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<Frep1OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}

	public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);
         
        HSSFCellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
         
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
    }
	

	//*****************************************************************************************************
		
}
