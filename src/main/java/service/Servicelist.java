package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import datamodels.ServiceModel;
import f4.BranchF4;
import f4.BukrsF4;
import f4.CountryF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.ServiceDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.dao.UserRoleDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Country;
import general.tables.ServiceTable;
import general.tables.Staff;
import general.tables.UserRole;
import user.User;

@ManagedBean(name = "servicelistBean", eager = true)
@ViewScoped
public class Servicelist {
	/**
	 * 
	 */
	private static final long serialVersionUID = 94L;
	private final static String transaction_code = "SERVICELIST";
	private final static Long transaction_id = (long) 94;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

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
	// *********************************************************************
	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest())
			return;

		try {
			PermissionController.canRead(userData, this.transaction_id);
			initServiceModel();

			loadUsers();
			
			if (userData.isMain()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
			} else {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					if (wa_bukrs.getBukrs().equals(userData.getBukrs())) {
						bukrs_list.add(wa_bukrs);
						break;
					}
				}
				outputTable.searchModel.setBranch_id((long) userData
						.getBranch_id());
				outputTable.searchModel.setBukrs(userData.getBukrs());
				loadBranch2();
			}
			
			checkAccessLevel();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		}
	}
	
	private void loadUsers() {
		UserDao uDao = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> ul = new ArrayList<general.tables.User>();
		ul = uDao.findAll("1 = 1");		
		user_list.clear();		
		
		for (general.tables.User u:ul) {
			if (isServiceUser(u.getUser_id())) {
				user_list.add(u);
			}				
		}
		user_list.sort(new Comparator<general.tables.User>() {
			@Override
			public int compare(general.tables.User o1, general.tables.User o2) {
				return o1.getUsername().compareToIgnoreCase(o2.getUsername());
			}
		});
	}
	
	private boolean isServiceUser(Long inUserId) {
		List<UserRole> urL = new ArrayList<UserRole>();
		UserRoleDao urlDao = (UserRoleDao) appContext.getContext().getBean(
				"userRoleDao");
		urL = urlDao.findUserRoles(inUserId);
		boolean isSU = false;
		for (UserRole wa_ur : urL) {
			if (wa_ur.getRoleId() == 24 || wa_ur.getRoleId() == 30 || wa_ur.getRoleId() == 29 || wa_ur.getRoleId().equals(52L) || wa_ur.getRoleId().equals(313L)) {
				isSU = true;
			}
		}
		return isSU;
	}
	
	private void checkAccessLevel() {
		disVaultTab = true;
		if (isServiceUser(userData.getUserid()) || userData.isSys_admin())
			disVaultTab = false;
	}

	public void initServiceModel() {
		outputTable = new ServiceModel((ServiceDao) appContext.getContext()
				.getBean("serviceDao"));
	}

	private void loadServiceModel() {
		String cond = " ";
		loadStaff();
		if (outputTable.getSearchModel().getServ_num() != null
				&& outputTable.getSearchModel().getServ_num() > 0) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bukrs");
			reqCtx.update("form:branch");
			reqCtx.update("form:tabView:master");
			reqCtx.update("form:tabView:operator");
			reqCtx.update("form:tabView:start_date");
			reqCtx.update("form:tabView:end_date");
		} else {
			outputTable.setCondition(cond);
		}
	}

	ServiceModel outputTable;

	public ServiceModel getOutputTable() {
		return outputTable;
	}

	public void setOutputTable(ServiceModel outputTable) {
		this.outputTable = outputTable;
	}
	
	// *********************************************************************************
	public int getOutputLength() {
		return this.outputTable.getRowCount();
	}

	public void search() {
		try {
			loadServiceModel();
			getTotalSumm();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:outputTable");
		reqCtx.update("form");
	}
	
	public void getTotalSumm() {
		List<ServiceTable> sL  = getServDao().dynamicFindAll(outputTable.getSearchModel().getCondition());
		//getServDao().getSumm(outputTable.getSearchModel().getCondition(), summTotal, paidTotal, premMaster, premOper);   
		
		summTotal = 0D;
		paidTotal = 0D;
		premMaster = 0D;
		premOper = 0D;
		currency = "";
		for (ServiceTable s:sL) {
			if (s.getServ_status() != ServiceTable.STATUS_CANCELLED) {
				summTotal += s.getPayment_due();
				paidTotal += s.getPaid();
				premMaster += s.getMaster_premi();
				premOper += s.getOper_premi();
			}
		}
		
		if (!Validation.isEmptyLong(outputTable.getSearchModel().getBranch_id())) {
			Branch br = p_branchF4Bean.getL_branch_map().get(outputTable.getSearchModel().getBranch_id());
			Country c = p_countryF4Bean.getL_country_map().get(br.getCountry_id());
			currency = c.getCurrency();				
		} else if (sL.size() > 0) {
			Branch br = p_branchF4Bean.getL_branch_map().get(sL.get(0).getBranch_id());
			Country c = p_countryF4Bean.getL_country_map().get(br.getCountry_id());
			currency = c.getCurrency();
		}
		
		remainTotal = summTotal - paidTotal;
		incomeTotal = summTotal - premMaster - premOper;
		
		System.out.println("SummTotal: " + summTotal);
		System.out.println("PaidTotal: " + paidTotal);
		System.out.println("Remain: " + remainTotal);
		System.out.println("PremiMaster: " + premMaster);
		System.out.println("PremiOper: " + premOper);
		System.out.println("Income: " + incomeTotal);
	}
	
	public ServiceDao getServDao() {
		return (ServiceDao) appContext.getContext().getBean("serviceDao"); 
	}

	public Double summTotal;
	public Double paidTotal;
	public Double premMaster;
	public Double premOper;
	public String currency;
	public Double remainTotal;
	public Double incomeTotal;
	
	public Double getRemainTotal() {
		return remainTotal;
	}
	public void setRemainTotal(Double remainTotal) {
		this.remainTotal = remainTotal;
	}
	public Double getIncomeTotal() {
		return incomeTotal;
	}

	public void setIncomeTotal(Double incomeTotal) {
		this.incomeTotal = incomeTotal;
	}

	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getPremMaster() {
		return premMaster;
	}
	public void setPremMaster(Double premMaster) {
		this.premMaster = premMaster;
	}
	public Double getPremOper() {
		return premOper;
	}
	public void setPremOper(Double premOper) {
		this.premOper = premOper;
	}
	public Double getSummTotal() {
		return summTotal;
	}
	public void setSummTotal(Double summTotal) {
		this.summTotal = summTotal;
	}
	public Double getPaidTotal() {
		return paidTotal;
	}
	public void setPaidTotal(Double paidTotal) {
		this.paidTotal = paidTotal;
	}
	// ***************** LOAD BRANCH LIST *******************************
	public List<Branch> loadBranchListByUserBranch(String a_bukr, Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));

		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		Branch inBranch = brDao.find(branchId);
		
		List<Branch> brL  = new ArrayList<Branch>();
		if ((inBranch.getType() != Branch.TYPE_BRANCH)
				|| (inBranch.getBusiness_area_id() == 0)) {
			bf.addFilter(new BranchParentFilter(inBranch.getBranch_id()));
			brL = bf.filterBranch(p_branchF4Bean.getBranch_list());
		} else bf.addFilter(new BranchParentFilter(inBranch.getParent_branch_id()));

		bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
	
		output = bf.filterBranch(p_branchF4Bean.getBranch_list());
		
		for (Branch br:brL) {
			if ((br.getType() != Branch.TYPE_BRANCH) || (br.getBusiness_area_id() == 0))
				output.addAll(loadBranchListByUserBranch(a_bukr, br.getBranch_id(), ba));			
		}		
		return output;
	}

	public void loadBranch2() {
		branch_list = new ArrayList<Branch>();
		
		Long branchId = userData.getBranch_id();
		if (userData.isMain() || userData.isSys_admin()) {
			if (outputTable.getSearchModel().getBukrs().equals("1000"))
				branchId = Branch.AURA_MAIN_BRANCH_ID;
			else if (outputTable.getSearchModel().getBukrs().equals("2000"))
				branchId = Branch.GREEN_LIGHT_MAIN_BRANCH_ID;
		}
		
		branch_list = loadBranchListByUserBranch(outputTable.getSearchModel().getBukrs(), branchId, BusinessArea.AREA_SERVICE);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		outputTable.searchModel.setContract_id(null);
		reqCtx.update("form:tabView:contr_num");
	}

	// ************************** BranchF4 ******************************
	// ******************************************************************
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
	// **************************Employee*******************************************
		private List<Staff> l_master = new ArrayList<Staff>();
		private List<Staff> l_oper = new ArrayList<Staff>();
		
		public List<Staff> getL_master() {
			return l_master;
		}

		public void setL_master(List<Staff> l_master) {
			this.l_master = l_master;
		}

		public List<Staff> getL_oper() {
			return l_oper;
		}

		public void setL_oper(List<Staff> l_oper) {
			this.l_oper = l_oper;
		}

		public void loadStaff() {
			try {
				l_master = new ArrayList<Staff>();
				l_oper = new ArrayList<Staff>();

				String dynamicWhereClause = "";
				StaffDao staffDao = (StaffDao) appContext.getContext().getBean(
						"staffDao");
				dynamicWhereClause = dynamicWhereClause
						+ " and sal.position_id in (13, 14, 16, 17) and sal.branch_id in ("
						+ outputTable.getSearchModel().getSelBranchesString() + ") and sal.bukrs = "
						+ outputTable.getSearchModel().getBukrs();
				l_master = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
				if (l_master.size() > 0) {
					for (Staff wa_staff : l_master) {
						wa_staff.setFirstname(wa_staff.getLF());
					}
				}
				System.out.println("MASTERS found: " + l_master.size());

				dynamicWhereClause = "";
				dynamicWhereClause = dynamicWhereClause
						+ " and sal.position_id in (18, 19) and sal.branch_id in ("
						+ outputTable.getSearchModel().getSelBranchesString() + ") and sal.bukrs = "
						+ outputTable.getSearchModel().getBukrs();
				l_oper = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
				if (l_oper.size() > 0) {
					for (Staff wa_staff : l_oper) {
						wa_staff.setFirstname(wa_staff.getLF());
					}
				}
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:tabView:master");
				reqCtx.update("form:tabView:operator");
			} catch (DAOException ex) {
				GeneralUtil.addMessage("Info", ex.getMessage());
			}
		}

	
		
		public void toNewServicePage() {
			GeneralUtil.doRedirect("/service/maintenance/service01.xhtml");
		}		
		
		// ***************************Country*******************************
		@ManagedProperty(value = "#{countryF4Bean}")
		private CountryF4 p_countryF4Bean;

		public CountryF4 getP_countryF4Bean() {
			return p_countryF4Bean;
		}

		public void setP_countryF4Bean(CountryF4 p_countryF4Bean) {
			this.p_countryF4Bean = p_countryF4Bean;
		}

		List<Country> country_list = new ArrayList<Country>();

		public List<Country> getCountry_list() {
			return country_list;
		}

		// ******************************************************************
		
		public boolean disVaultTab;

		public boolean isDisVaultTab() {
			return disVaultTab;
		}

		public void setDisVaultTab(boolean disVaultTab) {
			this.disVaultTab = disVaultTab;
		}

		// ***************************User session***************************
		
		public List<general.tables.User> user_list = new ArrayList<general.tables.User>();

		public List<general.tables.User> getUser_list() {
			return user_list;
		}

}
