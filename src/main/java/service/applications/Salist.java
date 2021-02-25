package service.applications;

import f4.BranchF4;
import f4.BukrsF4;
import f4.CountryF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.clone.Clone;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ServiceApplicationDao;
import general.dao.UserDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.services.CustomerService;
import general.services.ServAppService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Contract;
import general.tables.Country;
import general.tables.Customer;
import general.tables.ServiceApplication;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import datamodels.ContractModel;
import datamodels.ServappModel;
import dms.contract.ContractDetails;
import user.User;

@ManagedBean(name = "salistBean", eager = true)
@ViewScoped
public class Salist implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 93L;
	private final static String transaction_code = "SALIST";
	private final static Long transaction_id = 93L;

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

	List<Branch> region_list = new ArrayList<Branch>();

	public List<Branch> getRegion_list() {
		return region_list;
	}

	List<Branch> cbranch_list = new ArrayList<Branch>();

	public List<Branch> getCbranch_list() {
		return cbranch_list;
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
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public List<general.tables.User> user_list = new ArrayList<general.tables.User>();

	public List<general.tables.User> getUser_list() {
		return user_list;
	}

	// ******************************************************************
	public List<ServAppOutputTable> servAppTable;

	public List<ServAppOutputTable> getServAppTable() {
		return servAppTable;
	}

	public void setServAppTable(List<ServAppOutputTable> servAppTable) {
		this.servAppTable = servAppTable;
	}

	public ServAppOutputTable selectedSAOT;
	public ServAppOutputTable selectedSAOTEdit;

	public ServAppOutputTable getSelectedSAOTEdit() {
		return selectedSAOTEdit;
	}

	public void setSelectedSAOTEdit(ServAppOutputTable selectedSAOTEdit) {
		this.selectedSAOTEdit = selectedSAOTEdit;
	}

	public ServAppOutputTable getSelectedSAOT() {
		return selectedSAOT;
	}

	public void setSelectedSAOT(ServAppOutputTable selectedSAOT) {
		this.selectedSAOT = selectedSAOT;
	}

	public ServiceApplication selectedSA;

	public ServiceApplication getSelectedSA() {
		return selectedSA;
	}

	public void setSelectedSA(ServiceApplication selectedSA) {
		this.selectedSA = selectedSA;
	}

	// *********************************************************************
	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}
		try {
			PermissionController.canWriteRedirect(userData, transaction_id);
			initSAModel();
			initContractModel();

			UserDao uDao = (UserDao) appContext.getContext().getBean("userDao");
			this.user_list = uDao.findAll("1 = 1");

			if (userData.isMain()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
				servappModel.searchModel.setBukrs(bukrs_list.get(0).getBukrs());
				contractModel.searchModel
						.setBukrs(bukrs_list.get(0).getBukrs());
				userDis = false;
			} else {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					if (wa_bukrs.getBukrs().equals(userData.getBukrs())) {
						bukrs_list.add(wa_bukrs);
					}
				}
				servappModel.searchModel.setBukrs(userData.getBukrs());
				servappModel.searchModel.setBranch_id(userData.getBranch_id());
				servappModel.searchModel.setCreated_by(userData.getUserid());
				userDis = true;
				contractModel.searchModel.setBukrs(userData.getBukrs());
				contractModel.searchModel.setBranch_id(userData.getBranch_id());

				// dis_branch_inp = true;
			}

			updateByNewBukrs();
			// loadServAppOutputTable();

		} catch (DAOException ex) {
			// GeneralUtil.doRedirect("/general/mainPage.xhtml");
			throw new DAOException("Error! " + ex.getMessage());
		}
	}

	public boolean userDis;

	public boolean isUserDis() {
		return userDis;
	}

	public void setUserDis(boolean userDis) {
		this.userDis = userDis;
	}

	// *****************************************************************************************************
	public void updateByNewBukrs() {
		loadBranchesByBukrs(servappModel.searchModel.getBukrs());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ServAppCreateForm");
		reqCtx.update("ServAppEditForm");
		reqCtx.update("ContractListForm");
	}

	public void loadBranchesByBukrs(String a_bukrs) {
		branch_list = new ArrayList<Branch>();
		Long branchId = userData.getBranch_id();
		if (userData.isMain() || userData.isSys_admin()) {
			if (a_bukrs.equals("1000"))
				branchId = Branch.AURA_MAIN_BRANCH_ID;
			else if (a_bukrs.equals("2000"))
				branchId = Branch.GREEN_LIGHT_MAIN_BRANCH_ID;
		}

		//System.out.println("User BranchID: " + branchId);
		branch_list = loadBranchListByUserBranch(a_bukrs,
				branchId, BusinessArea.AREA_SERVICE);
	}

	public List<Branch> loadBranchListByUserBranch(String a_bukr, Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		Branch inBranch = brDao.find(branchId);
		
		List<Branch> brL  = new ArrayList<Branch>();
		if (inBranch.getType() != Branch.TYPE_BRANCH) {
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
	
	public void loadContrBranch() {
		cbranch_list = new ArrayList<Branch>();
		if (contractModel.searchModel.getBukrs() != null) {
			for (Branch b : p_branchF4Bean.getBranch_list()) {
				if ((b.getType() == 3)
						&& (b.getBukrs().equals(contractModel.searchModel
								.getBukrs()))) {
					cbranch_list.add(b);
				}
			}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:cbranch");
	}

	// *****************************************************************************
	ServiceApplication newSA = new ServiceApplication();

	public ServiceApplication getNewSA() {
		return newSA;
	}

	public void setNewSA(ServiceApplication newSA) {
		this.newSA = newSA;
	}

	public void prepareCreate() {
		newSA = new ServiceApplication();
		edit = false;

		if (!Validation.isEmptyString(servappModel.searchModel.getBukrs())
				&& servappModel.searchModel.getBukrs().length() > 1) {
			newSA.setBukrs(servappModel.searchModel.getBukrs());
			contractModel.searchModel.setBukrs(servappModel.searchModel
					.getBukrs());
			contractModel.searchModel.setBranch_id(servappModel.searchModel
					.getBranch_id());
			selectedSA = new ServiceApplication();
			selectedContr = new ContractDetails();
		} else {
			newSA.setBukrs(bukrs_list.get(0).getBukrs());
			// selectedSA.setBukrs(bukrs_list.get(0).getBukrs());
			contractModel.searchModel.setBukrs(bukrs_list.get(0).getBukrs());
		}

		loadContrBranch();
		// selectedSA.setApp_status(1L);
		// selectedSA.setCreated_by(userData.getUserid());
		contractModel.searchModel.setBranch_id(userData.getBranch_id());
		newSA.setApp_status(1L);
		newSA.setCreated_by(userData.getUserid());
		newSA.setAdate(Calendar.getInstance().getTime());
		updateByNewBukrs();
	}

	private ServAppOutputTable cloneSAOT(ServAppOutputTable a_saot) {
		ServAppOutputTable saot = new ServAppOutputTable();

		saot.setBranch(a_saot.getBranch());
		saot.setCompany(a_saot.getCompany());
		saot.setContract(a_saot.getContract());
		saot.setCustomer(a_saot.getCustomer());
		saot.setIndex(a_saot.getIndex());
		saot.setContr_num(a_saot.getContr_num());
		saot.setUser_fio(a_saot.getUser_fio());

		saot.setServApp(Clone.cloneServiceApplication(a_saot.getServApp()));

		return saot;
	}

	// *****************************************************************************

	public void prepareUpdate() {
		if (selectedSA != null) {
			selectedSAOTEdit = new ServAppOutputTable();

			edit = true;
			/*
			 * selectedSAOTEdit.setIndex(selectedSA.getIndex());
			 * selectedSAOTEdit.setBranch(selectedSA.getBranch());
			 * selectedSAOTEdit.setCompany(selectedSA.getCompany());
			 * selectedSAOTEdit.setServApp(selectedSA.getServApp());
			 * selectedSAOTEdit.setUser_fio(selectedSA.getUser_fio());
			 */

			ServiceApplication p_SA = Clone.cloneServiceApplication(selectedSA);
			selectedSAOTEdit.setServApp(p_SA);

			ContractDao conDao = (ContractDao) appContext.getContext().getBean(
					"contractDao");
			Contract con = conDao.findByContractNumber(selectedSAOTEdit
					.getServApp().getContract_number());
			selectedSAOTEdit.setContract(con);

			if (con != null && con.getCustomer_id() != null
					&& con.getCustomer_id() > 0) {
				CustomerDao cusDao = (CustomerDao) appContext.getContext()
						.getBean("customerDao");
				Customer cus = cusDao.find(con.getCustomer_id());
				if (cus != null && cus.getId() != null && cus.getId() > 0) {
					selectedSAOTEdit.setCustomer(cus);
					selectedSAOTEdit.setCustomer_fio(cus.getFIO());
				}
			}

		}
	}

	// ******************************************************************************************************

	private ContractModel contractModel;

	public ContractModel getContractModel() {
		return contractModel;
	}

	public void setContractModel(ContractModel contractModel) {
		this.contractModel = contractModel;
	}

	public int getOutputLength() {
		return this.contractModel.getRowCount();
	}

	public void searchContract() {
		try {
			loadContractModel();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:outputTable");
	}

	private void loadContractModel() {
		contractModel.searchModel.setMarkedType(Contract.MT_ALL);
		contractModel.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
		
		String cond = " ";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (contractModel.getSearchModel().getContract_number() != null
				&& contractModel.getSearchModel().getContract_number() > 0) {
			reqCtx.update("ContractListForm:bukrs");
			reqCtx.update("ContractListForm:branch");
			reqCtx.update("ContractListForm:dealer");
			reqCtx.update("ContractListForm:fio");
			reqCtx.update("ContractListForm:tovarSN");
		} else {
			contractModel.searchModel.setContract_number(null);
			reqCtx.update("ContractListForm:contr_num");
		}

		// initContractModel();
		contractModel.setCondition(cond);
	}

	public void initContractModel() {
		contractModel = new ContractModel((ContractDao) appContext.getContext()
				.getBean("contractDao"));
		contractModel.searchModel.setMarkedType(Contract.MT_ALL);
		contractModel.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
	}

	public ContractDetails selectedContr;

	public ContractDetails getSelectedContr() {
		return selectedContr;
	}

	public void setSelectedContr(ContractDetails selectedContr) {
		this.selectedContr = selectedContr;
	}

	public void assignSelectedContrId() {

		if (selectedContr != null && selectedContr.getContract() != null
				&& selectedContr.getContract().getContract_id() != null
				&& selectedContr.getContract().getContract_id() > 0) {
			System.out.println("CT: "
					+ selectedContr.getContrType().getTovar_category());
			if (edit) {
				selectedSAOTEdit.getServApp().setContract_number(selectedContr.getContract().getContract_number());
				selectedSAOTEdit.setContr_num(selectedContr.getContract().getContract_number().toString());
				selectedSAOTEdit.setContract(selectedContr.getContract());
				selectedSAOTEdit.setCustomer(selectedContr.getCustomer());
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("ServAppEditForm:contract");		
					
				
			} else {
				newSA.setContract_number(selectedContr.getContract().getContract_number());
				if (selectedContr.getContrType().getTovar_category() == 1)
					newSA.setApp_type(3L);
				else if (selectedContr.getContrType().getTovar_category() == 2)
					newSA.setApp_type(1L);
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("ServAppCreateForm:contract");
				reqCtx.update("ServAppCreateForm:type");
			}
			GeneralUtil.hideDialog("ContractWidget");
		}
	}

	public void clearContractField() {
		selectedContr = new ContractDetails();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (edit) {
			selectedSAOTEdit.getServApp().setContract_number(null);
			selectedSAOTEdit.setContr_num(null);
			selectedSAOTEdit.setContract(new Contract());
			selectedSAOTEdit.setCustomer(new Customer());
			reqCtx.update("ServAppEditForm:contract");
		} else {
			newSA.setContract_number(null);
			reqCtx.update("ServAppCreateForm:contract");
		}
	}

	// ************************************************************************************
	private List<Staff> l_dealer;

	public List<Staff> getL_dealer() {
		return l_dealer;
	}

	public void setL_dealer(List<Staff> l_dealer) {
		this.l_dealer = l_dealer;
	}

	public void loadDealerList() {
		l_dealer = new ArrayList<Staff>();
		// SalistService ss = new SalistService();
		Branch wa_bra = p_branchF4Bean.getL_branch_map().get(
				servappModel.searchModel.getBranch_id());
		// l_dealer = ss.loadDealers(wa_bra);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm");
	}

	public void prepareContractSelect() {
		loadDealerList();
	}

	// *********************************************************************
	public void createSA() {
		try {
			if (newSA != null) {
				newSA.setId(null);
				// System.out.println("SA Type: " + newSA.getApp_type());
				if (newSA.getApp_type() != null && newSA.getApp_type() > 0) {
					// System.out.println("Inside CREATE ServApp!");
					ServAppService saServDao = (ServAppService) appContext
							.getContext().getBean("servAppService");
					saServDao.createServApp(newSA, userData.getUserid(),
							transaction_code);
					GeneralUtil.addMessage(
							"Successful!",
							"A new service application "
									+ newSA.getApp_number()
									+ " successfully created");
					GeneralUtil.hideDialog("ServAppCreateDialog");
					newSA = new ServiceApplication();
					loadServAppOutputTable();
				} else {
					// System.out.println("SA Type: " + newSA.getApp_type());
					GeneralUtil.addMessage("Empty Service Apllication type!",
							"Please select the type of Service Application");
					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("ServAppCreateForm");
				}
			}
		} catch (DAOException ex) {
			GeneralUtil.addMessage(null, ex.getMessage());
		}
	}

	public void updateSA() {
		try {
			if (selectedSAOTEdit.getServApp() != null) {
				ServiceApplication sa = selectedSAOTEdit.getServApp();
				// System.out.println("SA Type: " + newSA.getApp_type());
				if (sa.getApp_type() != null && sa.getApp_type() > 0) {
					// System.out.println("Inside CREATE ServApp!");
					ServAppService saServDao = (ServAppService) appContext
							.getContext().getBean("servAppService");
					saServDao.updateServApp(sa, userData.getUserid(),
							transaction_code);
					GeneralUtil.addMessage("Successful!",
							"Service application " + sa.getApp_number()
									+ " successfully updated");
					GeneralUtil.hideDialog("ServAppEditDlg");
					sa = new ServiceApplication();
					loadServAppOutputTable();
				} else {
					// System.out.println("SA Type: " + newSA.getApp_type());
					GeneralUtil.addMessage("Empty Service Apllication type!",
							"Please select the type of Service Application");
					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("ServAppCreateForm");
				}
			}
		} catch (DAOException ex) {
			GeneralUtil.addMessage(null, ex.getMessage());
		}
	}

	// ***********************************************************************

	public ServappModel servappModel;

	public ServappModel getServappModel() {
		return servappModel;
	}

	public void setServappModel(ServappModel servappModel) {
		this.servappModel = servappModel;
	}

	public int getSAOutputLength() {
		return this.servappModel.getRowCount();
	}

	public void loadServAppOutputTable() {
		try {
			loadSAModel();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ServAppListForm:SAlist");
	}

	private void loadSAModel() {
		// String cond = " ";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (servappModel.getSearchModel().getApp_number() != null
				&& servappModel.getSearchModel().getApp_number() > 0) {
			reqCtx.update("ServAppListForm");
		} else {
			servappModel.searchModel.setApp_number(null);
			// reqCtx.update("ServAppListForm:app_number");
		}

		// initSAModel();
		// servappModel.setCondition(cond);
	}

	public void initSAModel() {
		servappModel = new ServappModel((ServiceApplicationDao) appContext
				.getContext().getBean("servAppDao"));
	}

	// **************CRUD**************************************************************************
	public void destroy() {

	}

	// *****************************************************************************
	public boolean dis_branch_inp;

	public boolean isDis_branch_inp() {
		return dis_branch_inp;
	}

	public void setDis_branch_inp(boolean dis_branch_inp) {
		this.dis_branch_inp = dis_branch_inp;
	}

	public void closeCreateSA() {
		GeneralUtil.hideDialog("ServAppCreateDialog");
		newSA = new ServiceApplication();
	}

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
			contractModel.searchModel.setCustomer_id(selectedCustomer.getId());
			contractModel.searchModel.customer_fio = selectedCustomer
					.getFullFIO();
		} else {
			contractModel.searchModel.setCustomer_id(null);
			contractModel.searchModel.customer_fio = " ";
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:fio");
		GeneralUtil.hideDialog("CustomerWidget");
		// selectedCustomer = new Customer();
	}

	public void clearCustomerField() {
		contractModel.searchModel.setCustomer_id(null);
		contractModel.searchModel.customer_fio = " ";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:fio");
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

	private boolean edit = false;

}