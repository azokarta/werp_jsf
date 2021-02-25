package finance.reports;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.PayrollDao;
import general.dao.UserRoleDao;
import general.services.ZreportService;
import general.tables.Branch;
import general.tables.UserRole;
import general.tables.Zreport;

import java.io.InputStream;
import java.io.Serializable;

 





import java.util.ArrayList;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;
 












import user.User;

@ManagedBean(name = "frep7Bean", eager = true)
@ViewScoped
public class Frep7 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP7";
	private final static Long transaction_id = (long) 455;
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
			toMainPage();
			//Calendar curDate = Calendar.getInstance();
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Frep7.transaction_id);
			
			
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
  			if (p_searchTable.getBukrs()==null || p_searchTable.getBukrs().equals("0"))
  			{
  				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  			}
  				
  			
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
  				dynamicWhereClause = dynamicWhereClause + " b1.branch_id in "+br_list;
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			
//  			if (p_searchTable.con_date_from==null)
//	  		{
//	  			throw new DAOException("Выберите дату с");
//	  		}
//	  		else dynamicWhereClause = dynamicWhereClause + " and c.contract_date between '"+GeneralUtil.getSQLDate(p_searchTable.con_date_from)+"' ";
//	  			
//	  		
//	  		if (p_searchTable.con_date_to==null)
//	  		{
//	  			throw new DAOException("Выберите дату по");
//	  		}
//	  		else dynamicWhereClause = dynamicWhereClause + " and '"+GeneralUtil.getSQLDate(p_searchTable.con_date_to)+"' ";
  			
  			
  			double exKzt = p_exchangeRateF4Bean.getL_er_map_national().get("1KZT").getSc_value();
  			double exKgs = p_exchangeRateF4Bean.getL_er_map_national().get("1KGS").getSc_value();
  			double exUzs = p_exchangeRateF4Bean.getL_er_map_national().get("1UZS").getSc_value();
  			double exAzn = p_exchangeRateF4Bean.getL_er_map_national().get("1AZN").getSc_value();
  			
	  		PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = payrollDao.dynamicFrep7Result(p_searchTable.getBukrs(),dynamicWhereClause, userData.getU_language());
  			OutputTable wa_temp = new OutputTable();
  			for (Object[] wa_result:results)
  			{
  				OutputTable wa_out = new OutputTable();
  				//a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s.text45 as hname
  				if (wa_result[1]!=null) wa_out.setBranchName(String.valueOf(wa_result[1]));
  				if (wa_result[2]!=null) wa_out.setWaers(String.valueOf(wa_result[2]));
  				if (wa_result[3]!=null) wa_out.setBalance(Double.parseDouble(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setDeposit(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setDolg(Double.parseDouble(String.valueOf(wa_result[5])));
  				if (wa_result[6]!=null) wa_out.setZablok(Double.parseDouble(String.valueOf(wa_result[6])));
  				if (wa_result[7]!=null) wa_out.setAvans_zapros(Double.parseDouble(String.valueOf(wa_result[7])));
  				
  				if (wa_out.getWaers()!=null)
  				{
  					double sv = 1;
  					if (wa_out.getWaers().equals("KZT")) sv=exKzt;
  					else if (wa_out.getWaers().equals("KGS")) sv=exKgs;
  					else if (wa_out.getWaers().equals("UZS")) sv=exUzs;
  					else if (wa_out.getWaers().equals("AZN")) sv=exAzn;
  					wa_out.setBalance_usd(wa_out.getBalance()/sv);
  	  				wa_out.setDeposit_usd(wa_out.getDeposit()/sv);
  	  				wa_out.setDolg_usd(wa_out.getDolg()/sv);
  	  				wa_out.setZablok_usd(wa_out.getZablok()/sv);
  	  				wa_out.setAvans_zapros_usd(wa_out.getAvans_zapros()/sv);
  					
  					l_outputTable.add(wa_out);
  					
  					if (wa_out.getBranchName()==null)
  					{
  						wa_temp.setBalance_usd(wa_temp.getBalance_usd()+wa_out.getBalance_usd());
  						wa_temp.setDeposit_usd(wa_temp.getDeposit_usd()+wa_out.getDeposit_usd());
  						wa_temp.setDolg_usd(wa_temp.getDolg_usd()+wa_out.getDolg_usd());
  						wa_temp.setZablok_usd(wa_temp.getZablok_usd()+wa_out.getZablok_usd());
  						wa_temp.setAvans_zapros_usd(wa_temp.getAvans_zapros_usd()+wa_out.getAvans_zapros_usd());
  					}
  				}
  				
  				
  				
  			}
  			l_outputTable.add(wa_temp);
  			
  			
  			
			
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
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private String bukrs = "";
		private String branchName;
		private String waers;
		private double balance;
		private double deposit;
		private double dolg;
		private double zablok;
		private double avans_zapros;
		private double balance_usd;
		private double deposit_usd;
		private double dolg_usd;
		private double zablok_usd;
		private double avans_zapros_usd;
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
		public double getDeposit() {
			return deposit;
		}
		public void setDeposit(double deposit) {
			this.deposit = deposit;
		}
		public double getDolg() {
			return dolg;
		}
		public void setDolg(double dolg) {
			this.dolg = dolg;
		}
		public double getZablok() {
			return zablok;
		}
		public void setZablok(double zablok) {
			this.zablok = zablok;
		}
		public double getAvans_zapros() {
			return avans_zapros;
		}
		public void setAvans_zapros(double avans_zapros) {
			this.avans_zapros = avans_zapros;
		}
		public double getBalance_usd() {
			return balance_usd;
		}
		public void setBalance_usd(double balance_usd) {
			this.balance_usd = balance_usd;
		}
		public double getDeposit_usd() {
			return deposit_usd;
		}
		public void setDeposit_usd(double deposit_usd) {
			this.deposit_usd = deposit_usd;
		}
		public double getDolg_usd() {
			return dolg_usd;
		}
		public void setDolg_usd(double dolg_usd) {
			this.dolg_usd = dolg_usd;
		}
		public double getZablok_usd() {
			return zablok_usd;
		}
		public void setZablok_usd(double zablok_usd) {
			this.zablok_usd = zablok_usd;
		}
		public double getAvans_zapros_usd() {
			return avans_zapros_usd;
		}
		public void setAvans_zapros_usd(double avans_zapros_usd) {
			this.avans_zapros_usd = avans_zapros_usd;
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
		private String bukrs = "";
		private String branchName;
		private String fio;
		private Long staff_id;
		private String waers;
		private double balance;
		private double deposit;
		private double dolg;
		private double zablok;
		private double avans_zapros;
		private double balance_usd;
		private double deposit_usd;
		private double dolg_usd;
		private double zablok_usd;
		private double avans_zapros_usd;
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
		public double getDeposit() {
			return deposit;
		}
		public void setDeposit(double deposit) {
			this.deposit = deposit;
		}
		public double getDolg() {
			return dolg;
		}
		public void setDolg(double dolg) {
			this.dolg = dolg;
		}
		public double getZablok() {
			return zablok;
		}
		public void setZablok(double zablok) {
			this.zablok = zablok;
		}
		public double getAvans_zapros() {
			return avans_zapros;
		}
		public void setAvans_zapros(double avans_zapros) {
			this.avans_zapros = avans_zapros;
		}
		public double getBalance_usd() {
			return balance_usd;
		}
		public void setBalance_usd(double balance_usd) {
			this.balance_usd = balance_usd;
		}
		public double getDeposit_usd() {
			return deposit_usd;
		}
		public void setDeposit_usd(double deposit_usd) {
			this.deposit_usd = deposit_usd;
		}
		public double getDolg_usd() {
			return dolg_usd;
		}
		public void setDolg_usd(double dolg_usd) {
			this.dolg_usd = dolg_usd;
		}
		public double getZablok_usd() {
			return zablok_usd;
		}
		public void setZablok_usd(double zablok_usd) {
			this.zablok_usd = zablok_usd;
		}
		public double getAvans_zapros_usd() {
			return avans_zapros_usd;
		}
		public void setAvans_zapros_usd(double avans_zapros_usd) {
			this.avans_zapros_usd = avans_zapros_usd;
		}
		public String getFio() {
			return fio;
		}
		public void setFio(String fio) {
			this.fio = fio;
		}
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		
		
	}
	Map<String,Long> branch_map = new HashMap<String,Long>();
	public void updateBranch()
	{
		try{
			Long ba = 0L;
			Long br = 0L;
			branch_map = new HashMap<String,Long>();
			p_searchTable.getL_branch().clear();
			
			
			UserRoleDao userRoleDao = (UserRoleDao) appContext.getContext().getBean("userRoleDao");	
			for (UserRole wa:userRoleDao.findUserRoles(userData.getUserid()))
			{
				if (wa.getRoleId().equals(1L) || wa.getRoleId().equals(8L) || wa.getRoleId().equals(52L))
				{

					String text = "Уволенные";
					if (userData.getU_language().equals("en")) text = "Dismissed";
					else if (userData.getU_language().equals("tr")) text = "Dismissed";
					
					Branch wa_temp = new Branch();
					wa_temp.setBranch_id(0L);
					wa_temp.setText45(text);
					p_searchTable.getL_branch().add(wa_temp);
				}
			}
			for(Branch wa:p_branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(p_searchTable.bukrs,userData.getBukrs(),ba,ba, br,userData.getBranch_id()))
			{
				p_searchTable.getL_branch().add(wa);
				branch_map.put(wa.getText45(), wa.getBranch_id());
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
	
	
	public void getDetail(String a_branchName,String a_waers)
	{
		try 
		{

			String text = "Уволенные";
			if (userData.getU_language().equals("en")) text = "Dismissed";
			else if (userData.getU_language().equals("tr")) text = "Dismissed";
			Long a_branch_id;
			l_detailTable.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
			if (a_branchName.equals(text))
			{
				a_branch_id = 0L;
			}
			else if (a_branchName.length()<1)
			{
				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
			}
			else
			{
				a_branch_id = branch_map.get(a_branchName);
				if (a_branch_id==null)
				{
					throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
				}
			}
			
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			dynamicWhereClause = dynamicWhereClause + " b1.branch_id in ("+a_branch_id+") ";
  			dynamicWhereClause = dynamicWhereClause + " and b1.waers = '"+a_waers+"' ";
  				
  			
  			
  			
  			double exKzt = p_exchangeRateF4Bean.getL_er_map_national().get("1KZT").getSc_value();
  			double exKgs = p_exchangeRateF4Bean.getL_er_map_national().get("1KGS").getSc_value();
  			double exUzs = p_exchangeRateF4Bean.getL_er_map_national().get("1UZS").getSc_value();
  			double exAzn = p_exchangeRateF4Bean.getL_er_map_national().get("1AZN").getSc_value();
  			
	  		PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = payrollDao.dynamicFrep7Detail(p_searchTable.getBukrs(),dynamicWhereClause);
  			DetailTable wa_temp = new DetailTable();
  			for (Object[] wa_result:results)
  			{
  				DetailTable wa_out = new DetailTable();
  				//a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s.text45 as hname
  				if (wa_result[1]!=null) wa_out.setBranchName(String.valueOf(wa_result[1]));
  				if (wa_result[2]!=null) wa_out.setWaers(String.valueOf(wa_result[2]));
  				if (wa_result[3]!=null) wa_out.setBalance(Double.parseDouble(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setDeposit(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setDolg(Double.parseDouble(String.valueOf(wa_result[5])));
  				if (wa_result[6]!=null) wa_out.setZablok(Double.parseDouble(String.valueOf(wa_result[6])));
  				if (wa_result[7]!=null) wa_out.setAvans_zapros(Double.parseDouble(String.valueOf(wa_result[7])));
  				if (wa_result[8]!=null) wa_out.setFio(String.valueOf(wa_result[8]));
  				if (wa_result[9]!=null) wa_out.setStaff_id(Long.parseLong(String.valueOf(wa_result[9])));
  				if (wa_out.getWaers()!=null)
  				{
  					double sv = 1;
  					if (wa_out.getWaers().equals("KZT")) sv=exKzt;
  					else if (wa_out.getWaers().equals("KGS")) sv=exKgs;
  					else if (wa_out.getWaers().equals("UZS")) sv=exUzs;
  					else if (wa_out.getWaers().equals("AZN")) sv=exAzn;
  					wa_out.setBalance_usd(wa_out.getBalance()/sv);
  	  				wa_out.setDeposit_usd(wa_out.getDeposit()/sv);
  	  				wa_out.setDolg_usd(wa_out.getDolg()/sv);
  	  				wa_out.setZablok_usd(wa_out.getZablok()/sv);
  	  				wa_out.setAvans_zapros_usd(wa_out.getAvans_zapros()/sv);
  					
  	  				l_detailTable.add(wa_out);
  					
	  	  			wa_temp.setBalance(wa_temp.getBalance()+wa_out.getBalance());
					wa_temp.setDeposit(wa_temp.getDeposit()+wa_out.getDeposit());
					wa_temp.setDolg(wa_temp.getDolg()+wa_out.getDolg());
					wa_temp.setZablok(wa_temp.getZablok()+wa_out.getZablok());
					wa_temp.setAvans_zapros(wa_temp.getAvans_zapros()+wa_out.getAvans_zapros());
  	  				wa_temp.setBalance_usd(wa_temp.getBalance_usd()+wa_out.getBalance_usd());
					wa_temp.setDeposit_usd(wa_temp.getDeposit_usd()+wa_out.getDeposit_usd());
					wa_temp.setDolg_usd(wa_temp.getDolg_usd()+wa_out.getDolg_usd());
					wa_temp.setZablok_usd(wa_temp.getZablok_usd()+wa_out.getZablok_usd());
					wa_temp.setAvans_zapros_usd(wa_temp.getAvans_zapros_usd()+wa_out.getAvans_zapros_usd());
  				}
  				
  				
  				
  			}
  			l_detailTable.add(wa_temp);
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
	
	public void downloadExcelResult() {
        try {
        	Long a_rep_id=65L;
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(2);
            HSSFCell stringCell = row2.getCell(1); HSSFCellStyle stringStyle = stringCell.getCellStyle();
        	HSSFCell doubleCell = row2.getCell(3); HSSFCellStyle doubleStyle = doubleCell.getCellStyle();
        	
        	HSSFRow row3 = sheet.getRow(3);        	
        	HSSFCell substringCell = row3.getCell(1); HSSFCellStyle substringStyle = substringCell.getCellStyle();
        	HSSFCell subdoubleCell = row3.getCell(3); HSSFCellStyle subdoubleStyle = subdoubleCell.getCellStyle();
        	
        	HSSFRow row4 = sheet.getRow(4);        	
        	HSSFCell totstringCell = row4.getCell(1); HSSFCellStyle totstringStyle = totstringCell.getCellStyle();
        	HSSFCell totdoubleCell = row4.getCell(3); HSSFCellStyle totdoubleStyle = totdoubleCell.getCellStyle();
        	
            int rowNum = 2;
            int count = 1;
            for (OutputTable wa_out:l_outputTable)
			{
            	HSSFRow row = sheet.getRow(rowNum);
            	if (row==null)
            	{
            		sheet.createRow(rowNum);
            		row = sheet.getRow(rowNum);
            	}
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
            	if (count==l_outputTable.size())
            	{
            		cell1.setCellStyle(totstringStyle);
                	cell2.setCellStyle(totstringStyle);
                	cell3.setCellStyle(totstringStyle);
                	cell4.setCellStyle(totstringStyle);
                	cell5.setCellStyle(totdoubleStyle); 
                	cell6.setCellStyle(totdoubleStyle);
                	cell7.setCellStyle(totdoubleStyle);
                	cell8.setCellStyle(totdoubleStyle);
                	cell9.setCellStyle(totdoubleStyle);
                	cell10.setCellStyle(totdoubleStyle);
                	cell11.setCellStyle(totdoubleStyle);
                	cell12.setCellStyle(totdoubleStyle);
            	}
            	else if (wa_out.getBranchName()==null || wa_out.getBranchName().length()==0)
            	{
            		cell1.setCellStyle(substringStyle);
                	cell2.setCellStyle(substringStyle);
                	cell3.setCellStyle(substringStyle);
                	cell4.setCellStyle(substringStyle);
                	cell5.setCellStyle(subdoubleStyle); 
                	cell6.setCellStyle(subdoubleStyle);
                	cell7.setCellStyle(subdoubleStyle);
                	cell8.setCellStyle(subdoubleStyle);
                	cell9.setCellStyle(subdoubleStyle);
                	cell10.setCellStyle(subdoubleStyle);
                	cell11.setCellStyle(subdoubleStyle);
                	cell12.setCellStyle(subdoubleStyle);
                	
            		
            	}            	 
            	else
            	{
            		cell1.setCellStyle(stringStyle);
                	cell2.setCellStyle(stringStyle);
                	cell3.setCellStyle(stringStyle);
                	cell4.setCellStyle(stringStyle);
                	cell5.setCellStyle(doubleStyle); 
                	cell6.setCellStyle(doubleStyle);
                	cell7.setCellStyle(doubleStyle);
                	cell8.setCellStyle(doubleStyle);
                	cell9.setCellStyle(doubleStyle);
                	cell10.setCellStyle(doubleStyle);
                	cell11.setCellStyle(doubleStyle);
                	cell12.setCellStyle(doubleStyle);
            	}
            		
            	
            	if (wa_out.getBranchName()!=null) cell1.setCellValue(wa_out.getBranchName());
            	if (wa_out.getWaers()!=null) cell2.setCellValue(wa_out.getWaers());
            	
            	cell3.setCellValue(wa_out.getBalance());
            	cell4.setCellValue(wa_out.getDeposit());
            	cell5.setCellValue(wa_out.getDolg());
            	cell6.setCellValue(wa_out.getZablok());
            	cell7.setCellValue(wa_out.getAvans_zapros());
            	cell8.setCellValue(wa_out.getBalance_usd());
            	cell9.setCellValue(wa_out.getDeposit_usd());
            	cell10.setCellValue(wa_out.getDolg_usd());
            	cell11.setCellValue(wa_out.getZablok_usd());
            	cell12.setCellValue(wa_out.getAvans_zapros_usd()); 

            	
            	rowNum++;
            	count++;
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
	
	public void downloadExcelDetail() {
        try {
        	Long a_rep_id=66L;
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(2);
            HSSFCell stringCell = row2.getCell(1); HSSFCellStyle stringStyle = stringCell.getCellStyle();
        	HSSFCell doubleCell = row2.getCell(3); HSSFCellStyle doubleStyle = doubleCell.getCellStyle();
        	
        	HSSFRow row3 = sheet.getRow(3);        	
        	HSSFCell substringCell = row3.getCell(1); HSSFCellStyle substringStyle = substringCell.getCellStyle();
        	HSSFCell subdoubleCell = row3.getCell(3); HSSFCellStyle subdoubleStyle = subdoubleCell.getCellStyle();
        	
        	HSSFRow row4 = sheet.getRow(4);        	
        	HSSFCell totstringCell = row4.getCell(1); HSSFCellStyle totstringStyle = totstringCell.getCellStyle();
        	HSSFCell totdoubleCell = row4.getCell(3); HSSFCellStyle totdoubleStyle = totdoubleCell.getCellStyle();
        	
            int rowNum = 2;
            int count = 1;
            for (DetailTable wa_out:l_detailTable)
			{
            	HSSFRow row = sheet.getRow(rowNum);
            	if (row==null)
            	{
            		sheet.createRow(rowNum);
            		row = sheet.getRow(rowNum);
            	}
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
            	if (count==l_outputTable.size())
            	{
            		cell1.setCellStyle(totstringStyle);
                	cell2.setCellStyle(totstringStyle);
                	cell3.setCellStyle(totstringStyle);
                	cell4.setCellStyle(totstringStyle);
                	cell5.setCellStyle(totdoubleStyle); 
                	cell6.setCellStyle(totdoubleStyle);
                	cell7.setCellStyle(totdoubleStyle);
                	cell8.setCellStyle(totdoubleStyle);
                	cell9.setCellStyle(totdoubleStyle);
                	cell10.setCellStyle(totdoubleStyle);
                	cell11.setCellStyle(totdoubleStyle);
                	cell12.setCellStyle(totdoubleStyle);
            	}
            	else if (wa_out.getFio()==null || wa_out.getFio().length()==0)
            	{
            		cell1.setCellStyle(substringStyle);
                	cell2.setCellStyle(substringStyle);
                	cell3.setCellStyle(substringStyle);
                	cell4.setCellStyle(substringStyle);
                	cell5.setCellStyle(subdoubleStyle); 
                	cell6.setCellStyle(subdoubleStyle);
                	cell7.setCellStyle(subdoubleStyle);
                	cell8.setCellStyle(subdoubleStyle);
                	cell9.setCellStyle(subdoubleStyle);
                	cell10.setCellStyle(subdoubleStyle);
                	cell11.setCellStyle(subdoubleStyle);
                	cell12.setCellStyle(subdoubleStyle);
                	
            		
            	}            	 
            	else
            	{
            		cell1.setCellStyle(stringStyle);
                	cell2.setCellStyle(stringStyle);
                	cell3.setCellStyle(stringStyle);
                	cell4.setCellStyle(stringStyle);
                	cell5.setCellStyle(doubleStyle); 
                	cell6.setCellStyle(doubleStyle);
                	cell7.setCellStyle(doubleStyle);
                	cell8.setCellStyle(doubleStyle);
                	cell9.setCellStyle(doubleStyle);
                	cell10.setCellStyle(doubleStyle);
                	cell11.setCellStyle(doubleStyle);
                	cell12.setCellStyle(doubleStyle);
            	}
            		
            	
            	if (wa_out.getFio()!=null) cell1.setCellValue(wa_out.getFio());
            	if (wa_out.getWaers()!=null) cell2.setCellValue(wa_out.getWaers());
            	
            	cell3.setCellValue(wa_out.getBalance());
            	cell4.setCellValue(wa_out.getDeposit());
            	cell5.setCellValue(wa_out.getDolg());
            	cell6.setCellValue(wa_out.getZablok());
            	cell7.setCellValue(wa_out.getAvans_zapros());
            	cell8.setCellValue(wa_out.getBalance_usd());
            	cell9.setCellValue(wa_out.getDeposit_usd());
            	cell10.setCellValue(wa_out.getDolg_usd());
            	cell11.setCellValue(wa_out.getZablok_usd());
            	cell12.setCellValue(wa_out.getAvans_zapros_usd()); 

            	
            	rowNum++;
            	count++;
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
	
	//*****************************************************************************************************
		
}
