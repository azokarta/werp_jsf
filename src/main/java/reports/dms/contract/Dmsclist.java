package reports.dms.contract;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.services.CustomerService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Customer;
import general.tables.Staff;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import user.User;
import datamodels.ContractModel;

@ManagedBean(name = "dmsclistBean", eager = true)
@ViewScoped
public class Dmsclist {
	private final static String transaction_code = "DMSCLIST";
	private final static Long transaction_id = 54L;

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// *******************************************************************
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
	// ***************************ContractTypeF4*******************************
	@ManagedProperty(value = "#{contractTypeF4Bean}")
	private ContractTypeF4 p_contractTypeF4Bean;

	public ContractTypeF4 getP_contractTypeF4Bean() {
		return p_contractTypeF4Bean;
	}

	public void setP_contractTypeF4Bean(ContractTypeF4 p_contractTypeF4Bean) {
		this.p_contractTypeF4Bean = p_contractTypeF4Bean;
	}

	List<ContractType> contractType_list = new ArrayList<ContractType>();

	public List<ContractType> getContractType_list() {
		return contractType_list;
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

	// **************************Employee*******************************
	private List<Staff> p_staff_list = new ArrayList<Staff>();

	public List<Staff> getP_staff_list() {
		return p_staff_list;
	}

	public void setP_staff_list(List<Staff> p_staff_list) {
		this.p_staff_list = p_staff_list;
	}

	// *********************************************************************
	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest())
			return;

		try {
			PermissionController.canRead(userData, this.transaction_id);
			initContractModel();

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
				
				loadStaff();
				// outputTable.searchModel.setP_start_date(Calendar.getInstance().getTime());
			}
			
			outputTable.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		}
	}

	// *********************************************************************************
	public int getOutputLength() {
		return this.outputTable.getRowCount();
	}

	public void search() {
		try {
//			if (userData.isMain() && outputTable.searchModel.getBranch_id() < 0)
//				outputTable.searchModel.setBranch_id(0L);
			loadContractModel();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:outputTable");
	}

	private void loadContractModel() {
		outputTable.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
		String cond = "";
		if (outputTable.getSearchModel().getContract_number() != null
				&& outputTable.getSearchModel().getContract_number() > 0) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView:bukrs");
			reqCtx.update("form:tabView:branch");
			reqCtx.update("form:tabView:dealer");
			reqCtx.update("form:tabView:collector");
			reqCtx.update("form:tabView:start_date");
			reqCtx.update("form:tabView:end_date");
		} else {
			if (outputTable.getSearchModel().getDealer() != null
					&& outputTable.getSearchModel().getDealer().intValue() == -1
					&& l_dealer.size() > 0) {
				cond += " and dealer not in (";
				int count = 0;
				for (Staff wa_staff : l_dealer) {
					count = count + 1;
					if (count == 1) {
						cond += wa_staff.getStaff_id();
					} else {
						cond += ", " + wa_staff.getStaff_id();
					}
				}
				cond += ")";
			}

			if (outputTable.getSearchModel().getCollector() != null
					&& outputTable.getSearchModel().getCollector().intValue() == -1
					&& l_collector.size() > 0) {
				cond += " and collector not in (";
				int count = 0;
				for (Staff wa_staff : l_collector) {
					count = count + 1;
					if (count == 1) {
						cond += wa_staff.getStaff_id();
					} else {
						cond += "," + wa_staff.getStaff_id();
					}
				}
				cond += ")";
			}
		}

		// initContractModel();
		outputTable.setCondition(cond);
	}

	public void initContractModel() {
		outputTable = new ContractModel((ContractDao) appContext.getContext()
				.getBean("contractDao"));
	}

	// *********************************************************************
	private ContractModel outputTable;

	public ContractModel getOutputTable() {
		return outputTable;
	}

	public void setOutputTable(ContractModel outputTable) {
		this.outputTable = outputTable;
	}

	// ***************************ContractStatusF4*******************************
	@ManagedProperty(value = "#{contractStatusF4Bean}")
	private ContractStatusF4 p_contractStatusF4Bean;

	public ContractStatusF4 getP_contractStatusF4Bean() {
		return p_contractStatusF4Bean;
	}

	public void setP_contractStatusF4Bean(
			ContractStatusF4 p_contractStatusF4Bean) {
		this.p_contractStatusF4Bean = p_contractStatusF4Bean;
	}

	// ***************** LOAD BRANCH LIST *******************************
	public List<Branch> loadBranchList(String a_bukr) {
		List<Branch> wa_brlist = new ArrayList<Branch>();
		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (a_bukr.equals(wa_branch.getBukrs())
					&& wa_branch.getType() == 3
					&& (wa_branch.getBusiness_area_id() == null || (wa_branch
							.getBusiness_area_id() != null && wa_branch
							.getBusiness_area_id() != 5))) {
				wa_brlist.add(wa_branch);
			}
		}
		return wa_brlist;
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

		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		if (userData.isSys_admin()) branch_list = brDao.findUserBranchesDmsc01Admin(outputTable.getSearchModel().getBukrs(), userData.getUserid());
		else branch_list = brDao.findUserBranchesDmsc01(outputTable.getSearchModel().getBukrs(), userData.getUserid());
		
		
