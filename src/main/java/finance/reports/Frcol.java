package finance.reports;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException; 
import general.output.tables.RfcolResultTable;
import general.services.RfcolService;
import general.tables.Branch;

import java.io.Serializable;

 






import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@ManagedBean(name = "frcolBean", eager = true)
@ViewScoped
public class Frcol implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FRCOL";
	private final static Long transaction_id = (long) 255;
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
			PermissionController.canRead(userData,Frcol.transaction_id);
			
			Calendar curDate = Calendar.getInstance(); 
			//p_searchTable.setBudat_to(curDate.getTime());
			p_searchTable.setP_year(curDate.get(Calendar.YEAR));
			p_searchTable.setP_month(curDate.get(Calendar.MONTH)+1);
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
	public void showByCollectors(String a_branch_id, String a_waers)
  	{
  		try 
		{
  			l_outputTable2.clear();
  			l_map_collector.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";

  			dynamicWhereClause = dynamicWhereClause + " and br.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			dynamicWhereClause = dynamicWhereClause + " and br.branch_id in ("+a_branch_id+") ";
  			dynamicWhereClause = dynamicWhereClause + " and waers = '"+a_waers+"' ";
  			Calendar cal_budat_from = Calendar.getInstance();
  			Calendar cal_budat_to = Calendar.getInstance();
  			cal_budat_from.setTime(p_searchTable.budat_from);
  			cal_budat_to.setTime(p_searchTable.budat_to);

  			//System.out.println(dynamicWhereClause);
  			//RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			RfcolService rfcolService = (RfcolService) appContext.getContext().getBean("rfcolService");
  			//l_outputTable2 = rfcolDao.dynamicFindCollectorStat(dynamicWhereClause,  new java.sql.Date(cal_budat_from.getTimeInMillis()),  new java.sql.Date(cal_budat_to.getTimeInMillis()),2,p_searchTable.getP_status());
  			l_outputTable2 = rfcolService.search(p_searchTable.getBukrs(),p_searchTable.getP_year(),p_searchTable.getP_month(),
  					dynamicWhereClause,  GeneralUtil.getSQLDate(cal_budat_from), GeneralUtil.getSQLDate(cal_budat_to),2,p_searchTable.getP_status());
  			for(RfcolResultTable wa_rt:l_outputTable2)
  			{
  				if (wa_rt.getWaers()!=null && !wa_rt.getWaers().equals("USD"))
  				{
  					double sec_cur = p_exchangeRateF4Bean.getL_er_map_national().get("1"+wa_rt.getWaers()).getSc_value();
  					wa_rt.setRas_usd_plan(wa_rt.getRas_plan()/sec_cur);
  					wa_rt.setRas_usd_poluchen(wa_rt.getRas_poluchen()/sec_cur);
  					wa_rt.setOne_month_usd_plan(wa_rt.getOne_month_plan()/sec_cur);
  					wa_rt.setOne_month_usd_poluchen(wa_rt.getOne_month_poluchen()/sec_cur);
  				}
  				else
  				{
  					wa_rt.setRas_usd_plan(wa_rt.getRas_plan());
  					wa_rt.setRas_usd_poluchen(wa_rt.getRas_poluchen());
  					wa_rt.setOne_month_usd_plan(wa_rt.getOne_month_plan());
  					wa_rt.setOne_month_usd_poluchen(wa_rt.getOne_month_poluchen());
  				}
  				
  				if (wa_rt.getRas_plan()>0)
				{
					wa_rt.setRas_percentage(GeneralUtil.round(wa_rt.getRas_poluchen()/wa_rt.getRas_plan()*100, 2));
				}
  				if (wa_rt.getOne_month_plan()>0)
				{
					wa_rt.setOne_month_percentage(GeneralUtil.round(wa_rt.getOne_month_poluchen()/wa_rt.getOne_month_plan()*100, 2));
				}
  				
  				TotalTable wa_tt = l_map_collector.get(wa_rt.getWaers());
  				if (wa_tt==null)
  				{
  					wa_tt = new TotalTable();
  					wa_tt.setContract_amount(wa_rt.getContract_amount());
  					wa_tt.setWaers(wa_rt.getWaers());
  					wa_tt.setRas_plan(wa_rt.getRas_plan());
  					wa_tt.setRas_poluchen(wa_rt.getRas_poluchen());
  					wa_tt.setOne_month_plan(wa_rt.getOne_month_plan());
  					wa_tt.setOne_month_poluchen(wa_rt.getOne_month_poluchen());
  					wa_tt.setRas_usd_plan(wa_rt.getRas_usd_plan());
  					wa_tt.setRas_usd_poluchen(wa_rt.getRas_usd_poluchen());
  					wa_tt.setOne_month_usd_plan(wa_rt.getOne_month_usd_plan());
  					wa_tt.setOne_month_usd_poluchen(wa_rt.getOne_month_usd_poluchen());
  					l_map_collector.put(wa_rt.getWaers(), wa_tt);
  				}
  				else
  				{
  					wa_tt.setContract_amount(wa_tt.getContract_amount()+wa_rt.getContract_amount());
  					wa_tt.setRas_plan(wa_tt.getRas_plan()+wa_rt.getRas_plan());
  					wa_tt.setRas_poluchen(wa_tt.getRas_poluchen()+wa_rt.getRas_poluchen());
  					wa_tt.setOne_month_plan(wa_tt.getOne_month_plan()+wa_rt.getOne_month_plan());
  					wa_tt.setOne_month_poluchen(wa_tt.getOne_month_poluchen()+wa_rt.getOne_month_poluchen());
  					wa_tt.setRas_usd_plan(wa_tt.getRas_usd_plan()+wa_rt.getRas_usd_plan());
  					wa_tt.setRas_usd_poluchen(wa_tt.getRas_usd_poluchen()+wa_rt.getRas_usd_poluchen());
  					wa_tt.setOne_month_usd_plan(wa_tt.getOne_month_usd_plan()+wa_rt.getOne_month_usd_plan());
  					wa_tt.setOne_month_usd_poluchen(wa_tt.getOne_month_usd_poluchen()+wa_rt.getOne_month_usd_poluchen());
  				}

  				
  			}
  			
  			//l_outputTable = bkpfDao.findResultTableRfdocrow(dynamicWhereClause,true);
  			
  			
  			
  			tabindex = 2;
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");
  			
//  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
//  	  		reqCtx.update("form:tabView:bsegScrollPanel2");  	  	
//  			reqCtx.update("form:tabView:l_outputTable2");
		}
  		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
  	}
	public List<TotalTable> getBranchTotals() {
		List<TotalTable> l_tt = new ArrayList<TotalTable>();
		for (Map.Entry<String, TotalTable> entry : l_map_branch.entrySet()) {
	    	TotalTable wa_ft = new TotalTable();
	        wa_ft = (TotalTable) entry.getValue();
	        l_tt.add(wa_ft);
		}
		return l_tt;
	}




	
	public List<TotalTable> getCollectorTotals() {
		List<TotalTable> l_tt = new ArrayList<TotalTable>();
		for (Map.Entry<String, TotalTable> entry : l_map_collector.entrySet()) {
	    	TotalTable wa_ft = new TotalTable();
	        wa_ft = (TotalTable) entry.getValue();
	        l_tt.add(wa_ft);
		}
		return l_tt;
	}
	
  	public void to_search()
  	{
  		try 
		{
  			l_outputTable.clear();
  			l_map_branch.clear();
  			l_map_city_total.clear();
  			city_total_plan = 0;
  			city_total_poluchen = 0;	
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			Calendar date_from = Calendar.getInstance();
  			Calendar date_to = Calendar.getInstance();
  			
  			date_from.set(Calendar.YEAR, p_searchTable.getP_year());
  			date_from.set(Calendar.MONTH, p_searchTable.getP_month()-1);
  			date_from.set(Calendar.DAY_OF_MONTH, 1);
  			
  			
  			date_to.setTime(GeneralUtil.getLastDayOfMonth(p_searchTable.getP_year(), p_searchTable.getP_month()).getTime());
  			
  			p_searchTable.setBudat_from(date_from.getTime());
  			p_searchTable.setBudat_to(date_to.getTime());
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and br.bukrs = '"+p_searchTable.getBukrs()+"' ";
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
  				dynamicWhereClause = dynamicWhereClause + " and br.branch_id in "+br_list;
  			}
  			else
  				throw new DAOException("Выберите филиал");
  			Calendar cal_budat_from = Calendar.getInstance();
  			Calendar cal_budat_to = Calendar.getInstance();
  			if(p_searchTable.budat_from!=null)
  			{
  				cal_budat_from.setTime(p_searchTable.budat_from);
  			}
  			else
  			{
  				throw new DAOException("Выберите дату");
  			}
  			if(p_searchTable.budat_to!=null)
  			{
  				cal_budat_to.setTime(p_searchTable.budat_to);
  			}
  			//System.out.println(dynamicWhereClause);
  			//RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			RfcolService rfcolService = (RfcolService) appContext.getContext().getBean("rfcolService");
  			l_outputTable = rfcolService.search(p_searchTable.getBukrs(),p_searchTable.getP_year(),p_searchTable.getP_month(),
  					dynamicWhereClause,  GeneralUtil.getSQLDate(cal_budat_from), GeneralUtil.getSQLDate(cal_budat_to),1,p_searchTable.getP_status());
  			//l_outputTable = rfcolDao.dynamicFindCollectorStat();
  			for(RfcolResultTable wa_rt:l_outputTable)
  			{
  				if (wa_rt.getWaers()!=null && !wa_rt.getWaers().equals("USD"))
  				{
  					double sec_cur = p_exchangeRateF4Bean.getL_er_map_national().get("1"+wa_rt.getWaers()).getSc_value();
  					wa_rt.setRas_usd_plan(wa_rt.getRas_plan()/sec_cur);
  					wa_rt.setRas_usd_poluchen(wa_rt.getRas_poluchen()/sec_cur);
  					wa_rt.setOne_month_usd_plan(wa_rt.getOne_month_plan()/sec_cur);
  					wa_rt.setOne_month_usd_poluchen(wa_rt.getOne_month_poluchen()/sec_cur);
  				}
  				else
  				{
  					wa_rt.setRas_usd_plan(wa_rt.getRas_plan());
  					wa_rt.setRas_usd_poluchen(wa_rt.getRas_poluchen());
  					wa_rt.setOne_month_usd_plan(wa_rt.getOne_month_plan());
  					wa_rt.setOne_month_usd_poluchen(wa_rt.getOne_month_poluchen());
  				}
  				Branch wa_br = new Branch();
  				wa_br = p_branchF4Bean.getParentBranch(wa_rt.getBranch_id());
  				wa_rt.setCity_id(wa_br.getBranch_id());
  				wa_rt.setCity_name(wa_br.getText45());
  				//wa_rt.setTotal_usd_plan(wa_rt.getRas_usd_plan()+wa_rt.getOne_month_usd_plan());
  				//wa_rt.setTotal_usd_poluchen(wa_rt.getRas_usd_poluchen()+wa_rt.getOne_month_usd_poluchen());
  				
  				
  				TotalTable wa_city_tot = l_map_city_total.get(wa_rt.getCity_id());
  				if (wa_city_tot==null)
  				{
  					//using ras_us_plan and poluchen instead of total_usd_poluchen variables
  					wa_city_tot = new TotalTable();
  					wa_city_tot.setRas_usd_plan(wa_rt.getRas_usd_plan()+wa_rt.getOne_month_usd_plan());
  					wa_city_tot.setRas_usd_poluchen(wa_rt.getRas_usd_poluchen()+wa_rt.getOne_month_usd_poluchen());
  					l_map_city_total.put(wa_rt.getCity_id(), wa_city_tot);
  				}
  				else
  				{
  					wa_city_tot.setRas_usd_plan(wa_city_tot.getRas_usd_plan()+wa_rt.getRas_usd_plan()+wa_rt.getOne_month_usd_plan());
  					wa_city_tot.setRas_usd_poluchen(wa_city_tot.getRas_usd_poluchen()+wa_rt.getRas_usd_poluchen()+wa_rt.getOne_month_usd_poluchen());
  				}
  				
  				TotalTable wa_tt = l_map_branch.get(wa_rt.getWaers());
  				if (wa_tt==null)
  				{
  					wa_tt = new TotalTable();
  					wa_tt.setContract_amount(wa_rt.getContract_amount());
  					wa_tt.setWaers(wa_rt.getWaers());
  					wa_tt.setRas_plan(wa_rt.getRas_plan());
  					wa_tt.setRas_poluchen(wa_rt.getRas_poluchen());
  					wa_tt.setOne_month_plan(wa_rt.getOne_month_plan());
  					wa_tt.setOne_month_poluchen(wa_rt.getOne_month_poluchen());
  					wa_tt.setRas_usd_plan(wa_rt.getRas_usd_plan());
  					wa_tt.setRas_usd_poluchen(wa_rt.getRas_usd_poluchen());
  					wa_tt.setOne_month_usd_plan(wa_rt.getOne_month_usd_plan());
  					wa_tt.setOne_month_usd_poluchen(wa_rt.getOne_month_usd_poluchen());
  					l_map_branch.put(wa_rt.getWaers(), wa_tt);
  				}
  				else
  				{
  					wa_tt.setContract_amount(wa_tt.getContract_amount()+wa_rt.getContract_amount());
  					wa_tt.setRas_plan(wa_tt.getRas_plan()+wa_rt.getRas_plan());
  					wa_tt.setRas_poluchen(wa_tt.getRas_poluchen()+wa_rt.getRas_poluchen());
  					wa_tt.setOne_month_plan(wa_tt.getOne_month_plan()+wa_rt.getOne_month_plan());
  					wa_tt.setOne_month_poluchen(wa_tt.getOne_month_poluchen()+wa_rt.getOne_month_poluchen());
  					wa_tt.setRas_usd_plan(wa_tt.getRas_usd_plan()+wa_rt.getRas_usd_plan());
  					wa_tt.setRas_usd_poluchen(wa_tt.getRas_usd_poluchen()+wa_rt.getRas_usd_poluchen());
  					wa_tt.setOne_month_usd_plan(wa_tt.getOne_month_usd_plan()+wa_rt.getOne_month_usd_plan());
  					wa_tt.setOne_month_usd_poluchen(wa_tt.getOne_month_usd_poluchen()+wa_rt.getOne_month_usd_poluchen());
  				}
  				
  			}
  			for(RfcolResultTable wa_rt:l_outputTable)
  			{
  				
  				TotalTable wa_city_tot = l_map_city_total.get(wa_rt.getCity_id());
  				if (wa_city_tot!=null)
  				{
  					//using ras_us_plan and poluchen instead of total_usd_poluchen variables
  					wa_rt.setTotal_usd_plan(wa_city_tot.getRas_usd_plan());
  					wa_rt.setTotal_usd_poluchen(wa_city_tot.getRas_usd_poluchen());
  					l_map_city_total.remove(wa_rt.getCity_id());
  					if (wa_rt.getTotal_usd_plan()>0)
  					{
  						wa_rt.setTotal_usd_percentage(GeneralUtil.round(wa_rt.getTotal_usd_poluchen()/wa_rt.getTotal_usd_plan()*100, 2));
  					}
  					
  					city_total_plan = city_total_plan + wa_rt.getTotal_usd_plan();
  		  			city_total_poluchen = city_total_poluchen + wa_rt.getTotal_usd_poluchen();
  				}
  				
  				
  				
  			}
  			//l_outputTable = bkpfDao.findResultTableRfdocrow(dynamicWhereClause,true);
  			
  			if (city_total_plan>0)
			{
				city_total_percentage = GeneralUtil.round(city_total_poluchen/city_total_plan*100, 2);
			}
  			
  			getBranchTotals();
  			
			
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

	
	
	private List<RfcolResultTable> l_outputTable = new ArrayList<RfcolResultTable>();
	public List<RfcolResultTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<RfcolResultTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}
	private List<RfcolResultTable> l_outputTable2 = new ArrayList<RfcolResultTable>();
	public List<RfcolResultTable> getL_outputTable2() {
		return l_outputTable2;
	}
	public void setL_outputTable2(List<RfcolResultTable> l_outputTable2) {
		this.l_outputTable2 = l_outputTable2;
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
		private int p_status;
		private String bukrs = "";
		private List<Branch> l_branch = new ArrayList<Branch>();
		private List<String> selectedBranches = new ArrayList<String>();
		private Long branch_id = 0L;
		private Date budat_from;
		private Date budat_to;
		private int p_year;
		private int p_month;
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
		public int getP_year() {
			return p_year;
		}
		public void setP_year(int p_year) {
			this.p_year = p_year;
		}
		public int getP_month() {
			return p_month;
		}
		public void setP_month(int p_month) {
			this.p_month = p_month;
		}
		public int getP_status() {
			return p_status;
		}
		public void setP_status(int p_status) {
			this.p_status = p_status;
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
				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(1L) || wa.getBusiness_area_id().equals(2L) || wa.getBusiness_area_id().equals(3L) || wa.getBusiness_area_id().equals(4L))
				{
					p_searchTable.getL_branch().add(wa);
				}
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
	private double city_total_plan = 0;
	private double city_total_poluchen = 0;
	private double city_total_percentage = 0;	
	public double getCity_total_plan() {
		return city_total_plan;
	}
	public void setCity_total_plan(double city_total_plan) {
		this.city_total_plan = city_total_plan;
	}
	public double getCity_total_poluchen() {
		return city_total_poluchen;
	}
	public void setCity_total_poluchen(double city_total_poluchen) {
		this.city_total_poluchen = city_total_poluchen;
	}
	public double getCity_total_percentage() {
		return city_total_percentage;
	}
	public void setCity_total_percentage(double city_total_percentage) {
		this.city_total_percentage = city_total_percentage;
	}



	Map<Long,TotalTable> l_map_city_total = new HashMap<Long, TotalTable>();	
	Map<String,TotalTable> l_map_branch = new HashMap<String, TotalTable>();
	Map<String,TotalTable> l_map_collector = new HashMap<String, TotalTable>();
	public class TotalTable {
		public TotalTable()
		{
			
		};
		private String waers;
		private int contract_amount;
		private double  ras_plan;
		private double  ras_poluchen;
		private double  one_month_plan;
		private double  one_month_poluchen;
		private double  ras_usd_plan;
		private double  ras_usd_poluchen;
		private double  one_month_usd_plan;
		private double  one_month_usd_poluchen;
		public double getRas_plan() {
			return ras_plan;
		}
		public void setRas_plan(double ras_plan) {
			this.ras_plan = ras_plan;
		}
		public double getRas_poluchen() {
			return ras_poluchen;
		}
		public void setRas_poluchen(double ras_poluchen) {
			this.ras_poluchen = ras_poluchen;
		}
		public double getOne_month_plan() {
			return one_month_plan;
		}
		public void setOne_month_plan(double one_month_plan) {
			this.one_month_plan = one_month_plan;
		}
		public double getOne_month_poluchen() {
			return one_month_poluchen;
		}
		public void setOne_month_poluchen(double one_month_poluchen) {
			this.one_month_poluchen = one_month_poluchen;
		}
		public double getRas_usd_plan() {
			return ras_usd_plan;
		}
		public void setRas_usd_plan(double ras_usd_plan) {
			this.ras_usd_plan = ras_usd_plan;
		}
		public double getRas_usd_poluchen() {
			return ras_usd_poluchen;
		}
		public void setRas_usd_poluchen(double ras_usd_poluchen) {
			this.ras_usd_poluchen = ras_usd_poluchen;
		}
		public double getOne_month_usd_plan() {
			return one_month_usd_plan;
		}
		public void setOne_month_usd_plan(double one_month_usd_plan) {
			this.one_month_usd_plan = one_month_usd_plan;
		}
		public double getOne_month_usd_poluchen() {
			return one_month_usd_poluchen;
		}
		public void setOne_month_usd_poluchen(double one_month_usd_poluchen) {
			this.one_month_usd_poluchen = one_month_usd_poluchen;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public int getContract_amount() {
			return contract_amount;
		}
		public void setContract_amount(int contract_amount) {
			this.contract_amount = contract_amount;
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
         
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
        
        HSSFCellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setDataFormat((short)2);
         
        
        for(int i=0; i < sheet.getPhysicalNumberOfRows();i++) {
        	if (i>0)
        	{
        		HSSFRow row = sheet.getRow(i);
                for(int i2=0; i2 < row.getPhysicalNumberOfCells();i2++) {
                	HSSFCell cell = row.getCell(i2);
                	if (i2>3 && i2<14)
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		String value = cell.getStringCellValue();
                		if (value.length()>0)
                		{
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
    }
	public void postProcessXLS2(Object document) {
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
        
        HSSFCellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setDataFormat((short)2);
         
        
        for(int i=0; i < sheet.getPhysicalNumberOfRows();i++) {
        	if (i>0)
        	{
        		HSSFRow row = sheet.getRow(i);
                for(int i2=0; i2 < row.getPhysicalNumberOfCells();i2++) {
                	HSSFCell cell = row.getCell(i2);
                	if ((i2>5 && i2<8) || (i2>9 && i2<12) || (i2>12))
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		String value = cell.getStringCellValue();
                		if (value.length()>0)
                		{
                			value = value.replaceAll("\u00A0", "");
                    		value = value.replace(",", ".");
                    		double dvalue = Double.valueOf(value);
                    		
                    		value = value.replace("\\s","");
                    		value = value.trim();
                    		
                    		cell.setCellValue(dvalue);
                		}
                		
                	}
                	if (i2==5 || i2==9)
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		cell.setCellValue("");
                		
                	}
                }
        	}
            
        }
    }
	public void getDetail(Long a_branch_id,String a_waers,Long a_staff_id, String a_ps)
	{
		try 
		{
			

  			int a_staff_id_int = 0;
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";

  			dynamicWhereClause = dynamicWhereClause + " and br.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			dynamicWhereClause = dynamicWhereClause + " and br.branch_id in ("+a_branch_id+") ";
  			dynamicWhereClause = dynamicWhereClause + " and waers = '"+a_waers+"' ";
  			if (a_staff_id!=null)
  			{
  				a_staff_id_int = Integer.parseInt(String.valueOf(a_staff_id));
  	  			if (a_staff_id_int>0) dynamicWhereClause = dynamicWhereClause + " and collector = "+a_staff_id_int;  
  			}
  			else dynamicWhereClause = dynamicWhereClause + " and collector is null ";
  				
  							
  			
  			if (a_ps.equals("1")) 
  			{
  				l_detailedInfo1Month = new ArrayList<DetailTable>();
  				l_detailedInfo1Month_kol_total = 0;
  				l_detailedInfo1Month_plan_total = 0;
  				l_detailedInfo1Month_poluchen_total = 0;
  				dynamicWhereClause = dynamicWhereClause + " and payment_schedule = "+a_ps;
  			}
  			else
  			{
  				l_detailedInfo2Month = new ArrayList<DetailTable>();
  				l_detailedInfo2Month_kol_total = 0;
  				l_detailedInfo2Month_plan_total = 0;
  				l_detailedInfo2Month_poluchen_total = 0;
  				dynamicWhereClause = dynamicWhereClause + " and payment_schedule > "+1;
  			}
  				
  			
  			Calendar cal_budat_from = Calendar.getInstance();
  			Calendar cal_budat_to = Calendar.getInstance();
  			cal_budat_from.setTime(p_searchTable.budat_from);
  			cal_budat_to.setTime(p_searchTable.budat_to);

  			//System.out.println(dynamicWhereClause);
  			
  			RfcolService rfcolService = (RfcolService) appContext.getContext().getBean("rfcolService");
  			//RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolService.searchDetail(p_searchTable.getBukrs(), p_searchTable.getP_year(), p_searchTable.getP_month(), dynamicWhereClause, 
  					GeneralUtil.getSQLDate(cal_budat_from), GeneralUtil.getSQLDate(cal_budat_to), p_searchTable.getP_status());
  			//results =  rfcolDao.dynamicFindCollectorDetail(dynamicWhereClause,  new java.sql.Date(cal_budat_from.getTimeInMillis()),  new java.sql.Date(cal_budat_to.getTimeInMillis()),p_searchTable.getP_status());
			for (Object[] result :results) {
				DetailTable wa_rt = new DetailTable();

			if (result[0]!=null) wa_rt.setWaers(String.valueOf(result[0]));
			if (a_ps.equals("1"))
			{
				if (result[3]!=null) wa_rt.setPlan(Double.parseDouble(String.valueOf(result[3]))); 
				if (result[4]!=null) wa_rt.setPoluchen(Double.parseDouble(String.valueOf(result[4])));
			}
			else
			{
				if (result[1]!=null) wa_rt.setPlan(Double.parseDouble(String.valueOf(result[1]))); 
				if (result[2]!=null) wa_rt.setPoluchen(Double.parseDouble(String.valueOf(result[2])));
			}
			if (result[5]!=null) wa_rt.setContract_number(String.valueOf(result[5]));
			
			if (result[6]!=null)
				try {
					wa_rt.setContract_date(formatter.parse(String.valueOf(result[6])));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (result[7]!=null) wa_rt.setFio(String.valueOf(result[7]));
			if (result[8]!=null) wa_rt.setName(String.valueOf(result[8]));
			
			if (a_ps.equals("1")) 
  			{
				l_detailedInfo1Month.add(wa_rt);			
				l_detailedInfo1Month_kol_total ++;
				l_detailedInfo1Month_plan_total = l_detailedInfo1Month_plan_total+ wa_rt.getPlan();
				l_detailedInfo1Month_poluchen_total = l_detailedInfo1Month_poluchen_total+ wa_rt.getPoluchen();
  			}
  			else
  			{
  				l_detailedInfo2Month.add(wa_rt);			
  				l_detailedInfo2Month_kol_total ++;
  				l_detailedInfo2Month_plan_total = l_detailedInfo2Month_plan_total+ wa_rt.getPlan();
  				l_detailedInfo2Month_poluchen_total = l_detailedInfo2Month_poluchen_total+ wa_rt.getPoluchen();
  			}
			
			
			

		}
    	
    	
  			
  			//l_outputTable = bkpfDao.findResultTableRfdocrow(dynamicWhereClause,true);
  			
  			
			if (a_ps.equals("1")) 
  			{
				RequestContext reqCtx = RequestContext.getCurrentInstance();
	  			reqCtx.update("form:detInfo1MonthTable");
  			}
  			else
  			{
  				RequestContext reqCtx = RequestContext.getCurrentInstance();
  	  			reqCtx.update("form:detInfo2MonthTable");
  			}
  	  		
  			
//  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
//  	  		reqCtx.update("form:tabView:bsegScrollPanel2");  	  	
//  			reqCtx.update("form:tabView:l_outputTable2");
		}
  		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
	}
	private List<DetailTable> l_detailedInfo1Month = new ArrayList<DetailTable>();
	public List<DetailTable> getL_detailedInfo1Month() {
		return l_detailedInfo1Month;
	}
	public void setL_detailedInfo1Month(List<DetailTable> l_detailedInfo1Month) {
		this.l_detailedInfo1Month = l_detailedInfo1Month;
	}
	private int l_detailedInfo1Month_kol_total;
	private double l_detailedInfo1Month_plan_total;
	private double l_detailedInfo1Month_poluchen_total;
	
	private List<DetailTable> l_detailedInfo2Month = new ArrayList<DetailTable>();
	private int l_detailedInfo2Month_kol_total;
	private double l_detailedInfo2Month_plan_total;
	private double l_detailedInfo2Month_poluchen_total;
	
	
	
	public int getL_detailedInfo1Month_kol_total() {
		return l_detailedInfo1Month_kol_total;
	}
	public void setL_detailedInfo1Month_kol_total(int l_detailedInfo1Month_kol_total) {
		this.l_detailedInfo1Month_kol_total = l_detailedInfo1Month_kol_total;
	}
	public double getL_detailedInfo1Month_plan_total() {
		return l_detailedInfo1Month_plan_total;
	}
	public void setL_detailedInfo1Month_plan_total(
			double l_detailedInfo1Month_plan_total) {
		this.l_detailedInfo1Month_plan_total = l_detailedInfo1Month_plan_total;
	}
	public double getL_detailedInfo1Month_poluchen_total() {
		return l_detailedInfo1Month_poluchen_total;
	}
	public void setL_detailedInfo1Month_poluchen_total(
			double l_detailedInfo1Month_poluchen_total) {
		this.l_detailedInfo1Month_poluchen_total = l_detailedInfo1Month_poluchen_total;
	}
	public List<DetailTable> getL_detailedInfo2Month() {
		return l_detailedInfo2Month;
	}
	public void setL_detailedInfo2Month(List<DetailTable> l_detailedInfo2Month) {
		this.l_detailedInfo2Month = l_detailedInfo2Month;
	}
	public int getL_detailedInfo2Month_kol_total() {
		return l_detailedInfo2Month_kol_total;
	}
	public void setL_detailedInfo2Month_kol_total(int l_detailedInfo2Month_kol_total) {
		this.l_detailedInfo2Month_kol_total = l_detailedInfo2Month_kol_total;
	}
	public double getL_detailedInfo2Month_plan_total() {
		return l_detailedInfo2Month_plan_total;
	}
	public void setL_detailedInfo2Month_plan_total(
			double l_detailedInfo2Month_plan_total) {
		this.l_detailedInfo2Month_plan_total = l_detailedInfo2Month_plan_total;
	}
	public double getL_detailedInfo2Month_poluchen_total() {
		return l_detailedInfo2Month_poluchen_total;
	}
	public void setL_detailedInfo2Month_poluchen_total(
			double l_detailedInfo2Month_poluchen_total) {
		this.l_detailedInfo2Month_poluchen_total = l_detailedInfo2Month_poluchen_total;
	}




	public class DetailTable {
		public DetailTable()
		{
			
		};
		private String contract_number;
		private Date contract_date;
		private String fio;
		private String name;
		private String waers;
		private double plan;
		private double poluchen;
		public String getContract_number() {
			return contract_number;
		}
		public void setContract_number(String contract_number) {
			this.contract_number = contract_number;
		}
		public Date getContract_date() {
			return contract_date;
		}
		public void setContract_date(Date contract_date) {
			this.contract_date = contract_date;
		}
		public String getFio() {
			return fio;
		}
		public void setFio(String fio) {
			this.fio = fio;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getPlan() {
			return plan;
		}
		public void setPlan(double plan) {
			this.plan = plan;
		}
		public double getPoluchen() {
			return poluchen;
		}
		public void setPoluchen(double poluchen) {
			this.poluchen = poluchen;
		}
		
	}
	
	public void to_save()
	{
		try{
			if (!(userData.getUserid()==1L))
			{
				throw new DAOException("У вас нет прав. Обратитесь  к администратору.");
			}
			
			RfcolService rfcolService = (RfcolService) appContext.getContext().getBean("rfcolService");
			rfcolService.save(p_searchTable.getBukrs(), p_searchTable.getP_year(), p_searchTable.getP_month());
			addMessage("Info","Saved"); 
		}
  		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  
	}
	
	//*****************************************************************************************************
		
}
