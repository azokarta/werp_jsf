package finance.cashbankoperations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import f4.MonthF4;
import f4.PositionF4;
import general.AppContext; 
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.comparators.PayrollCompareStaffId;
import general.dao.DAOException; 
import general.dao.PayrollDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.services.FinanceServicePayroll;
import general.services.FmglflextService;
import general.services.PayrollService;
import general.tables.Branch;
import general.tables.Bukrs; 
import general.tables.Fmglflext;
import general.tables.Hkont;
import general.tables.Month;
import general.tables.Payroll;
import general.tables.Staff;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext; 

import user.User;

@ManagedBean(name = "hrppBean", eager = true)
@ViewScoped
public class Hrpp implements Serializable{
	/**
	 * 
	 */
	private Calendar curDate = Calendar.getInstance();

	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "HRPP";
	private final static Long transaction_id = (long) 73;
	public static Long getTransactionId() {
		return transaction_id;
	}
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
	// ***************************BranchF4*******************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;
	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}
	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}
	
	private Map<Long, Branch> l_branchMap = new HashMap<Long, Branch>();
	public Map<Long, Branch> getL_branchMap() {
		return l_branchMap;
	}
	public void setL_branchMap(Map<Long, Branch> l_branchMap) {
		this.l_branchMap = l_branchMap;
	}
	

	// ******************************************************************
	//***************************HkontF4***************************
	@ManagedProperty(value="#{hkontF4Bean}")
	private HkontF4 p_hkontF4Bean;
	public HkontF4 getP_hkontF4Bean() {
	    return p_hkontF4Bean;
	}
	public void setP_hkontF4Bean(HkontF4 p_hkontF4Bean) {
	    this.p_hkontF4Bean = p_hkontF4Bean;
	}
	//******************************************************************	
	//***************************MonthF4***************************
	@ManagedProperty(value="#{monthF4Bean}")
	private MonthF4 p_monthF4Bean;
	public MonthF4 getP_monthF4Bean() {
	    return p_monthF4Bean;
	}
	public void setP_monthF4Bean(MonthF4 p_monthF4Bean) {
	    this.p_monthF4Bean = p_monthF4Bean;
	}
	
	List<Month> p_month_list = new ArrayList<Month>();
	public List<Month> getP_month_list(){
		return p_month_list;
	}
	//******************************************************************
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;
	public BukrsF4 getP_bukrsF4Bean() {
	    return p_bukrsF4Bean;
	}
	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
	    this.p_bukrsF4Bean = p_bukrsF4Bean;
	}
		
	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();
	public List<Bukrs> getBukrs_list(){
		return bukrs_list;
	}
	//******************************************************************	
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{positionF4Bean}")
	private PositionF4 p_positionF4Bean;		
	public PositionF4 getP_positionF4Bean() {
	    return p_positionF4Bean;
	}
	public void setP_positionF4Bean(PositionF4 p_positionF4Bean) {
	    this.p_positionF4Bean = p_positionF4Bean;
	}
	//******************************************************************
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4Bean;		
	public ExchangeRateF4 getP_exchangeRateF4Bean() {
	    return p_exchangeRateF4Bean;
	}
	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
	    this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}

	
	//******************************************************************	
	//**********************PostConstruct******************************
	@PostConstruct
	public void init() {
		try 
		{			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Hrpp.transaction_id);
			
			
			for(Month wa_month:p_monthF4Bean.getP_month_list())
			{
				
				if(userData.getU_language().equals(wa_month.getLanguage()))
				{ 
					p_month_list.add(wa_month);
				}
			}
			
			//int index = 0;
			for(Branch wa_branch:p_branchF4Bean.getBranch_list())
			{
				if (wa_branch.getType()!=null && (wa_branch.getType() == 0 || wa_branch.getType() == 3 || wa_branch.getType() == 2))
				{
					
					l_branchMap.put(wa_branch.getBranch_id(), wa_branch);
					//index++;
				}
			}
			setBldat(curDate.getTime());
			
			
					
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			toMainPage();
		}
				
	}
	private boolean amountDisableDmbtr = true;
	private boolean amountDisableWrbtr = true;
	public boolean isAmountDisableDmbtr() {
		return amountDisableDmbtr;
	}

	public void setAmountDisableDmbtr(boolean amountDisableDmbtr) {
		this.amountDisableDmbtr = amountDisableDmbtr;
	}

	public boolean isAmountDisableWrbtr() {
		return amountDisableWrbtr;
	}
	public void setAmountDisableWrbtr(boolean amountDisableWrbtr) {
		this.amountDisableWrbtr = amountDisableWrbtr;
	}
	
    //*****************************************************************
	//***********************Salary Charge*****************************	 
	

	
	//*****************************************************************

	
	//***********************  SEARCH  ******************************	

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
		private Long branch_id;
		private Long staff_id_fired; 
		private String branch_name;
		private String staff_name_fired;

		public String getBranch_name() {
			return branch_name;
		}
		public void setBranch_name(String branch_name) {
			this.branch_name = branch_name;
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
		public String getStaff_name_fired() {
			return staff_name_fired;
		}
		public void setStaff_name_fired(String staff_name_fired) {
			this.staff_name_fired = staff_name_fired;
		}
		public Long getStaff_id_fired() {
			return staff_id_fired;
		}
		public void setStaff_id_fired(Long staff_id_fired) {
			this.staff_id_fired = staff_id_fired;
		}	 
		
	}	
	private List<FiredList> l_fired = new ArrayList<Hrpp.FiredList>();
	public List<FiredList> getL_fired() {
		return l_fired;
	}

	public void setL_fired(List<FiredList> l_fired) {
		this.l_fired = l_fired;
	}

	public class FiredList {
		public FiredList()
		{
			
		};
		private Long staff_id_fired; 
		private String staff_name_fired;
		public Long getStaff_id_fired() {
			return staff_id_fired;
		}
		public void setStaff_id_fired(Long staff_id_fired) {
			this.staff_id_fired = staff_id_fired;
		}
		public String getStaff_name_fired() {
			return staff_name_fired;
		}
		public void setStaff_name_fired(String staff_name_fired) {
			this.staff_name_fired = staff_name_fired;
		}
		
		
		 
		
	}	
	
	
	
	List<Fmglflext> l_fgl = new ArrayList<Fmglflext>();	
	public List<Fmglflext> getL_fgl() {
		return l_fgl;
	}
	public void setL_fgl(List<Fmglflext> l_fgl) {
		this.l_fgl = l_fgl;
	}
	Map<String,Fmglflext> l_fgl_map = new HashMap<String,Fmglflext>();
	public Map<String, Fmglflext> getL_fgl_map() {
		return l_fgl_map;
	}
	public void setL_fgl_map(Map<String, Fmglflext> l_fgl_map) {
		this.l_fgl_map = l_fgl_map;
	}
	String selectedHkontValue = new String();
	public String getSelectedHkontValue() {
		return selectedHkontValue;
	}
	public void setSelectedHkontValue(String selectedHkontValue) {
		this.selectedHkontValue = selectedHkontValue;
	}

	public void to_search(){
		try{
			l_outputTable.clear();
			l_asCash.clear();
			l_asBank.clear();
			p_outputTablePayroll = new OutputTablePayroll();
			l_fgl = new ArrayList<Fmglflext>();	
			l_fgl_map = new HashMap<String,Fmglflext>();
			//System.out.println("Branch:" + selectedPoles[0].getText45());
			//Находим сотрудников кто числится в компании
			//List<Salary> l_salary = new ArrayList<Salary>();
			List<Payroll> l_payroll_schet = new ArrayList<Payroll>();
			List<Payroll> l_payroll_deposit = new ArrayList<Payroll>();
			List<Payroll> l_payroll_avans_zapros = new ArrayList<Payroll>();			
			List<Payroll> l_payroll_zablok = new ArrayList<Payroll>();
			List<Payroll> l_payroll_dolg = new ArrayList<Payroll>();
			List<Payroll> l_payroll_doubt_dolg = new ArrayList<Payroll>();
			//List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();

			
			PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
			SalaryDao salaryDao = (SalaryDao) appContext.getContext().getBean("salaryDao");
			FmglflextService fmglflextService = (FmglflextService) appContext.getContext().getBean("fmglflextService");
			
			
			String dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause + " and sal.bukrs = '"+p_searchTable.getBukrs()+"'";
			dynamicWhereClause = dynamicWhereClause + " and sal.branch_id = "+p_searchTable.getBranch_id();
			if (p_searchTable.getBukrs()==null || p_searchTable.getBukrs().length()==0)
			{
				throw new DAOException("Выберите компанию");
			}
			
			if (p_searchTable.getBranch_id()==null || p_searchTable.getBranch_id()==0)
			{
				throw new DAOException("Выберите филиал");
			}
			
			
			//l_salary = salaryDao.findDynamic2(dynamicWhereClause);
			List<Long> l_staff_id = new ArrayList<Long>();
			List<Long> l_customer_id = new ArrayList<Long>();
			Map<Long,OutputTable> l_outputTable_map = new HashMap<Long,OutputTable>();
			int index = 0;
			List<Object[]> results =  new ArrayList<Object[]>();
			
			if (p_searchTable.getStaff_id_fired()!=null && p_searchTable.getStaff_id_fired()>0L)
			{
				dynamicWhereClause = dynamicWhereClause + " and sal.staff_id = "+p_searchTable.getStaff_id_fired();
				results = salaryDao.findDynamicFired(dynamicWhereClause);
			}
			else
			{
				results = salaryDao.findDynamic3(dynamicWhereClause);
			}
			
			
			for(Object[] result:results)
			{
//				st.staff_id,"
//						+" LISTAGG(p.text, ', ') WITHIN GROUP (ORDER BY p.text) position_name,"
//						+" st.customer_id,"
//						+" st.iin_bin,"
//						+" st.lastname,"
//						+" st.firstname,"
//						+" st.middlename"
				
				
				OutputTable wa_out = new OutputTable();
				wa_out.setIndex(index);
				if (result[0]!=null) wa_out.setStaff_id(Long.parseLong(String.valueOf(result[0])));
				if (result[1]!=null) wa_out.setPosition_name(String.valueOf(result[1]));
				if (result[2]!=null) wa_out.setCustomer_id(Long.parseLong(String.valueOf(result[2])));
				if (result[3]!=null) wa_out.setIin_bin(String.valueOf(result[3]));
				if (result[4]!=null) wa_out.setLastname(String.valueOf(result[4]));
				if (result[5]!=null) wa_out.setFirstname(String.valueOf(result[5]));
				if (result[6]!=null) wa_out.setMiddlename(String.valueOf(result[6]));
				wa_out.setStaff_name(Validation.returnFio(wa_out.getFirstname(), wa_out.getLastname(), wa_out.getMiddlename()));
				
				
				
				wa_out.setBukrs(p_searchTable.getBukrs());
				wa_out.setBranch_id(p_searchTable.getBranch_id());
				l_outputTable.add(wa_out);
				l_outputTable_map.put(wa_out.getCustomer_id(), wa_out);
				
				l_staff_id.add(wa_out.getStaff_id());
				l_customer_id.add(wa_out.getCustomer_id());
			}
			//System.out.println(l_salary.size()); 
			// add elements to al, including duplicates
					
			if (l_staff_id.size()>0)
			{	
				Set<Long> hs = new HashSet<Long>();
				hs.addAll(l_staff_id);
				l_staff_id.clear();
				l_staff_id.addAll(hs);
				l_payroll_schet = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
				l_payroll_deposit = payrollDao.findByBukrsBranchAllDeposit(l_staff_id,p_searchTable.getBukrs());
				l_payroll_avans_zapros = payrollDao.findByBukrsBranchAvansZapros(l_staff_id,  GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
				l_payroll_zablok = payrollDao.findByBukrsBranchAllZablok(l_staff_id,  GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
				l_payroll_dolg = payrollDao.findByBukrsBranchAllDolg(l_staff_id,  GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
				l_payroll_doubt_dolg = payrollDao.findByBukrsBranchAllDoubtfulDolg(l_staff_id,  GeneralUtil.getSQLDate(curDate),p_searchTable.getBukrs());
				//List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();
			}
			index = -1;
			for(OutputTable wa_out:l_outputTable)
			{
				Payroll searchKey = new Payroll();
				searchKey.setStaff_id(wa_out.getStaff_id());
				index = Collections.binarySearch(l_payroll_schet, searchKey,new PayrollCompareStaffId());
				while (index > 0 && new PayrollCompareStaffId().compare(l_payroll_schet.get(index), l_payroll_schet.get(index-1)) == 0) {
				    index--;
				}
				boolean loop = true;
				while (index>=0 && index<l_payroll_schet.size() && loop)
  				{
					
					Payroll wa_payroll = l_payroll_schet.get(index); 

  					if (wa_payroll.getStaff_id().equals(wa_out.getStaff_id()))
  					{
  						wa_out.getL_payroll_schet().add(wa_payroll);
  						
  						index++;
  						loop = true;
  							
  					}
  					else
  					{ 
  						index = -1;
  						loop = false;
  					}
  				}

				
			} 
			index = -1;
			for(OutputTable wa_out:l_outputTable)
			{
				Payroll searchKey = new Payroll();
				searchKey.setStaff_id(wa_out.getStaff_id());
				index = Collections.binarySearch(l_payroll_deposit, searchKey,new PayrollCompareStaffId());
				while (index > 0 && new PayrollCompareStaffId().compare(l_payroll_deposit.get(index), l_payroll_deposit.get(index-1)) == 0) {
				    index--;
				}
				boolean loop = true;
				while (index>=0 && index<l_payroll_deposit.size() && loop)
  				{
					
					Payroll wa_payroll = l_payroll_deposit.get(index); 

  					if (wa_payroll.getStaff_id().equals(wa_out.getStaff_id()))
  					{
  						wa_out.getL_payroll_deposit().add(wa_payroll);
  						
  						index++;
  						loop = true;
  							
  					}
  					else
  					{ 
  						index = -1;
  						loop = false;
  					}
  				}

				
			} 
			index = -1;
			for(OutputTable wa_out:l_outputTable)
			{
				Payroll searchKey = new Payroll();
				searchKey.setStaff_id(wa_out.getStaff_id());
				index = Collections.binarySearch(l_payroll_zablok, searchKey,new PayrollCompareStaffId());
				while (index > 0 && new PayrollCompareStaffId().compare(l_payroll_zablok.get(index), l_payroll_zablok.get(index-1)) == 0) {
				    index--;
				}
				boolean loop = true;
				while (index>=0 && index<l_payroll_zablok.size() && loop)
  				{
					
					Payroll wa_payroll = l_payroll_zablok.get(index); 

  					if (wa_payroll.getStaff_id().equals(wa_out.getStaff_id()))
  					{
  						wa_out.getL_payroll_zablok().add(wa_payroll);
  						
  						index++;
  						loop = true;
  							
  					}
  					else
  					{ 
  						index = -1;
  						loop = false;
  					}
  				}

				
			}
			
			index = -1;
			for(OutputTable wa_out:l_outputTable)
			{
				Payroll searchKey = new Payroll();
				searchKey.setStaff_id(wa_out.getStaff_id());
				index = Collections.binarySearch(l_payroll_avans_zapros, searchKey,new PayrollCompareStaffId());
				while (index > 0 && new PayrollCompareStaffId().compare(l_payroll_avans_zapros.get(index), l_payroll_avans_zapros.get(index-1)) == 0) {
				    index--;
				}
				boolean loop = true;
				while (index>=0 && index<l_payroll_avans_zapros.size() && loop)
  				{
					
					Payroll wa_payroll = l_payroll_avans_zapros.get(index); 

  					if (wa_payroll.getStaff_id().equals(wa_out.getStaff_id()))
  					{
  						wa_out.getL_payroll_avans_zapros().add(wa_payroll);
  						if (wa_payroll.getApprove()==4)
  						{
  							wa_out.getL_payroll_avans_odob().add(wa_payroll);
  						}
  						index++;
  						loop = true;
  							
  					}
  					else
  					{ 
  						index = -1;
  						loop = false;
  					}
  				}

				
			} 
			
			
			index = -1;
			for(OutputTable wa_out:l_outputTable)
			{
				Payroll searchKey = new Payroll();
				searchKey.setStaff_id(wa_out.getStaff_id());
				index = Collections.binarySearch(l_payroll_dolg, searchKey,new PayrollCompareStaffId());
				while (index > 0 && new PayrollCompareStaffId().compare(l_payroll_dolg.get(index), l_payroll_dolg.get(index-1)) == 0) {
				    index--;
				}
				boolean loop = true;
				while (index>=0 && index<l_payroll_dolg.size() && loop)
  				{
					
					Payroll wa_payroll = l_payroll_dolg.get(index); 

  					if (wa_payroll.getStaff_id().equals(wa_out.getStaff_id()))
  					{
  						wa_out.getL_payroll_dolg().add(wa_payroll);
  						index++;
  						loop = true;
  							
  					}
  					else
  					{ 
  						index = -1;
  						loop = false;
  					}
  				}

				
			}
			
			index = -1;
			for(OutputTable wa_out:l_outputTable)
			{
				Payroll searchKey = new Payroll();
				searchKey.setStaff_id(wa_out.getStaff_id());
				index = Collections.binarySearch(l_payroll_doubt_dolg, searchKey,new PayrollCompareStaffId());
				while (index > 0 && new PayrollCompareStaffId().compare(l_payroll_doubt_dolg.get(index), l_payroll_doubt_dolg.get(index-1)) == 0) {
				    index--;
				}
				boolean loop = true;
				while (index>=0 && index<l_payroll_doubt_dolg.size() && loop)
  				{
					
					Payroll wa_payroll = l_payroll_doubt_dolg.get(index); 

  					if (wa_payroll.getStaff_id().equals(wa_out.getStaff_id()))
  					{
  						wa_out.getL_payroll_doubt_dolg().add(wa_payroll);
  						index++;
  						loop = true;
  							
  					}
  					else
  					{ 
  						index = -1;
  						loop = false;
  					}
  				}

				
			}
			
			
			//Находим остатки на кассах и банковских счетах
			//System.out.println(p_headerPanelGrid1.getBranch_id());

			
			List<String> sl_hkont = new ArrayList<String>();
//			System.out.println(p_searchTable.getBukrs()+String.valueOf(p_searchTable.getBranch_id()));
//			System.out.println(p_hkontF4Bean.getL_hkont_cash_bank_map().get(p_searchTable.getBukrs()+String.valueOf(p_searchTable.getBranch_id())).size());
			
			//List<Hkont> wal_hkont = p_hkontF4Bean.getL_hkont_cash_bank_map().get(p_searchTable.getBranch_id());
			//for (Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
			
			for (Hkont wa_hkont:p_hkontF4Bean.getL_hkont_cash_bank_map().get(p_searchTable.getBukrs()+String.valueOf(p_searchTable.getBranch_id())))
			{
				if ( wa_hkont.getBukrs().equals(p_searchTable.getBukrs()) && wa_hkont.getHkont().startsWith("1010"))
				{ 
					sl_hkont.add(wa_hkont.getHkont());
					//System.out.println(wa_hkont.getHkont());
				}
				else if ( wa_hkont.getBukrs().equals(p_searchTable.getBukrs()) && wa_hkont.getHkont().startsWith("1030"))
				{ 
					sl_hkont.add(wa_hkont.getHkont());
					//System.out.println(wa_hkont.getHkont());
				}
				
			}
			
			if (sl_hkont.size()==0)
			{
				throw new DAOException("Branch does not have Cash or Bank Accounts");
			}
			else
			{ 
				// removing duplicates
				Set<String> hs = new HashSet<>();
				hs.addAll(sl_hkont);
				sl_hkont.clear();
				sl_hkont.addAll(hs);
				
				l_fgl = fmglflextService.getAccountsBalance(p_searchTable.getBukrs(), curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH)+1, sl_hkont);
				for(Fmglflext wa_fgl:l_fgl)
				{
					if (wa_fgl.getBeg_amount()>0)
					{
						l_fgl_map.put(wa_fgl.getHkont(), wa_fgl);
					}
					
				}
				
				//l_fgl = fmglflextDao.findByBukrsGjahrHkontList(p_searchTable.getBukrs(),curDate.get(Calendar.YEAR),sl_hkont);
			}
			sortByOrderNo();
			/*
			Map<String,AccountStatement> l_as_map = new HashMap<String,AccountStatement>();
			for(Fmglflext wa_fmgl: l_fgl)
  			{
				AccountStatement wa_as = l_as_map.get(wa_fmgl.getHkont());
				AccountStatement wa_as2 = new AccountStatement(); 
  				double amount = 0;
  				amount  = amount + wa_fmgl.getBeg_amount();
  				for(int i = 1; i<=curDate.get(Calendar.MONTH)+1;i++)
  				{ 
	  				switch (i) {
	  					case 1:  amount = amount + wa_fmgl.getMonth1();
	  	                    break;
	  					case 2:  amount = amount + wa_fmgl.getMonth2();
	  	                    break;
	  					case 3:  amount = amount + wa_fmgl.getMonth3();
	  	                    break;
	  					case 4:  amount = amount + wa_fmgl.getMonth4();
	  	                    break;
	  					case 5:  amount = amount + wa_fmgl.getMonth5();
	  	                    break;
	  					case 6:  amount = amount + wa_fmgl.getMonth6();
	  	                    break;
	  					case 7:  amount = amount + wa_fmgl.getMonth7();
	  	                    break;
	  					case 8:  amount = amount + wa_fmgl.getMonth8();
	  	                    break;
	  					case 9:  amount = amount + wa_fmgl.getMonth9();
	  	                    break;
	  					case 10: amount = amount + wa_fmgl.getMonth10();
	  	                    break;
	  					case 11: amount = amount + wa_fmgl.getMonth11();
	  	                    break;
	  					case 12: amount = amount + wa_fmgl.getMonth12();
	  	                    break;
	  					
	  					}
  				}
  					
  				if (wa_fmgl.getDrcrk().equals("S"))
				{
  					wa_as2.setHkont(wa_fmgl.getHkont());
  					wa_as2.setWaers(wa_fmgl.getWaers());
  					wa_as2.setAmount(wa_as2.getAmount()+amount); 
				}
				else if (wa_fmgl.getDrcrk().equals("H"))
				{
					wa_as2.setHkont(wa_fmgl.getHkont());
  					wa_as2.setWaers(wa_fmgl.getWaers());
  					wa_as2.setAmount(wa_as2.getAmount()-amount);
				}
  				
				if (wa_as!=null)
				{
					wa_as.setAmount(wa_as.getAmount()+wa_as2.getAmount());
				}
				else
				{					
	  				l_as_map.put(wa_fmgl.getHkont(), wa_as2);					
				}
				
  				
  			}
			
			Iterator it = l_as_map.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        AccountStatement wa_as3 = new AccountStatement();
		        wa_as3 = (AccountStatement) pair.getValue();
		        if (wa_as3.getHkont().startsWith("1010"))
		        {
		        	l_asCash.add(wa_as3);
		        }
		        else if (wa_as3.getHkont().startsWith("1030"))
		        {
		        	l_asBank.add(wa_as3);
		        }
		        it.remove(); // avoids a ConcurrentModificationException
		    }
			*/
			//System.out.println("Fmgl "+l_fgl.size()+" "+sl_hkont.size());
		    
			
			/*
			//Находим дебиторские и кредиторские задолженности
		    if (l_customer_id.size()>0)
			{	
		    	dynamicWhereClause = "";
				Set<Long> hs = new HashSet<Long>();
				hs.addAll(l_customer_id);
				l_customer_id.clear();
				l_customer_id.addAll(hs);
				l_customer_id.removeAll(Collections.singleton(null));
				BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
				dynamicWhereClause = dynamicWhereClause + " awkey is null and awtyp IN (1,2) and ((waers = 'USD' and dmbtr <> dmbtr_paid) or (waers <> 'USD' and wrbtr <> wrbtr_paid)) ";

				l_bkpf = bkpfDao.findBkpfHrpp(l_customer_id, dynamicWhereClause);
				
				int indexBsid = 0;
			    int indexBsik = 0;
			    for(Bkpf wa_bkpf:l_bkpf)
			    {
			    	
			    	
			    	OutputTable wa_out = l_outputTable_map.get(wa_bkpf.getCustomer_id());
			    	if (wa_out!=null)
			    	{
			    		
			    		if (wa_bkpf.getAwtyp()==1 && wa_out.getCustomer_id().equals(wa_bkpf.getCustomer_id()) && !wa_bkpf.getBlart().equals("GC"))
				    	{	
			    			
				    		OutputTableBsikBsid wa_otbb = new OutputTableBsikBsid();
				    		wa_otbb.setP_bkpf_old(wa_bkpf);
				    		wa_otbb.setIndex(indexBsid);
				    		if (wa_bkpf.getWaers().equals("USD"))
				    		{
				    			wa_otbb.setAmount_old(wa_bkpf.getDmbtr()-wa_bkpf.getDmbtr_paid());
				    		}
				    		else
				    		{
				    			wa_otbb.setAmount_old(wa_bkpf.getWrbtr()-wa_bkpf.getWrbtr_paid());
				    		}
				    		
				    		wa_otbb.setIndex(wa_out.getL_bkpf_bsidCustomer().size());
				    		wa_out.getL_bkpf_bsidCustomer().add(wa_otbb);
				    		
				    	}
				    	else if (wa_bkpf.getAwtyp()==2 && wa_out.getCustomer_id().equals(wa_bkpf.getCustomer_id()))
				    	{	
				    		OutputTableBsikBsid wa_otbb = new OutputTableBsikBsid();
				    		wa_otbb.setP_bkpf_old(wa_bkpf);
				    		wa_otbb.setIndex(indexBsik);
				    		if (wa_bkpf.getWaers().equals("USD"))
				    		{
				    			wa_otbb.setAmount_old(wa_bkpf.getDmbtr()-wa_bkpf.getDmbtr_paid());
				    		}
				    		else
				    		{
				    			wa_otbb.setAmount_old(wa_bkpf.getWrbtr()-wa_bkpf.getWrbtr_paid());
				    		}
				    		wa_otbb.setIndex(wa_out.getL_bkpf_bsikCustomer().size());
				    		wa_out.getL_bkpf_bsikCustomer().add(wa_otbb);
				    	}
			    	}
			    			    	 
			    	
			    }
				//System.out.println(l_bkpf.size());
			}*/
		    
		    
			
			tabindex = 1;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");
  			reqCtx.update("form:cashBankDaialog");
  			reqCtx.update("form:tabView:viewButton");
  			reqCtx.update("form:tabView:updateButton");
			//System.out.println(count);
		}
		catch (DAOException ex)
		{
			addMessage("Info  ",ex.getMessage());  
		}
		
	}
	public void removeStaff()
	{
		try{
//			p_searchTable.setStaff_id(null);
//			p_searchTable.setStaff_name("");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView:p_staff");
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			//System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	//*****************************************************************	

	//****************************Search Parameters**********************************






	
	public void onSelectOutputTable()
	{
		try{
			
			//System.out.println("HashMap1: "+l_payroll_map);
			//l_payroll_map.clear();

			p_outputTablePayroll = new OutputTablePayroll();
			

			p_outputTablePayroll.getL_payroll_schet().addAll(selectedOutputTable.getL_payroll_schet());
			p_outputTablePayroll.getL_payroll_zablok().addAll(selectedOutputTable.getL_payroll_zablok());
			p_outputTablePayroll.getL_payroll_deposit().addAll(selectedOutputTable.getL_payroll_deposit());
			p_outputTablePayroll.getL_payroll_avans_zapros().addAll(selectedOutputTable.getL_payroll_avans_zapros());
			p_outputTablePayroll.getL_payroll_avans_odob().addAll(selectedOutputTable.getL_payroll_avans_odob());
			p_outputTablePayroll.getL_payroll_dolg().addAll(selectedOutputTable.getL_payroll_dolg());
			p_outputTablePayroll.getL_payroll_doubt_dolg().addAll(selectedOutputTable.getL_payroll_doubt_dolg());
			p_outputTablePayroll.setStaff_name(selectedOutputTable.getStaff_name());
			//System.out.println(selectedOutputTable.getL_payroll_avans_odob().size()+" zzzzzzzzzzz");
			summa = 0;
			selectedAvansOdob_payroll_id=0L;
			p_bkpf_new_dmbtr_bool = false;
			//selectedHkontValue = "";
			//for(Payroll wa_payroll:selectedOutputTable.getL_payroll())
			//{
				//p_outputTablePayroll.getL_payroll().add(wa_payroll);
				/*
				//System.out.println(wa_payroll.getPayroll_id());
				Payroll wa_payroll2 = l_payroll_map.get(wa_payroll.getWaers());
				if (wa_payroll2!=null)
				{
					//System.out.println("found");
					if(wa_payroll.getDrcrk().equals("H"))
					{
						wa_payroll2.setDmbtr(wa_payroll2.getDmbtr()-wa_payroll.getDmbtr());
					}
					else
					{
						wa_payroll2.setDmbtr(wa_payroll2.getDmbtr()+wa_payroll.getDmbtr());
					}
				}
				else
				{
					//System.out.println("Not found");
					Payroll wa_payroll3 = new Payroll();
					wa_payroll3.setDrcrk(wa_payroll.getDrcrk());
					wa_payroll3.setDmbtr(wa_payroll.getDmbtr());
					wa_payroll3.setWaers(wa_payroll.getWaers());
					if(wa_payroll.getDrcrk().equals("H"))
					{
						wa_payroll3.setDmbtr(wa_payroll.getDmbtr()*-1);
					}
					l_payroll_map.put(wa_payroll.getWaers(), wa_payroll3);
					
				}*/
				
			//}
			//System.out.println("HashMap2: "+l_payroll_map);
			/*Iterator it = l_payroll_map.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        p_outputTablePayroll.getL_payroll().add((Payroll) pair.getValue());
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }*/

		    //selectedOutputTable = new OutputTable();		    
		    //l_payroll_map.clear();
		    //System.out.println("HashMap3: "+l_payroll_map);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView:staffPayroll");
  			reqCtx.update("form:tabView:otschet");
  			reqCtx.update("form:tabView:otzablok");
  			reqCtx.update("form:tabView:otdeposit");
  			reqCtx.update("form:tabView:otzarposavans");
  			reqCtx.update("form:tabView:otdolg");
  			reqCtx.update("form:tabView:otDoubtdolg");
  			reqCtx.update("form:tabView:viewButton1");
  			reqCtx.update("form:tabView:viewButton2");
  			reqCtx.update("form:tabView:viewButton");
  			reqCtx.update("form:tabView:updateButton");  			
  			reqCtx.update("form:p_bkpf_new_dmbtr");
  			reqCtx.update("form:selectedAvansOdob");
  			reqCtx.update("form:p_bkpf_new_dmbtr_bool");
  			
  			//reqCtx.update("form:selectedHkontValue");
  			
		}
		catch (DAOException ex)
		{
			addMessage("Info  ",ex.getMessage());  
		}
	}
	
	private Long selectedAvansOdob_payroll_id = 0L;
	
	public Long getSelectedAvansOdob_payroll_id() {
		return selectedAvansOdob_payroll_id;
	}

	public void setSelectedAvansOdob_payroll_id(Long selectedAvansOdob_payroll_id) {
		this.selectedAvansOdob_payroll_id = selectedAvansOdob_payroll_id;
	}
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}
	
	private OutputTable selectedOutputTable = new OutputTable();
	public OutputTable getSelectedOutputTable() {
		return selectedOutputTable;
	}
	public void setSelectedOutputTable(OutputTable selectedOutputTable) {
		this.selectedOutputTable = selectedOutputTable;
	}
	
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private int index;
		private Long staff_id;
		private String staff_name;
		private Long customer_id;
		private String iin_bin;
		private String position_name;
		private String position_name_short;
		private String bukrs;
		private Long branch_id;
		private String lastname;
		private String firstname;
		private String middlename;
		private List<Payroll> l_payroll_schet = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_deposit = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_zablok = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_avans_zapros = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_dolg = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_doubt_dolg = new ArrayList<Payroll>();
		
		
		
		
		public List<Payroll> getL_payroll_doubt_dolg() {
			return l_payroll_doubt_dolg;
		}
		public void setL_payroll_doubt_dolg(List<Payroll> l_payroll_doubt_dolg) {
			this.l_payroll_doubt_dolg = l_payroll_doubt_dolg;
		}
		public List<Payroll> getL_payroll_zablok() {
			return l_payroll_zablok;
		}
		public void setL_payroll_zablok(List<Payroll> l_payroll_zablok) {
			this.l_payroll_zablok = l_payroll_zablok;
		}
		public List<Payroll> getL_payroll_avans_odob() {
			return l_payroll_avans_odob;
		}
		public void setL_payroll_avans_odob(List<Payroll> l_payroll_avans_odob) {
			this.l_payroll_avans_odob = l_payroll_avans_odob;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
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
		
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		public String getIin_bin() {
			return iin_bin;
		}
		public void setIin_bin(String iin_bin) {
			this.iin_bin = iin_bin;
		}
		public String getPosition_name() {
			return position_name;
		}
		public void setPosition_name(String position_name) {
			this.position_name = position_name;
			if (position_name.length()>20)
			{
				this.position_name_short = position_name.substring(0,20);
			}
			else
			{
				this.position_name_short = position_name;
			}
			
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
		public List<Payroll> getL_payroll_avans_zapros() {
			return l_payroll_avans_zapros;
		}
		public void setL_payroll_avans_zapros(List<Payroll> l_payroll_avans_zapros) {
			this.l_payroll_avans_zapros = l_payroll_avans_zapros;
		}
		public List<Payroll> getL_payroll_dolg() {
			return l_payroll_dolg;
		}
		public void setL_payroll_dolg(List<Payroll> l_payroll_dolg) {
			this.l_payroll_dolg = l_payroll_dolg;
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
		public String getLastname() {
			return lastname;
		}
		public void setLastname(String lastname) {
			this.lastname = lastname;
		}
		public String getFirstname() {
			return firstname;
		}
		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}
		public String getMiddlename() {
			return middlename;
		}
		public void setMiddlename(String middlename) {
			this.middlename = middlename;
		}
		public String getPosition_name_short() {
			return position_name_short;
		}
		public void setPosition_name_short(String position_name_short) {
			this.position_name_short = position_name_short;
		}
		

		
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

		private List<Payroll> l_payroll_doubt_dolg = new ArrayList<Payroll>();
		
		
		public List<Payroll> getL_payroll_doubt_dolg() {
			return l_payroll_doubt_dolg;
		}
		public void setL_payroll_doubt_dolg(List<Payroll> l_payroll_doubt_dolg) {
			this.l_payroll_doubt_dolg = l_payroll_doubt_dolg;
		}
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

	

	//**************************************************************************
	//Payroll
	
	
	
	private AccountStatement selectedAccountStatemntCash = new AccountStatement();	
	public AccountStatement getSelectedAccountStatemntCash() {
		return selectedAccountStatemntCash;
	}
	public void setSelectedAccountStatemntCash(
			AccountStatement selectedAccountStatemntCash) {
		this.selectedAccountStatemntCash = selectedAccountStatemntCash;
	}

	private AccountStatement selectedAccountStatemntBank = new AccountStatement();
	public AccountStatement getSelectedAccountStatemntBank() {
		return selectedAccountStatemntBank;
	}
	public void setSelectedAccountStatemntBank(
			AccountStatement selectedAccountStatemntBank) {
		this.selectedAccountStatemntBank = selectedAccountStatemntBank;
	}
	
	private AccountStatement selectedAccountStatemnt = new AccountStatement();	
	public AccountStatement getSelectedAccountStatemnt() {
		return selectedAccountStatemnt;
	}
	public void setSelectedAccountStatemnt(AccountStatement selectedAccountStatemnt) {
		this.selectedAccountStatemnt = selectedAccountStatemnt;
	}
	
	private List<AccountStatement> l_asCash = new ArrayList<AccountStatement>();
	public List<AccountStatement> getL_asCash() {
		return l_asCash;
	}
	public void setL_asCash(List<AccountStatement> l_asCash) {
		this.l_asCash = l_asCash;
	}

	private List<AccountStatement> l_asBank = new ArrayList<AccountStatement>();
	public List<AccountStatement> getL_asBank() {
		return l_asBank;
	}
	public void setL_asBank(List<AccountStatement> l_asBank) {
		this.l_asBank = l_asBank;
	}

	public class AccountStatement
	{
		public AccountStatement()
		{
			
		}
		private String hkont = "";
		private String hkont_name = "";
		private String waers = "";
		private double amount = 0; 
		 
		public String getHkont() {
			return hkont;
		}
		public void setHkont(String hkont) {
			this.hkont = hkont;
		}
		public String getHkont_name() {
			return hkont_name;
		}
		public void setHkont_name(String hkont_name) {
			this.hkont_name = hkont_name;
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
		
	}

	//	
  	//Подотчет*************************************************************************

	//********************************************************************************************
	//************************Save Bonus*****************************


	
	private String bktxt="";
	private double summa=0;
	private Date bldat;
	private boolean p_bkpf_new_dmbtr_bool=false;
	
	public boolean isP_bkpf_new_dmbtr_bool() {
		return p_bkpf_new_dmbtr_bool;
	}

	public void setP_bkpf_new_dmbtr_bool(boolean p_bkpf_new_dmbtr_bool) {
		this.p_bkpf_new_dmbtr_bool = p_bkpf_new_dmbtr_bool;
	}

	public String getBktxt() {
	return bktxt;
	}
	
	public void setBktxt(String bktxt) {
	this.bktxt = bktxt;
	}
	
	public double getSumma() {
	return summa;
	}
	
	public void setSumma(double summa) {
	this.summa = summa;
	}
	
	public Date getBldat() {
		return bldat;
	}

	public void setBldat(Date bldat) {
		this.bldat = bldat;
	}

	public void disableSumma()
	{
		if (selectedAvansOdob_payroll_id>0)
		{
			p_bkpf_new_dmbtr_bool = true;
		}
		else
		{
			p_bkpf_new_dmbtr_bool = false;
		}
		summa = 0;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:p_bkpf_new_dmbtr");
			
	}
	//zapros na avans
	public void avans(){		
		try{
			PermissionController.canWrite(userData, Hrpp.transaction_id);
			//System.out.println(selectedHkontValue);
			Fmglflext wa_fgl = l_fgl_map.get(selectedHkontValue);
			if (wa_fgl==null||wa_fgl.getWaers()==null||wa_fgl.getHkont()==null)
			{				
				throw new DAOException(Helper.getErrorMessage(60L, userData.getU_language()));
			}
			if (selectedOutputTable.getStaff_id()==null || selectedOutputTable.getStaff_id()==0)
			{
				throw new DAOException(Helper.getErrorMessage(63L, userData.getU_language()));
			}
			if (summa==0 || summa<0)
			{
				throw new DAOException(Helper.getErrorMessage(61L, userData.getU_language()));
			}
			
			if (summa>wa_fgl.getBeg_amount())
			{
				throw new DAOException(Helper.getErrorMessage(62L, userData.getU_language()));
			}
			double dmbtr = 0;
			for(Payroll wa_prl:selectedOutputTable.getL_payroll_schet())
			{
				if (wa_prl.getWaers().equals(wa_fgl.getWaers()))
				{
					dmbtr = dmbtr + wa_prl.getDmbtr();
				}
				
			}
			
			if (summa<=dmbtr)
			{
				throw new DAOException(Helper.getErrorMessage(64L, userData.getU_language()));
			}
			
			if (selectedOutputTable.getL_payroll_avans_zapros().size()>0)
			{
				throw new DAOException(Helper.getErrorMessage(65L, userData.getU_language()));
				
			}
			
			
				Payroll new_prl = new Payroll();
				new_prl.setBukrs(p_searchTable.getBukrs());
				new_prl.setStaff_id(selectedOutputTable.getStaff_id());
				new_prl.setDrcrk("H");
				new_prl.setBranch_id(p_searchTable.getBranch_id());
				new_prl.setDmbtr(summa);
				Calendar curDate = Calendar.getInstance();
				new_prl.setGjahr(curDate.get(Calendar.YEAR));
				new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
				new_prl.setApprove(3);
				new_prl.setPayroll_date(curDate.getTime());
				new_prl.setBldat(curDate.getTime());
				new_prl.setWaers(wa_fgl.getWaers());
				new_prl.setText45(bktxt);
				PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService");
				payrollService.createNew(new_prl,userData.getUserid(),false,Hrpp.transaction_code,0);
				
				RequestContext context = RequestContext.getCurrentInstance();
				context.update("form:selectedHkontValue");
				context.execute("PF('PaySalaryWidget').hide();");			
				selectedOutputTable = new OutputTable();
				//p_searchTable.setBukrs(bukrs);
				//p_searchTable.setBranch_id(branch_id);
				selectedHkontValue  = new String();
				bktxt = new String();
				summa = 0;
				to_search();
			

		
			//l_outputTable.clear();
		}
		catch (DAOException ex)
		{
			
			addMessage("Info",ex.getMessage()); 
			//System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	public void to_save(){		
		try{
			PermissionController.canWrite(userData, Hrpp.transaction_id);
			Fmglflext wa_fgl = l_fgl_map.get(selectedHkontValue);
			if (wa_fgl==null||wa_fgl.getWaers()==null||wa_fgl.getHkont()==null)
			{				
				throw new DAOException(Helper.getErrorMessage(60L, userData.getU_language()));
			}
			if (selectedOutputTable.getStaff_id()==null || selectedOutputTable.getStaff_id()==0)
			{
				throw new DAOException(Helper.getErrorMessage(63L, userData.getU_language()));
			}
			
			double dmbtr = 0;
			for(Payroll wa_prl:selectedOutputTable.getL_payroll_schet())
			{
				if (wa_prl.getWaers().equals(wa_fgl.getWaers()))
				{
					dmbtr = dmbtr + wa_prl.getDmbtr();
				}
				
			}
			
			if (selectedAvansOdob_payroll_id>0)
			{
				for(Payroll wa_prl:selectedOutputTable.getL_payroll_avans_odob())
				{
					if (wa_prl.getPayroll_id().equals(selectedAvansOdob_payroll_id))
					{
						if (!wa_prl.getWaers().equals(wa_fgl.getWaers()))
						{
							throw new DAOException(Helper.getErrorMessage(66L, userData.getU_language()));
						}
						summa = wa_prl.getDmbtr();
						
					}
				}
				
			}
			if (summa==0 || summa<0)
			{
				throw new DAOException(Helper.getErrorMessage(61L, userData.getU_language()));
			}
			if (summa>wa_fgl.getBeg_amount())
			{
				throw new DAOException(Helper.getErrorMessage(62L, userData.getU_language()));
			}
			
			if (selectedAvansOdob_payroll_id==0)
			{
				if (dmbtr==0 || dmbtr<0)
				{
					throw new DAOException(Helper.getErrorMessage(67L, userData.getU_language()));
				}
				
				if (summa>dmbtr)
				{
					throw new DAOException(Helper.getErrorMessage(67L, userData.getU_language()));
				}
				
			}
			
			if (bldat.after(curDate.getTime()))
			{
				throw new DAOException(Helper.getErrorMessage(69L, userData.getU_language()));
			}
			
				
			FinanceServicePayroll fSP = (FinanceServicePayroll) appContext.getContext().getBean("financeServicePayroll");
			//fSP.hrppPayrollPayments(p_bkpf_old, p_bkpf_new, l_bseg_new, l_hpt,selectedOutputTable.getStaff_id());
			fSP.hrppPayrollPayments(p_searchTable.getBukrs(), p_searchTable.getBranch_id(), selectedOutputTable.getCustomer_id(), selectedOutputTable.getStaff_id(), 
					transaction_code, userData.getUserid(), summa, wa_fgl.getWaers(), wa_fgl.getHkont(),selectedAvansOdob_payroll_id,bktxt);
			//toMainPage();
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("form:selectedHkontValue");
			context.execute("PF('PaySalaryWidget').hide();");			
			selectedOutputTable = new OutputTable();
			//p_searchTable.setBukrs(bukrs);
			//p_searchTable.setBranch_id(branch_id);
			selectedHkontValue  = new String();
			bktxt = new String();
			summa = 0;
			selectedAvansOdob_payroll_id = 0L;
			to_search();
			addMessage("Info",Helper.getErrorMessage(101L, userData.getU_language()));
			//l_outputTable.clear();
		}
		catch (DAOException ex)
		{
			
			addMessage("Info",ex.getMessage()); 
			//System.out.println(ex.getMessage());
			//toMainPage();
		}
	}	
	//***********************Others************************************	 
	public void toMainPage() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath() + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}
	
	public void addMessage(String summary, String detail) {
	   
       FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
       FacesContext.getCurrentInstance().addMessage(null, message);
       //System.out.println(message.getDetail());
       RequestContext reqCtx = RequestContext.getCurrentInstance();
       reqCtx.update("form:messages");
   }

	//*****************************************************************	
	// **************************Employee*******************************
	private List<Staff> p_staff_list = new ArrayList<Staff>();
	public List<Staff> getP_staff_list() {
		return p_staff_list;
	}
	public void setP_staff_list(List<Staff> p_staff_list) {
		this.p_staff_list = p_staff_list;
	}

	private Staff selectedStaff = new Staff();
	public Staff getSelectedStaff() {
		return selectedStaff;
	}
	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	private Staff searchStaff = new Staff();
	public Staff getSearchStaff() {
		return searchStaff;
	}
	public void setSearchStaff(Staff searchStaff) {
		this.searchStaff = searchStaff;
	}
	public void to_search_staff() {
		try {
			String dynamicWhereClause = "";
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
			
			if (searchStaff!=null && searchStaff.getStaff_id()!=null && searchStaff.getStaff_id()>0)
			{	 
				dynamicWhereClause = dynamicWhereClause + " and stf.staff_id = " + searchStaff.getStaff_id();			
				dynamicWhereClause = dynamicWhereClause + " and stf.position_id <> 1";
				p_staff_list = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			}
			else if (searchStaff!=null && searchStaff.getIin_bin()!=null && searchStaff.getIin_bin().length()>0)
			{  
				dynamicWhereClause = dynamicWhereClause + " and stf.iin_bin = '" + searchStaff.getIin_bin()+"'";
				dynamicWhereClause = dynamicWhereClause + " and stf.position_id <> 1";
				p_staff_list = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			}
			else if  (searchStaff.getFirstname()!=null || searchStaff.getLastname()!=null || searchStaff.getMiddlename()!=null || p_searchTable.getBranch_id()!=null)
			{ 
				String firstName = "";
				String middleName = "";
				String lastName = "";
				firstName = searchStaff.getFirstname().replaceAll("\\s", "");
				middleName = searchStaff.getMiddlename().replaceAll("\\s", "");
				lastName = searchStaff.getLastname().replaceAll("\\s", "");
				if (firstName.length() > 0) {
					dynamicWhereClause = dynamicWhereClause + " ";
					dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(dynamicWhereClause, "and", "=", "stf.firstname", firstName);
				}
				if (middleName.length() > 0) {
					dynamicWhereClause = dynamicWhereClause + " ";
					dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(dynamicWhereClause, "and", "=", "stf.middlename", middleName);
				}
				if (lastName.length() > 0) {
					dynamicWhereClause = dynamicWhereClause + " ";
					dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(dynamicWhereClause, "and", "=", "stf.lastname", lastName);
				}
				
				if (p_searchTable.getBranch_id()!=null && p_searchTable.getBranch_id()>0)
				{
					dynamicWhereClause = dynamicWhereClause + "sal.branch_id ="+ p_searchTable.getBranch_id();
				}
				if (dynamicWhereClause.length()==0)
				{
					throw new DAOException("Fill search criteria");
				}
				dynamicWhereClause = dynamicWhereClause + " and stf.position_id <> 1"; 
				p_staff_list = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			}
			
			if (p_staff_list==null || p_staff_list.size()==0)
			{
				throw new DAOException("No employee found");
			}
				
			

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");

		} catch (DAOException ex) {
			p_staff_list = new ArrayList<Staff>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");
			addMessage("Info", ex.getMessage());
		}
	}
	public void assignFoundEmployee() {
		String fio = "";
//		if (selectedStaff != null && selectedStaff.getStaff_id() != null) {
//			if (selectedStaff.getLastname() != null && selectedStaff.getLastname().length()>0 )
//				fio = fio + selectedStaff.getLastname();
//			if (selectedStaff.getFirstname() != null && selectedStaff.getFirstname().length()>0 )
//				fio = fio + " " + selectedStaff.getFirstname();
//			if (selectedStaff.getMiddlename() != null && selectedStaff.getMiddlename().length()>0 )
//				fio = fio + " " + selectedStaff.getMiddlename(); 
//			
//			p_searchTable.setStaff_id(selectedStaff.getStaff_id());
//			p_searchTable.setStaff_name(fio);
//			
//		} else {
//
//			p_searchTable.setStaff_id(null);
//			p_searchTable.setStaff_name("");
//			
//		}

		selectedStaff = new Staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:p_staff");
		reqCtx.update("form:p_staff1"); 
	}
	// *********************************************************************	


	public void updateBranch()
	{
		l_fired.clear();
		p_searchTable.setBranch_name(l_branchMap.get(p_searchTable.getBranch_id()).getText45());
		
		StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
		List<Object[]> results = staffDao.getFiredList(p_searchTable.getBranch_id());
		for(Object[] result:results)
		{
			FiredList fl = new FiredList();
			if (result[0]!=null) fl.setStaff_id_fired(Long.parseLong(String.valueOf(result[0])));
			if (result[1]!=null) fl.setStaff_name_fired(String.valueOf(result[1]));
			l_fired.add(fl);
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:p_branch1");
		reqCtx.update("form:tabView:p_staff_id_fired");
		
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
		private java.util.Date begDate = new  java.util.Date();
		private java.util.Date endDate = new  java.util.Date();
		private int detailType;
		private String waers;
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
	
	
	
	
	private List<Payroll> detailedInfo = new ArrayList<Payroll>();
	
	private double total_detail = 0;
	
	public double getTotal_detail() {
		return total_detail;
	}

	public void setTotal_detail(double total_detail) {
		this.total_detail = total_detail;
	}

	public void getDatiledInfo()
	{
		try
		{
			if (selectedOutputTable!=null)
			{
				total_detail = 0;
				detailedInfo = new ArrayList<Payroll>();
				List<Long> l_staff_id = new ArrayList<Long>();
				l_staff_id.add(selectedOutputTable.getStaff_id());
				Calendar firstDay = Calendar.getInstance(); 
				Calendar lastDay = Calendar.getInstance();			  
				firstDay.setTime(p_detailSearchTable.getBegDate());
				lastDay.setTime(p_detailSearchTable.getEndDate());
				PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
				detailedInfo = payrollDao.findByBukrsBranchAllPeriod(l_staff_id,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),
						p_searchTable.getBukrs(),p_detailSearchTable.getDetailType(),p_detailSearchTable.getWaers());
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
				reqCtx.update("form:detInfoTable");
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
	

	public void sortByOrderNo() {

		   Collections.sort(l_outputTable, new Comparator<OutputTable>() {

			@Override
			public int compare(OutputTable o1, OutputTable o2) {

				return o1.getStaff_name().compareTo(o2.getStaff_name());

			}
		   });
		}
	
	public void refreshAfterSort() throws DAOException
	{
		try
		{
			System.out.println("Text zzzzzzzzzzzzzz");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView:l_outputTable");
			//FinanceServicePayroll financeServicePayroll = (FinanceServicePayroll) appContext.getContext().getBean("financeServicePayroll");
			//financeServicePayroll.old_wages();
				
		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
		}
	}
}