//		branch_list =  brDao.findUserBranchesDmsc01(outputTable.getSearchModel().getBukrs(), userData.getUserid());
//		loadBranchListByUserBranch(outputTable.getSearchModel().getBukrs(), branchId, BusinessArea.AREA_ALL_EXCEPT_SERVICE);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		outputTable.searchModel.setContract_number(null);
		reqCtx.update("form:tabView:contr_num");
	}

	public List<Branch> loadBranchListByUserBranch(String a_bukr, Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));

		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		Branch inBranch = brDao.find(branchId);
		
		List<Branch> brL  = new ArrayList<Branch>();
		if (inBranch.getType() != Branch.TYPE_BRANCH  || inBranch.getBusiness_area_id() == 0) {
			bf.addFilter(new BranchParentFilter(inBranch.getBranch_id()));
			brL = bf.filterBranch(p_branchF4Bean.getBranch_list());
		} else bf.addFilter(new BranchParentFilter(inBranch.getParent_branch_id()));
		
		bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
	
		output = bf.filterBranch(p_branchF4Bean.getBranch_list());
		
		for (Branch br:brL) {
			if (br.getType() != Branch.TYPE_BRANCH || br.getBusiness_area_id() == 0)
				output.addAll(loadBranchListByUserBranch(a_bukr, br.getBranch_id(), ba));			
		}		
		return output;
	}
	// **************************Employee*******************************************
	private List<Staff> l_dealer = new ArrayList<Staff>();

	public List<Staff> getL_dealer() {
		return l_dealer;
	}

	public void setL_dealer(List<Staff> l_dealer) {
		this.l_dealer = l_dealer;
	}

	public void loadStaff() {
		try {
			l_dealer = new ArrayList<Staff>();
			l_collector = new ArrayList<Staff>();
			l_demosec = new ArrayList<Staff>();
			Map<Long,Staff> l_staff_map = new HashMap<Long,Staff>();
			String dynamicWhereClause = "";
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean(
					"staffDao");
			dynamicWhereClause = dynamicWhereClause
					+ " and (sal.position_id not in (8)) and sal.branch_id = "
					+ outputTable.getSearchModel().getBranch_id()
					+ " and sal.bukrs = "
					+ outputTable.getSearchModel().getBukrs();
			l_dealer = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			l_staff_map.clear();
			if (l_dealer.size() > 0) {
				for (Staff wa_staff : l_dealer) {
					wa_staff.setFirstname(wa_staff.getLF());
					l_staff_map.put(wa_staff.getStaff_id(), wa_staff);					
				}
				l_dealer.clear();
				for (Map.Entry<Long, Staff> entry : l_staff_map.entrySet())
				{
					l_dealer.add(entry.getValue());
				    //System.out.println(entry.getKey() + "/" + entry.getValue());
				}
			}
			
			
			
			

			dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause
					+ " and sal.position_id in (9, 62) and sal.branch_id = "
					+ outputTable.getSearchModel().getBranch_id()
					+ " and sal.bukrs = "
					+ outputTable.getSearchModel().getBukrs();
			l_collector = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			l_staff_map.clear();
			if (l_collector.size() > 0) {
				for (Staff wa_staff : l_collector) {
					wa_staff.setFirstname(wa_staff.getLF());
					l_staff_map.put(wa_staff.getStaff_id(), wa_staff);
				}
				l_collector.clear();
				for (Map.Entry<Long, Staff> entry : l_staff_map.entrySet())
				{
					l_collector.add(entry.getValue());
				    //System.out.println(entry.getKey() + "/" + entry.getValue());
				}
			}
			
			dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause
					+ " and sal.position_id = 8 and sal.branch_id = "
					+ outputTable.getSearchModel().getBranch_id()
					+ " and sal.bukrs = "
					+ outputTable.getSearchModel().getBukrs();
			l_demosec = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			l_staff_map.clear();
			if (l_demosec.size() > 0) {
				for (Staff wa_staff : l_demosec) {
					wa_staff.setFirstname(wa_staff.getLF());
					l_staff_map.put(wa_staff.getStaff_id(), wa_staff);
				}
				l_demosec.clear();
				for (Map.Entry<Long, Staff> entry : l_staff_map.entrySet())
				{
					l_demosec.add(entry.getValue());
				    //System.out.println(entry.getKey() + "/" + entry.getValue());
				}
			}
			
			Comparator<Staff> com = new Comparator<Staff>() {
				@Override
				public int compare(Staff o1, Staff o2) {
					return o1.getLF().compareTo(o2.getLF());
				}
			}; 
			
			l_dealer.sort(com);
			l_demosec.sort(com);
			l_collector.sort(com);
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView:dealer");
			reqCtx.update("form:tabView:collector");
			reqCtx.update("form:tabView:demoSec");
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
	}

	private List<Staff> l_demosec = new ArrayList<Staff>();
	public List<Staff> getL_demosec() {
		return l_demosec;
	}
	public void setL_demosec(List<Staff> l_demosec) {
		this.l_demosec = l_demosec;
	}

	private List<Staff> l_collector = new ArrayList<Staff>();
	public List<Staff> getL_collector() {
		return l_collector;
	}
	public void setL_collector(List<Staff> l_collector) {
		this.l_collector = l_collector;
	}

	// *************************** Customer ********************************
	private List<Customer> p_customer_list = new ArrayList<Customer>();

	public List<Customer> getP_customer_list() {
		return p_customer_list;
	}

	public void setP_customer_list(List<Customer> p_customer_list) {
		this.p_customer_list = p_customer_list;
	}

	private Customer selectedCustomer = new Customer();

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	private Customer searchCustomer = new Customer();

	public Customer getSearchCustomer() {
		return searchCustomer;
	}

	public void setSearchCustomer(Customer searchCustomer) {
		this.searchCustomer = searchCustomer;
	}

	// ********************************************************************************
	// ******************************* CUSTOMER *****************************

	public void to_search_customer() {
		try {
			CustomerService personService = (CustomerService) appContext
					.getContext().getBean("customerService");
			p_customer_list = personService.dynamicSearch(searchCustomer);

			if (p_customer_list.size() == 0) {
				p_customer_list = new ArrayList<Customer>();
				GeneralUtil.addMessage("Инфо", "Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CustomerSearchForm:customerTable");

		} catch (DAOException ex) {
			p_customer_list = new ArrayList<Customer>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CustomerSearchForm:customerTable");
			GeneralUtil.addMessage("Error", ex.getMessage());
		}
	}

	public void assignFoundCustomer() {
		if (selectedCustomer != null && selectedCustomer.getId() != null) {
			outputTable.searchModel.setCustomer_id(selectedCustomer.getId());
			outputTable.searchModel.customer_fio = selectedCustomer
					.getFullFIO();
		} else {
			outputTable.searchModel.setCustomer_id(null);
			outputTable.searchModel.customer_fio = " ";
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:fio");
		GeneralUtil.hideDialog("CustomerWidget");
		// selectedCustomer = new Customer();
	}

	public void clearCustomerField() {
		outputTable.searchModel.setCustomer_id(null);
		outputTable.searchModel.customer_fio = "";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:fio");
	}

	// ***********************************************************************


}
