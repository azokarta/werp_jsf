package finance.other;

import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.BranchDao;
import general.dao.DAOException; 
import general.dao.TempPayrollDao;
import general.output.tables.FosaBranchResultTable;
import general.output.tables.FosaResultTable;
import general.services.PayrollService;
import general.tables.Branch;
import general.tables.TempPayroll;

import java.io.Serializable;

 






import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
 


















import org.springframework.beans.BeanUtils;

import user.User;

@ManagedBean(name = "fosaBean", eager = true)
@ViewScoped
public class Fosa implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FOSA";
	private final static Long transaction_id = (long) 256;
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
			PermissionController.canRead(userData,Fosa.transaction_id);
			
			//Calendar curDate = Calendar.getInstance(); 

			
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
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and br.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  			
  			if (p_searchTable.getBranch_id()!=null && !p_searchTable.getBranch_id().equals(0L))
  			{
  				dynamicWhereClause = dynamicWhereClause + getBranchTree(p_searchTable.getBranch_id());
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			
  			Calendar cal_budat_from = Calendar.getInstance();

  			//System.out.println(dynamicWhereClause);
  			List<TempPayroll> l_tp = new ArrayList<TempPayroll>();
  			TempPayrollDao tempPayrollDao = (TempPayrollDao) appContext.getContext().getBean("tmpPrlDao");
  			l_tp = tempPayrollDao.dynamicSearch(" bukrs = "+p_searchTable.getBukrs());
  			if (l_tp.size()==0)
  			{
  				throw new DAOException(Helper.getErrorMessage(78L, userData.getU_language()));
  			}
  			else 
  			{
  				cal_budat_from = new GregorianCalendar(l_tp.get(0).getGjahr(),l_tp.get(0).getMonat(),1,0,0,0);
  				cal_budat_from.set(Calendar.YEAR, l_tp.get(0).getGjahr());
  				cal_budat_from.set(Calendar.MONTH, l_tp.get(0).getMonat()-1);
  				cal_budat_from.set(Calendar.DAY_OF_MONTH, 1);
  				System.out.println(cal_budat_from.getTime());
  			}
  			String a_branch_id = getBranchTree(p_searchTable.getBranch_id());
  			l_outputTable = tempPayrollDao.getSalaryByOffices(p_searchTable.getBukrs(), a_branch_id, new java.sql.Date(cal_budat_from.getTimeInMillis()));
  			l_bb = tempPayrollDao.getSalaryByOffices2(p_searchTable.getBukrs(), a_branch_id, new java.sql.Date(cal_budat_from.getTimeInMillis()));
  			
  			/*for(FosaResultTable wa_rt:l_outputTable)
  			{
  				if (!wa_rt.getWaers().equals("USD"))
  				{
  					double sec_cur = p_exchangeRateF4Bean.getL_er_map_national().get("1"+wa_rt.getWaers()).getSc_value();
  					wa_rt.setRas_usd_plan(wa_rt.getRas_plan()/sec_cur);
  					wa_rt.setRas_usd_poluchen(wa_rt.getRas_poluchen()/sec_cur);
  					wa_rt.setOne_month_usd_plan(wa_rt.getOne_month_plan()/sec_cur);
  					wa_rt.setOne_month_usd_poluchen(wa_rt.getOne_month_usd_poluchen()/sec_cur);
  				}
  				else
  				{
  					wa_rt.setRas_usd_plan(wa_rt.getRas_plan());
  					wa_rt.setRas_usd_poluchen(wa_rt.getRas_poluchen());
  					wa_rt.setOne_month_usd_plan(wa_rt.getOne_month_plan());
  					wa_rt.setOne_month_usd_poluchen(wa_rt.getOne_month_usd_poluchen());
  				}
  				ras_usd_plan_tot=ras_usd_plan_tot+wa_rt.getRas_usd_plan();
  			    ras_usd_poluchen_tot=ras_usd_poluchen_tot+wa_rt.getRas_usd_poluchen();
  			    ras_plan_tot=ras_plan_tot+wa_rt.getRas_plan();
  			    ras_poluchen_tot=ras_poluchen_tot+wa_rt.getRas_poluchen();
  			    one_month_plan_tot=one_month_plan_tot+wa_rt.getOne_month_plan();
  			    one_month_poluchen_tot=one_month_poluchen_tot+wa_rt.getOne_month_poluchen();
  			    one_month_usd_plan_tot=one_month_usd_plan_tot+wa_rt.getOne_month_usd_plan();
  			    one_month_usd_poluchen_tot=one_month_usd_poluchen_tot+wa_rt.getOne_month_usd_poluchen();

  				
  			}*/
  			
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

	
	
	private List<FosaResultTable> l_outputTable = new ArrayList<FosaResultTable>();
	public List<FosaResultTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<FosaResultTable> l_outputTable) {
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
		private String bukrs = "";
		private Long branch_id = 0L;
	}	
	
	private String getBranchTree(Long a_parentId) {
		String where;
		//Branch temp_branch = null;
		where = " ";
		int count = 0;
		BranchDao branchDao = (BranchDao) appContext.getContext().getBean("branchDao");
		
		if (a_parentId==60L)
		{
			where = where + "60";
			return where;
		}
		
		for(Branch wa_branch:branchDao.findChilds(a_parentId))
		{
			if (wa_branch.getType()!=null && wa_branch.getType().equals(3L))
			{
				if (count==0)
				{
					where = where + wa_branch.getBranch_id();
				}
				else
				{
					where = where + ", "+wa_branch.getBranch_id();
				}
				count++;
			}
		}
		if (count==0)
		{
			throw new DAOException(Helper.getErrorMessage(103L, userData.getU_language()));
		}
		return where;

	}
	public void to_save()
	{
		try
		{
			PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService");
			payrollService.saveFosa(l_outputTable);
			//TempPayrollDao tempPayrollDao = (TempPayrollDao) appContext.getContext().getBean("tmpPrlDao");
			to_search();
			addMessage("Info",Helper.getErrorMessage(104L, userData.getU_language()));
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  	
	}
	
	public void findDetails(Long a_staff_id)
	{
		try
		{
			l_outputTableDetailed.clear();
			TempPayrollDao tempPayrollDao = (TempPayrollDao) appContext.getContext().getBean("tmpPrlDao");
			List<TempPayroll> l_tp = new ArrayList<TempPayroll>();
			l_tp = tempPayrollDao.dynamicSearch(" staff_id ="+a_staff_id+" and bukrs="+p_searchTable.getBukrs());
			for(TempPayroll wa_tp:l_tp)
			{
				OutputTable wa_out = new OutputTable();
				BeanUtils.copyProperties(wa_tp, wa_out);
				if (!(wa_tp.getType()==1 || wa_tp.getType()==2 || wa_tp.getType()==3 || wa_tp.getType()==7 || wa_tp.getType()==14 || wa_tp.getType()==15))
				{
					wa_out.setAmount(wa_out.getAmount()*-1);
				}	
					
				l_outputTableDetailed.add(wa_out);
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:outputTableDetailed");	
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  	
	}
	
	private List<FosaBranchResultTable> l_bb = new ArrayList<FosaBranchResultTable>();

	
	private double  ras_usd_plan_tot;
	private double  ras_usd_poluchen_tot;
	private double  ras_plan_tot;
	private double  ras_poluchen_tot;
	private double  one_month_plan_tot;
	private double  one_month_poluchen_tot;
	private double  one_month_usd_plan_tot;
	private double  one_month_usd_poluchen_tot;
	public double getRas_usd_plan_tot() {
		return ras_usd_plan_tot;
	}
	public void setRas_usd_plan_tot(double ras_usd_plan_tot) {
		this.ras_usd_plan_tot = ras_usd_plan_tot;
	}
	public double getRas_usd_poluchen_tot() {
		return ras_usd_poluchen_tot;
	}
	public void setRas_usd_poluchen_tot(double ras_usd_poluchen_tot) {
		this.ras_usd_poluchen_tot = ras_usd_poluchen_tot;
	}
	public double getRas_plan_tot() {
		return ras_plan_tot;
	}
	public void setRas_plan_tot(double ras_plan_tot) {
		this.ras_plan_tot = ras_plan_tot;
	}
	public double getRas_poluchen_tot() {
		return ras_poluchen_tot;
	}
	public void setRas_poluchen_tot(double ras_poluchen_tot) {
		this.ras_poluchen_tot = ras_poluchen_tot;
	}
	public double getOne_month_plan_tot() {
		return one_month_plan_tot;
	}
	public void setOne_month_plan_tot(double one_month_plan_tot) {
		this.one_month_plan_tot = one_month_plan_tot;
	}
	public double getOne_month_poluchen_tot() {
		return one_month_poluchen_tot;
	}
	public void setOne_month_poluchen_tot(double one_month_poluchen_tot) {
		this.one_month_poluchen_tot = one_month_poluchen_tot;
	}
	public double getOne_month_usd_plan_tot() {
		return one_month_usd_plan_tot;
	}
	public void setOne_month_usd_plan_tot(double one_month_usd_plan_tot) {
		this.one_month_usd_plan_tot = one_month_usd_plan_tot;
	}
	public double getOne_month_usd_poluchen_tot() {
		return one_month_usd_poluchen_tot;
	}
	public void setOne_month_usd_poluchen_tot(double one_month_usd_poluchen_tot) {
		this.one_month_usd_poluchen_tot = one_month_usd_poluchen_tot;
	}
	public List<FosaBranchResultTable> getL_bb() {
		return l_bb;
	}
	public void setL_bb(List<FosaBranchResultTable> l_bb) {
		this.l_bb = l_bb;
	}
	
	public List<OutputTable> getL_outputTableDetailed() {
		return l_outputTableDetailed;
	}
	public void setL_outputTableDetailed(List<OutputTable> l_outputTableDetailed) {
		this.l_outputTableDetailed = l_outputTableDetailed;
	}
	private List<OutputTable> l_outputTableDetailed = new ArrayList<OutputTable>();
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private int index;
		private Long tp_id;
		private String waers;
		private double amount; 
		private Long branch_id;
		private String branch_name;
		private Long staff_id;
		private String staff_name;
		private Long salary_id; 
		private int type;
		private Long bonus_id;
		private Long customer_id;
		private String text45;
		private String status;
		private Long contract_number;
		private Date bldat;
		private Long matnr;
		private int matnr_count;
		private Long position_id;
		private double plan_amount; 
		private double fact_amount; 
		
		public double getPlan_amount() {
			return plan_amount;
		}
		public void setPlan_amount(double plan_amount) {
			this.plan_amount = plan_amount;
		}
		public double getFact_amount() {
			return fact_amount;
		}
		public void setFact_amount(double fact_amount) {
			this.fact_amount = fact_amount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getText45() {
			return text45;
		}
		public void setText45(String text45) {
			this.text45 = text45;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Long getTp_id() {
			return tp_id;
		}
		public void setTp_id(Long tp_id) {
			this.tp_id = tp_id;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public String getBranch_name() {
			return branch_name;
		}
		public void setBranch_name(String branch_name) {
			this.branch_name = branch_name;
		}
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
		public Long getSalary_id() {
			return salary_id;
		}
		public void setSalary_id(Long salary_id) {
			this.salary_id = salary_id;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public Long getBonus_id() {
			return bonus_id;
		}
		public void setBonus_id(Long bonus_id) {
			this.bonus_id = bonus_id;
		}
		
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		public Long getMatnr() {
			return matnr;
		}
		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}
		public int getMatnr_count() {
			return matnr_count;
		}
		public void setMatnr_count(int matnr_count) {
			this.matnr_count = matnr_count;
		}
		public Long getPosition_id() {
			return position_id;
		}
		public void setPosition_id(Long position_id) {
			this.position_id = position_id;
		}
		public Long getContract_number() {
			return contract_number;
		}
		public void setContract_number(Long contract_number) {
			this.contract_number = contract_number;
		}
		public Date getBldat() {
			return bldat;
		}
		public void setBldat(Date bldat) {
			this.bldat = bldat;
		}
		
		
	}
	//*****************************************************************************************************
		
}
