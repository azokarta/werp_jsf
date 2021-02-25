package service.applications;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.ServiceApplicationDao;
import general.tables.Branch;
import java.io.Serializable;

 





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

import org.primefaces.context.RequestContext;
 









import user.User;

@ManagedBean(name = "serrep1Bean", eager = true)
@ViewScoped
public class Serrep1 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "SERREP1";
	private final static Long transaction_id = (long) 657;
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
			PermissionController.canRead(userData,Serrep1.transaction_id);
			Calendar curDate = Calendar.getInstance();
			//GeneralUtil.getCurrentMonthFirstDay().getTime();
			//GeneralUtil.getCurrentMonthLastDay().getTime();
			p_searchTable.setBudat_from(curDate.getTime());
			p_searchTable.setBudat_to(curDate.getTime());

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
  			totalKol = 0;
  			l_outputTable.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			
  			
  			
  			
  			
  			
//  			p_searchTable.setBudat_from(date_from.getTime());
//  			p_searchTable.setBudat_to(date_to.getTime());
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and sa.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException("Выберите компанию");
  			
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
  				dynamicWhereClause = dynamicWhereClause + " and sa.branch_id in "+br_list;
  			}
  			else
  				throw new DAOException("Выберите филиал");
  			
  			if (p_searchTable.budat_from==null)
	  		{
	  			throw new DAOException("Выберите дату с");
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and sa.adate between '"+GeneralUtil.getSQLDate(p_searchTable.budat_from)+"' ";
	  			
	  		
	  		if (p_searchTable.budat_to==null)
	  		{
	  			throw new DAOException("Выберите дату по");
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and '"+GeneralUtil.getSQLDate(p_searchTable.budat_to)+"' ";

	  		ServiceApplicationDao servAppDao = (ServiceApplicationDao) appContext.getContext().getBean("servAppDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = servAppDao.dynamicSerrep1(dynamicWhereClause);
  			for (Object[] wa_result:results)
  			{
  				OutputTable wa_out = new OutputTable();
//				sa.created_by,initcap(st.lastname)||' '||initcap(st.firstname) fio,count(sa.id) kol,sa.app_type,sa.app_status
  				if (wa_result[1]!=null) wa_out.setFio(String.valueOf(wa_result[1]));
  				if (wa_result[2]!=null) wa_out.setKol(Integer.parseInt(String.valueOf(wa_result[2])));
  				if (wa_result[3]!=null) wa_out.setApp_type_id(Integer.parseInt(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setApp_status_id(Integer.parseInt(String.valueOf(wa_result[4])));
  				totalKol = totalKol + wa_out.getKol();
//  				if (wa_result[0]!=null) wa_out.setWaers(String.valueOf(wa_result[0]));
//  				if (wa_result[1]!=null) wa_out.setSumma(Double.parseDouble(String.valueOf(wa_result[1])));
//  				if (wa_result[2]!=null) wa_out.setBranchName(String.valueOf(wa_result[2]));
//  				
//  				if (wa_result[3]!=null) wa_out.setSn(Long.parseLong(String.valueOf(wa_result[3])));
//  				if (wa_result[4]!=null) wa_out.setOld_sn(Long.parseLong(String.valueOf(wa_result[4])));
//  				if (wa_result[5]!=null) wa_out.setFio(String.valueOf(wa_result[5]));
//  				if (wa_result[6]!=null)
//					try {
//						wa_out.setBudat(formatter.parse(String.valueOf(wa_result[6])));
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//  				if (wa_result[7]!=null) wa_out.setRemain(Double.parseDouble(String.valueOf(wa_result[7])));
//  				if (wa_result[8]!=null) wa_out.setBelnr(Long.parseLong(String.valueOf(wa_result[8])));
//  				if (wa_result[9]!=null) wa_out.setGjahr(Integer.parseInt(String.valueOf(wa_result[9])));
//  				if (wa_result[10]!=null) wa_out.setBlart(String.valueOf(wa_result[10]));
//  				if (wa_result[11]!=null) wa_out.setBukrs(String.valueOf(wa_result[11]));
//  				if (wa_result[12]!=null) wa_out.setCustomer_id(Long.parseLong(String.valueOf(wa_result[12])));
  				
  				l_outputTable.add(wa_out);
  				
  			}
  			
  			
			
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

	private int totalKol = 0;
	public int getTotalKol() {
		return totalKol;
	}
	public void setTotalKol(int totalKol) {
		this.totalKol = totalKol;
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
		
		
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private String branchName;
		private String fio;
		private int app_type_id;
		private int app_status_id;
		private int kol;
		private String app_type;
		private String app_status;
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public String getFio() {
			return fio;
		}
		public void setFio(String fio) {
			this.fio = fio;
		}
		public int getApp_type_id() {			
			return app_type_id;
		}
		public void setApp_type_id(int app_type_id) {
			//1 - CEBSERVICE, 2 - CEBFILTERS, 3 - ROBSERVICE, 4 - COMPLAINT, 5 - OTHER
			if (app_type_id==1) this.app_type = "CEBILON - Сервис";
			else if (app_type_id==3) this.app_type = "ROBOCLEAN - Сервис";
			else if (app_type_id==4) this.app_type = "Жалоба / Пожелание";	
			else if (app_type_id==5) this.app_type = "Другое";
			else if (app_type_id==6) this.app_type = "ROBOCLEAN - Профилактика";
			else if (app_type_id==7) this.app_type = "CEBILON - Замена фильтров";
			
			this.app_type_id = app_type_id;
		}
		public int getApp_status_id() {
			return app_status_id;
		}
		public void setApp_status_id(int app_status_id) {
			// app_status: 1 - NEW, 2 - OPERATING, 3 - PROCESSED, 4 - CANCELLED 
			if (app_status_id==1) this.app_status = "Новая";
			else if (app_status_id==2) this.app_status = "В обработке";
			else if (app_status_id==3) this.app_status = "Выполнена";
			else if (app_status_id==4) this.app_status = "Отменена";	

			this.app_status_id = app_status_id;
		}
		public String getApp_type() {
			return app_type;
		}
		public void setApp_type(String app_type) {
			this.app_type = app_type;
		}
		public String getApp_status() {
			return app_status;
		}
		public void setApp_status(String app_status) {
			this.app_status = app_status;
		}
		public int getKol() {
			return kol;
		}
		public void setKol(int kol) {
			this.kol = kol;
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
				
				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(5L))
				{
					p_searchTable.getL_branch().add(wa);
					p_searchTable.getSelectedBranches().add(String.valueOf(wa.getBranch_id()));
					
				}
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
	
	
	
	

	
	//*****************************************************************************************************
		
}
