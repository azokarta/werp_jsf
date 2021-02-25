package dms.contract;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.PaymentTemplateF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.services.ContractService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.ContractStatus;
import general.tables.Country;
import general.tables.Currency;
import general.tables.PaymentTemplate;
import general.tables.Role_action;
import general.tables.Staff;
import general.tables.search.ContractSearch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;
import user.UserRoleActions;

@ManagedBean(name = "dmscrepayBean", eager = true)
@ViewScoped
public class Dmscrepay {
	private final static String transaction_code = "DMSCREPAY";
	private final static Long transaction_id = (long) 111;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;
	public static Long getTransactionId() {
		return transaction_id;
	}

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
	// ***************************CurrencyF4*******************************
	@ManagedProperty(value = "#{currencyF4Bean}")
	private CurrencyF4 p_currencyF4Bean;

	public CurrencyF4 getP_currencyF4Bean() {
		return p_currencyF4Bean;
	}

	public void setP_currencyF4Bean(CurrencyF4 p_currencyF4Bean) {
		this.p_currencyF4Bean = p_currencyF4Bean;
	}

	List<Currency> currency_list = new ArrayList<Currency>();

	public List<Currency> getCurrency_list() {
		return currency_list;
	}

	// ******************************************************************
	// ***************************ContractType*******************************
	@ManagedProperty(value = "#{contractF4Bean}")
	private ContractTypeF4 p_contracTypeF4Bean;

	public ContractTypeF4 getP_contracTypeF4Bean() {
		return p_contracTypeF4Bean;
	}

	public void setP_contracTypeF4Bean(ContractTypeF4 p_contracTypeF4Bean) {
		this.p_contracTypeF4Bean = p_contracTypeF4Bean;
	}

	// ******************************************************************
	// ***************************ContractStatus*******************************
	@ManagedProperty(value = "#{contractStatusF4Bean}")
	private ContractStatusF4 p_contracStatusF4Bean;

	public ContractStatusF4 getP_contracStatusF4Bean() {
		return p_contracStatusF4Bean;
	}

	public void setP_contracStatusF4Bean(ContractStatusF4 p_contracStatusF4Bean) {
		this.p_contracStatusF4Bean = p_contracStatusF4Bean;
	}

	// ******************************************************************
	// ***************************PaymentTemplateF4*******************************
	@ManagedProperty(value = "#{paymentTemplateF4Bean}")
	private PaymentTemplateF4 p_paymentTemplateF4Bean;

	public PaymentTemplateF4 getP_paymentTemplateF4Bean() {
		return p_paymentTemplateF4Bean;
	}

	public void setP_paymentTemplateF4Bean(
			PaymentTemplateF4 p_paymentTemplateF4Bean) {
		this.p_paymentTemplateF4Bean = p_paymentTemplateF4Bean;
	}

	List<PaymentTemplate> paymentTemplate_list = new ArrayList<PaymentTemplate>();

	public List<PaymentTemplate> getPaymentTemplate_list() {
		return paymentTemplate_list;
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

	// ******************************************************************
	// ******************************************************************

	@PostConstruct
	public void init() {
		try {
			p_contract = new Contract();

			PermissionController.canWrite(userData, transaction_id);
			if (userData.isMain()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
			} else {
				bukrs_list.add(p_bukrsF4Bean.getBukrsByBukrs(
					userData.getBukrs()).get(0));

				System.out.println("User name: " + userData.getUsername());
				System.out.println("User Branch: " + userData.getBranch_id());
				p_contract.setBranch_id(userData.getBranch_id());
				p_contract.setBukrs(userData.getBukrs());
			}

			for (UserRoleActions wa_ura : userData.getUra_list()) {
				if (wa_ura.getRole_id() == 2) {
					statusChangeRole = false;
				}
			}

			loadBranch();

			disableSave = true;
			//searchContractsByCS(searchCSId);

			p_bukrs = "";
			p_branch_id = 0L;
			p_contr_num = 0L;
		} catch (DAOException ex) {
			addMessage("Error", ex.getMessage());
			toMainPage();
		}
	}

	public void loadBranch() {
		branch_list = new ArrayList<Branch>();
		p_contract.setBranch_id(null);
		p_contract.setContract_type_id(null);
		p_contract.setPrice_list_id(null);
		p_branch_id = 0L;

		branch_list = loadBranchList(conSearch.getBukrs());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		reqCtx.update("form:branchref");
		reqCtx.update("form:contract_type_id");
		reqCtx.update("form:price_list_id");

	}

	public List<Branch> loadBranchList(String a_bukr) {
		List<Branch> wa_brlist = new ArrayList<Branch>();

		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			// System.out.println(wa_branch.getText45());
			if (a_bukr != null && a_bukr.equals(wa_branch.getBukrs()) && wa_branch.getType() == 3 && (
					wa_branch.getBusiness_area_id() == null ||				
					(wa_branch.getBusiness_area_id() != null && wa_branch.getBusiness_area_id() != 5))) {
				wa_brlist.add(wa_branch);
			}
		}

		return wa_brlist;
	}

	public void addMessage(String summary, String detail) {
		GeneralUtil.addMessage(summary, detail);
	}

	public void toMainPage() {
		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			context.redirect(context.getRequestContextPath()
					+ "/general/mainPage.xhtml");
		} catch (Exception ex) {
			addMessage("Error", ex.getMessage());
		}
	}

	// *****************************************************************************************************

	public void search() {
		cdOutputList = new ArrayList<ContractDetails>();
		int i = 0;
		for (ContractDetails cd : cdList) {
			System.out.println("Inspecting contract.");
			if (!Validation.isEmptyLong(conSearch.getContract_number())) {
				if (conSearch.getContract_number().equals(cd.getContract().getContract_number())) {
					cdOutputList.add(cd);
					break;
				}
			} else {
				if (!Validation.isEmptyString(conSearch.getBukrs())) {
					if ((!Validation.isEmptyLong(conSearch.getBranch_id())
							&& (cd.getContract().getBranch_id().equals(conSearch.getBranch_id()))) 
							|| (Validation.isEmptyLong(conSearch.getBranch_id()) 
									&& conSearch.getBukrs().equals(cd.getContract().getBukrs()))) {
						cdOutputList.add(cd);
						cd.setIndex(i++);
						System.out.println("Contract added!");
					}
				} 
			}
		}

		outputLength = cdOutputList.size();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:outputTable");
	}
	
	public void checkBukrs() {
		if (p_bukrs.length() == 0) {
			//Branch wa_branch = p_branchF4Bean.getL_branch_map().get(p_branch_id);
			p_bukrs = conSearch.getBukrs();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bukrs");
		}
	}

	public void searchContractsByCS() {
		try {
			ContractDao conDao = (ContractDao) appContext.getContext().getBean(
					"contractDao");
			
			//String wcl = "(last_state = 1 or last_state = 3)";
			String wcl = " contract_status_id = " + ContractStatus.STATUS_FORCANCEL;
			if (!Validation.isEmptyLong(conSearch.getContract_number()))
				wcl += " and contract_number = " + conSearch.getContract_number();
			else {
				if (!Validation.isEmptyString(conSearch.getBukrs())) 
					wcl += " and bukrs = " + conSearch.getBukrs();
				
				if (!Validation.isEmptyLong(conSearch.getBranch_id())) 
					wcl += " and branch_id = " + conSearch.getBranch_id();
			}
			List<Contract> conList = conDao.dynamicFindContracts(wcl);
			System.out.println("Contracts found: " + conList.size());
			cdList = new ArrayList<ContractDetails>();
			cdOutputList = new ArrayList<ContractDetails>();
			ContractDetails cd;
			int ind = 0;
			if (conList.size() > 0) {
				for (Contract con : conList) {
					cd = new ContractDetails(ind++, con);

					cd.setBranch(p_branchF4Bean.getL_branch_map().get(
							con.getBranch_id()));
					cd.setBukrs(p_bukrsF4Bean.getBukrsByBukrs(con.getBukrs())
							.get(0));

//					wcl = "contract_number = " + con.getContract_number()
//							+ " and blart = 'GC' and storno=0";
//					BkpfDao bkpfDao = (BkpfDao) appContext.getContext()
//							.getBean("bkpfDao");
//					cd.setBkpf(bkpfDao.dynamicFindSingleBkpf(wcl));

					ContractTypeDao ctDao = (ContractTypeDao) appContext
							.getContext().getBean("contractTypeDao");
					cd.setContrType(ctDao.find(con.getContract_type_id()));

					CustomerDao cusDao = (CustomerDao) appContext.getContext()
							.getBean("customerDao");
					cd.setCustomer(cusDao.find(con.getCustomer_id()));

					StaffDao stfDao = (StaffDao) appContext.getContext()
							.getBean("staffDao");

					Staff staff = new Staff();
					if (con.getDealer() != null && con.getDealer() > 0) {
						staff = stfDao.find(con.getDealer());
						if (staff != null && staff.getStaff_id() > 0) {
							cd.setDealer(staff);
						}
					}

//					if (con.getDemo_sc() != null && con.getDemo_sc() > 0) {
//						staff = stfDao.find(con.getDemo_sc());
//						if (staff != null && staff.getStaff_id() > 0) {
//							cd.setDemosec(staff);
//						}
//					}
//
//					if (con.getFitter() != null && con.getFitter() > 0) {
//						staff = stfDao.find(con.getFitter());
//						if (staff != null && staff.getStaff_id() > 0) {
//							cd.setFitter(staff);
//						}
//					}
//
//					if (con.getCollector() != null && con.getCollector() > 0) {
//						staff = stfDao.find(con.getCollector());
//						if (staff != null && staff.getStaff_id() > 0) {
//							cd.setCollector(staff);
//						}
//					}

					cd.setContract(con);
//					PaymentScheduleDao psDao = (PaymentScheduleDao) appContext
//							.getContext().getBean("paymentScheduleDao");
//					cd.setPsList(psDao.findAllByAwkeyOrderById(con.getAwkey()));

					cd.paid = con.getPaid();
					cd.remain = cd.price - cd.paid - cd.skidka;
					cd.repayment = 0;
//					if (cd.getPsList().size() > 0)
//						cd.firstPayment = cd.getPaymentTempList().get(0)
//							.getMonthly_payment_sum()
//							* cd.getBkpf().getKursf();
					cd.firstPayment = con.getFirst_payment();
					cd.setStatusName(p_contracStatusF4Bean.getCsMap()
							.get(con.getContract_status_id()).getName());
					cd.icon = "ui-icon-close";

					cdOutputList.add(cd);
//					cdList.add(cd);
				}
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
				System.out.println("Output list size: " + cdOutputList.size());
			} else {
				addMessage("", "No contract found!");
			}
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	public void approveRow(int ind) {
		disableSave = true;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		ContractDetails cdi = cdOutputList.get(ind);
		try {
			if (cdi.isApprove()) {
				cdi.setApprove(false);
				cdi.icon = "ui-icon-close";
				reqCtx.update("form:outputTable");
				addMessage("Disapproved", "Repayment sum disapproved.");
			} else {
				if ((cdi.repayment <= cdi.getContract().getPaid())
						//&& (cdi.repayment <= cdi.getContract().getFirst_payment())
						) {
					cdi.setApprove(true);
					cdi.icon = "ui-icon-check";
					reqCtx.update("form:outputTable:" + ind + ":approveButton");
					addMessage("Contract "
							+ cdi.getContract().getContract_number()
							+ " repayment sum is Approved",
							"Repayment sum approved.");
				} else {
					addMessage(
							"Repayment sum cannot be greater than the paid or prepayment sum!",
							"Repayment sum cannot be greater than the paid sum!");
				}
			}
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
		reqCtx.update("form:saveButton");
	}

	public void disapproveRow(int ind) {
		ContractDetails cdi = cdOutputList.get(ind);
		if (cdi.isApprove()) {
			cdi.setApprove(false);
			cdi.icon = "ui-icon-close";
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:outputTable:" + ind + ":approveButton");
			addMessage("Disapproved", "Repayment sum disapproved.");
		}
	}

	public void finishThem() {
		for (int i = 0; i < cdOutputList.size(); i++) {
			if (!cdOutputList.get(i).isApprove()) {
				cdOutputList.remove(i);
				for (int j = i; j < cdOutputList.size(); j++) {
					cdOutputList.get(j).setIndex(j);
				}
				i--;
			}
		}

		if (cdOutputList.size() > 0) {
			disableSave = false;
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:outputTable");
		reqCtx.update("form:saveButton");
	}

	public void saveThem() {
		try {
			Contract wa_con = new Contract();
			Bkpf wa_bkpf;
			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean(
					"bkpfDao");
			double repay_dmbtr;
			double repay_wrbtr;
			double wa_kursf;

			ContractService csDao = (ContractService) appContext.getContext()
					.getBean("contractService");

			for (ContractDetails wa_cd : cdOutputList) {
				wa_con = wa_cd.getContract();

				String wcl = "contract_number = " + wa_con.getContract_number()
						+ " and blart = 'GC' and storno = 0";
				wa_bkpf = bkpfDao.dynamicFindSingleBkpf(wcl,wa_con.getBukrs());

				wa_kursf = wa_bkpf.getKursf();
				repay_wrbtr = wa_cd.getRepayment();
				repay_dmbtr = repay_wrbtr / wa_kursf;

				csDao.CancelContract(wa_con, wa_bkpf, wa_kursf, repay_dmbtr,
						repay_wrbtr, userData, this.transaction_code, wa_cd.getInfo());

				GeneralUtil.addMessage("Success!",
						"Contract #" + wa_con.getContract_number()
								+ " has been cancelled!");
			}

			cdOutputList.clear();
			cdList.clear();

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			reqCtx.update("form:tableContainer");
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// *****************************************************************************************************

	public String p_currency;
	public Contract p_contract;
	public Long searchCSId;
	public boolean statusChangeRole;
	public List<ContractDetails> cdList = new ArrayList<ContractDetails>();
	public List<ContractDetails> cdOutputList = new ArrayList<ContractDetails>();
	public String p_bukrs;
	public Long p_branch_id;
	public Long p_contr_num;
	public int outputLength;
	public boolean disableSave = true;
	public ContractSearch conSearch = new ContractSearch();

	public ContractSearch getConSearch() {
		return conSearch;
	}

	public void setConSearch(ContractSearch conSearch) {
		this.conSearch = conSearch;
	}

	public boolean isDisableSave() {
		return disableSave;
	}

	public void setDisableSave(boolean disableSave) {
		this.disableSave = disableSave;
	}

	public int getOutputLength() {
		return outputLength;
	}

	public void setOutputLength(int outputLength) {
		this.outputLength = outputLength;
	}

	public Long getP_contr_num() {
		return p_contr_num;
	}

	public void setP_contr_num(Long p_contr_num) {
		this.p_contr_num = p_contr_num;
	}

	public String getP_bukrs() {
		return p_bukrs;
	}

	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	public Long getP_branch_id() {
		return p_branch_id;
	}

	public void setP_branch_id(Long p_branch_id) {
		this.p_branch_id = p_branch_id;
	}

	public List<ContractDetails> getCdOutputList() {
		return cdOutputList;
	}

	public void setCdOutputList(List<ContractDetails> cdOutputList) {
		this.cdOutputList = cdOutputList;
	}

	public Long getSearchCSId() {
		return searchCSId;
	}

	public void setSearchCSId(Long searchCSId) {
		this.searchCSId = searchCSId;
	}

	public List<ContractDetails> getCdList() {
		return cdList;
	}

	public void setCdList(List<ContractDetails> cdList) {
		this.cdList = cdList;
	}

	public boolean isStatusChangeRole() {
		return statusChangeRole;
	}

	public void setStatusChangeRole(boolean statusChangeRole) {
		this.statusChangeRole = statusChangeRole;
	}

	public Contract getP_contract() {
		return p_contract;
	}

	public void setP_contract(Contract p_contract) {
		this.p_contract = p_contract;
	}

	public String getP_currency() {
		return p_currency;
	}

	public void setP_currency(String p_currency) {
		this.p_currency = p_currency;
	}

}
