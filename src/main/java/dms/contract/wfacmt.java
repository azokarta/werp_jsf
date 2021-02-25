package dms.contract;

import f4.BlartF4;
import f4.BranchF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.AddressDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.dao.PaymentScheduleTemporaryDao;
import general.dao.PayrollDao;
import general.dao.ServCRMHistoryDao;
import general.dao.StaffDao;
import general.dao.SubCompanyDao;
import general.output.tables.ServCRMHistoryOutput;
import general.services.ContractService;
import general.tables.Address;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Currency;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.PaymentSchedule;
import general.tables.PaymentScheduleTemporary;
import general.tables.Payroll;
import general.tables.ServCRMHistory;
import general.tables.Staff;
import general.tables.SubCompany;

import java.io.Serializable;
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
import dms.promotion.PromoTable;

@ManagedBean(name = "wfacmtBean", eager = true)
@ViewScoped
public class wfacmt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2767180689812082106L;
	private final static String transaction_code = "WFACMT";
	private final static Long transaction_id = 476L;

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

		// ******************************************************************
		// ***************************BranchF4*******************************
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

		// ***************************BlartF4*******************************
		@ManagedProperty(value = "#{blartF4Bean}")
		private BlartF4 p_blartF4Bean;

		public BlartF4 getP_blartF4Bean() {
			return p_blartF4Bean;
		}

		public void setP_blartF4Bean(BlartF4 p_blartF4Bean) {
			this.p_blartF4Bean = p_blartF4Bean;
		}

		// ***************************Contract**********************************
		private Contract p_contract = new Contract();

		public Contract getP_contract() {
			return p_contract;
		}

		public void setP_contract(Contract p_contract) {
			this.p_contract = p_contract;
		}

		// *********************************************************************
		// *********FIO of Customer, Dealer, Demo secretary, and Collector******

		private String p_fio;

		public String getP_fio() {
			return p_fio;
		}

		public void setP_fio(String p_fio) {
			this.p_fio = p_fio;
		}

		private String p_fioDealer;

		public String getP_fioDealer() {
			return p_fioDealer;
		}

		public void setP_fioDealer(String p_fioDealer) {
			this.p_fioDealer = p_fioDealer;
		}

		private String p_fioDemoSec;

		public String getP_fioDemoSec() {
			return p_fioDemoSec;
		}

		public void setP_fioDemoSec(String p_fioDemoSec) {
			this.p_fioDemoSec = p_fioDemoSec;
		}

		private String p_fioColl;

		public String getP_fioColl() {
			return p_fioColl;
		}

		public void setP_fioColl(String p_fioColl) {
			this.p_fioColl = p_fioColl;
		}

		public String p_fioFitter;

		public String getP_fioFitter() {
			return p_fioFitter;
		}

		public void setP_fioFitter(String p_fioFitter) {
			this.p_fioFitter = p_fioFitter;
		}

		// *****************************************************************
		// **************************Employee*******************************

		private Staff selectedStaff = new Staff();

		public Staff getSelectedStaff() {
			return selectedStaff;
		}

		public void setSelectedStaff(Staff selectedStaff) {
			this.selectedStaff = selectedStaff;
		}

		// ********************************************************************

		private Customer selectedCustomer = new Customer();

		public Customer getSelectedCustomer() {
			return selectedCustomer;
		}

		public void setSelectedCustomer(Customer selectedCustomer) {
			this.selectedCustomer = selectedCustomer;
		}

		private Long contract_number_search;

		public Long getContract_number_search() {
			return contract_number_search;
		}

		public void setContract_number_search(Long contract_number_search) {
			this.contract_number_search = contract_number_search;
		}

		private Long contract_number;

		public Long getContract_number() {
			return contract_number;
		}

		public void setContract_number(Long contract_number) {
			this.contract_number = contract_number;
		}

		private Long contract_id;

		public Long getContract_id() {
			return contract_id;
		}

		public void setContract_id(Long contract_id) {
			this.contract_id = contract_id;
		}

		private Long contract_id_search;

		public Long getContract_id_search() {
			return contract_id_search;
		}

		public void setContract_id_search(Long contract_id_search) {
			this.contract_id_search = contract_id_search;
		}

		// *********************************************************************
		public String p_currency;

		public String getP_currency() {
			return p_currency;
		}

		public void setP_currency(String p_currency) {
			this.p_currency = p_currency;
		}

		public double p_currate;

		public double getP_currate() {
			return p_currate;
		}

		public void setP_currate(double p_currate) {
			this.p_currate = p_currate;
		}

		// *********************************************************************
		// *********************************************************************
		@PostConstruct
		public void init() {
			if (FacesContext.getCurrentInstance().getPartialViewContext()
					.isAjaxRequest()) {
				return; // Skip ajax requests.
			}
			try {
				belnr = "";
				gjahr = "";

				p_bukrs = "No";
				PermissionController.canRead(userData, transaction_id);

				if (contract_id != null) {
					contract_id_search = contract_id;
					to_search();
					contract_id = null;

				}
				if (contract_number != null) {
					contract_number_search = contract_number;
					to_search();
					contract_number = null;
				}
			} catch (DAOException ex) {
				contract_number = null;
				contract_id = null;
				GeneralUtil.addErrorMessage(ex.getMessage());
				toMainPage();
			}
		}

		public void loadLEList() {
			SubCompanyDao scDao = (SubCompanyDao) appContext.getContext().getBean(
					"subCompanyDao");
			List<SubCompany> scL = new ArrayList<SubCompany>();
			scL = scDao.findAll();

			sc_list.clear();

			for (SubCompany sc : scL) {
				if (!Validation.isEmptyString(sc.getBukrs())) {
					if (sc.getBukrs().equals(p_contract.getBukrs())
							&& (sc.getClosed_date() == null || sc.getClosed_date()
									.after(p_contract.getContract_date()))) {
						sc_list.add(sc);
					}
				}
			}
			System.out.println("Bukrs: " + p_contract.getBukrs() + "    IP found: "
					+ sc_list.size());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:subcompany");
		}

		List<SubCompany> sc_list = new ArrayList<SubCompany>();

		public List<SubCompany> getSc_list() {
			return sc_list;
		}

		public boolean ref_disc;

		public boolean isRef_disc() {
			return ref_disc;
		}

		public void setRef_disc(boolean ref_disc) {
			this.ref_disc = ref_disc;
		}

		public String belnr;
		public String gjahr;

		public String getBelnr() {
			return belnr;
		}

		public void setBelnr(String belnr) {
			this.belnr = belnr;
		}

		public String getGjahr() {
			return gjahr;
		}

		public void setGjahr(String gjahr) {
			this.gjahr = gjahr;
		}

		public String p_bukrs;

		public String getP_bukrs() {
			return p_bukrs;
		}

		public void setP_bukrs(String p_bukrs) {
			this.p_bukrs = p_bukrs;
		}

		public double remain;

		public double getRemain() {
			return remain;
		}

		public void setRemain(double remain) {
			this.remain = remain;
		}

		List<PaymentScheduleDetails> psDetL = new ArrayList<PaymentScheduleDetails>();

		public List<PaymentScheduleDetails> getPsDetL() {
			return psDetL;
		}

		public void setPsDetL(List<PaymentScheduleDetails> psDetL) {
			this.psDetL = psDetL;
		}

		private ContractService getConService() {
			ContractService contractService = (ContractService) appContext
					.getContext().getBean("contractService");
			return contractService;
		}
		
		public void to_search() {
			try {
				p_contract = new Contract();
				selectedStaff = new Staff();
				selectedCustomer = new Customer();
				p_fioColl = "";
				p_fioDealer = "";
				p_fioFitter = "";
				p_fio = "";
				p_fioDemoSec = "";
				remain = 0;

				ContractDao conDao = (ContractDao) appContext.getContext().getBean(
						"contractDao");
				if (contract_id_search != null && contract_id_search > 0) {
					p_contract = conDao.find(contract_id_search);
				} else if (contract_number_search != null
						&& contract_number_search > 0) {
					p_contract = getConService()
							.searchByContractNumber(contract_number_search);
				}				
				
				loadPremis();				
				
				if (p_contract != null) {
					loadLEList();

					p_branch = p_branchF4Bean.getL_branch_map().get(
							p_contract.getBranch_id());
					
					ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext()
							.getBean("contractTypeDao");
					ct = ctDao.find(p_contract.getContract_type_id());
					
					selectedCustomer = getConService().getCustomerById(p_contract
							.getCustomer_id());

					p_fio = selectedCustomer.getFullFIO();

					if (p_contract.getCollector() != null
							&& p_contract.getCollector() > 0) {
						selectedStaff = getConService().getStaffById(p_contract
								.getCollector());
						p_fioColl = selectedStaff.getLF();
						selectedStaff = new Staff();
					}

					p_bukrs = p_contract.getBukrs();

					// ***************************_Load_payment_schedule_*********************
					loadPaymentSchedule();
					p_currency = p_contract.getWaers();
					p_currate = p_contract.getRate();
					remain = p_contract.getPrice() - p_contract.getPaid();
					
					// ***********************************************************

					loadCustomerAddrList();
					for (Address wa_addr : addrList) {
						System.out.println("AddrId: " + wa_addr.getAddr_id());
						if (wa_addr.getAddr_id().equals(p_contract.getAddrHomeId())) {
							addrHome = wa_addr;
						}
						if (wa_addr.getAddr_id().equals(
								p_contract.getAddrServiceId())) {
							addrService = wa_addr;
						}
						if (wa_addr.getAddr_id().equals(p_contract.getAddrWorkId())) {
							addrWork = wa_addr;
						}
					}

					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("form");
				}
			} catch (DAOException ex) {
				GeneralUtil.addErrorMessage(ex.getMessage());
			}
		}
		
		private void loadPremis() {
			PayrollDao prlDao = (PayrollDao) appContext.getContext().getBean("payrollDao"); 
			l_payrollList = prlDao.findAll(" contract_number="+p_contract.getContract_number());
			
			for (Payroll p:l_payrollList) {
				if (p.getPosition_id() == 4) {
					p_dealerPremi = p.getDmbtr();
					p_fioDealer = getConService().getStaffById(p_contract.getDealer()).getFullFIO();
				} else if (p.getPosition_id() == 3) {
					p_managerPremi = p.getDmbtr();
					p_manager = getConService().getStaffById(p.getStaff_id());
					p_fioManager = p_manager.getFullFIO();
				} else if (p.getPosition_id() == 10) {
					p_directorPremi = p.getDmbtr();
					p_director = getConService().getStaffById(p.getStaff_id());
					p_fioDirector = p_director.getFullFIO();
				}				
			}			
		}
						
		
		private void loadPaymentSchedule() {
			PaymentScheduleTemporaryDao pstDao = (PaymentScheduleTemporaryDao) appContext
					.getContext().getBean("paymentScheduleTemporaryDao");

			List<PaymentScheduleTemporary> pst_list = new ArrayList<PaymentScheduleTemporary>();
			pst_list = pstDao.findAllByContractIdOrderById(p_contract.getContract_id());
			
			List<PaymentSchedule> ps_list = new ArrayList<PaymentSchedule>();
			for (PaymentScheduleTemporary pst:pst_list) {
				PaymentSchedule ps = new PaymentSchedule();
				ps.setBukrs(pst.getBukrs());
				ps.setIsFirstPayment(pst.getIsFirstPayment());
				ps.setPaid(pst.getPaid());
				ps.setPayment_date(pst.getPayment_date());
				ps.setSum(pst.getSum());
				ps_list.add(ps);
			}			
			
			psDetFirstPayment = new PaymentScheduleDetails();
			if (ps_list.size() > 0) {
				psDetFirstPayment.setPs(ps_list.get(0));
			} else {
				psDetFirstPayment.setPs(new PaymentSchedule());
			}
			psDetFirstPayment.mon_dis = true;

			psDetL = new ArrayList<PaymentScheduleDetails>();

			for (int i = 1; i < ps_list.size(); i++) {
				PaymentScheduleDetails psd = new PaymentScheduleDetails(i);
				psd.setPs(ps_list.get(i));
				psd.setMon_dis(true);

				psDetL.add(psd);
			}
			
		}

		public Branch p_branch;

		public Branch getP_branch() {
			return p_branch;
		}

		public void setP_branch(Branch p_branch) {
			this.p_branch = p_branch;
		}
		
		private ContractType ct = new ContractType();
		
		// *************************************************************************************
		
		public void toDmsc03(Long a_value) {
			GeneralUtil.doRedirect("/dms/contract/dmsc03.xhtml?contract_number="
					+ a_value);
		}
		
		public void toWfacList() {
			GeneralUtil.doRedirect("reports/dms/contract/wfaclist.xhtml");
		}
		
		private PaymentSchedule[] getPS() {
			PaymentSchedule ps1[] = new PaymentSchedule[psDetL.size() + 1];
			ps1[0] = psDetFirstPayment.getPs();
			for (int i = 1; i <= psDetL.size(); i++) {
				ps1[i] = psDetL.get(i - 1).getPs();
			}
			return ps1;
		}
		
		public void approveRequest() {
			try {
				PermissionController.canWrite(userData, this.transaction_id);
				
				p_contract.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
				PaymentSchedule ps[] = getPS();

				System.out.println("First Payment: Date:" + ps[0].getPayment_date()
						+ "  Sum: " + ps[0].getSum() + "  Paid: " + ps[0].getPaid()
						+ "  Bukrs" + ps[0].getBukrs());

				System.out.println("SAVING :");
				psDetFirstPayment.getPs().setIsFirstPayment(
						PaymentSchedule.ISFIRSTPAYMENT);
				ps[0] = psDetFirstPayment.getPs();
				System.out.println("First Payment: Date:" + ps[0].getPayment_date()
						+ "  Sum: " + ps[0].getSum() + "  Paid: " + ps[0].getPaid()
						+ "  Bukrs" + ps[0].getBukrs());

				for (int i = 1; i <= psDetL.size(); i++) {
					// PaymentSchedule nw = new PaymentSchedule();
					ps[i] = psDetL.get(i - 1).getPs();
				}

				// ********************************************************************

				if (p_contract != null) {
					p_contract.setCreated_by(userData.getUserid());
					p_contract.setFirst_payment(ps[0].getSum());
					p_contract.setWar_start(p_contract.getCreated_date());

					// System.out.println(p_contract.getAddr_dom_street());

					if (!Validation.isEmptyString(p_contract.getTovar_serial())
							&& p_contract.getTovar_serial().length() > 0) {
						p_contract.setTovar_serial(p_contract.getTovar_serial()
								.replaceAll("\\s+", ""));
					}

					// **********************************************_CREATE_CONTRACT_**********************************************

					double dmbtr = 0;

					if (getContractService().approveUniversalContract(p_contract,
							p_branch, userData, transaction_id, transaction_code, p_currency,
							p_currate, dmbtr, p_contract.getPrice(), ct.getMatnr(),
							ps, userData.getU_language())) {
						GeneralUtil.addInfoMessage("Contract successfully created "
								+ p_contract.getContract_number());
						toDmsc03(p_contract.getContract_number());
					}

					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("form");
										
				} else {
					throw new DAOException(
							"Contract is empty! Please enter the contract data!");					
				}
			} catch (DAOException ex) {
				p_contract.setContract_id(null);
				GeneralUtil.addErrorMessage(ex.getMessage());
				System.out.println(ex.getMessage());
			}
		}
		
		public ContractService getContractService() {
			ContractService contractService = (ContractService) appContext
					.getContext().getBean("contractService");
			return contractService;
		}
		
		// *************************************************************************************
		
		public void declineRequest() {
			try {
				if (getContractService().declineCutoffpriceContract(p_contract, userData, transaction_id))
					GeneralUtil.addInfoMessage("Contract successfully created "
							+ p_contract.getContract_number());

				toWfacList();
			} catch(DAOException ex) {
				GeneralUtil.addErrorMessage(ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		// *************************************************************************************
		
		public void loadCustomerAddrList() {
			addrList = new ArrayList<Address>();
			AddressDao addrDao = (AddressDao) appContext.getContext().getBean(
					"addressDao");
			addrList = addrDao.findAllByCustomerId(p_contract.getCustomer_id());
			System.out.println("Addresses loaded: " + addrList.size());
		}

		public List<Address> addrList = new ArrayList<Address>();
		public Address addrHome = new Address();
		public Address addrWork = new Address();
		public Address addrService = new Address();

		public List<Address> getAddrList() {
			return addrList;
		}

		public void setAddrList(List<Address> addrList) {
			this.addrList = addrList;
		}

		public Address getAddrHome() {
			return addrHome;
		}

		public void setAddrHome(Address addrHome) {
			this.addrHome = addrHome;
		}

		public Address getAddrWork() {
			return addrWork;
		}

		public void setAddrWork(Address addrWork) {
			this.addrWork = addrWork;
		}

		public Address getAddrService() {
			return addrService;
		}

		public void setAddrService(Address addrService) {
			this.addrService = addrService;
		}

		// ***********************************************************************

		public void toMainPage() {
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		}

		// *********************************************************************
		public PaymentSchedule ps[] = new PaymentSchedule[13];

		public PaymentSchedule[] getPs() {
			return ps;
		}

		public void setPs(PaymentSchedule[] ps) {
			this.ps = ps;
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

		// ******************************************************************************************************

		public void loadRegionsByBukrs(String a_bukrs) {
			region_list = new ArrayList<Branch>();
			for (Branch b : p_branchF4Bean.getBranch_list()) {
				if ((b.getType() == 2) && (b.getBukrs().equals(a_bukrs))) {
					region_list.add(b);
				}
			}
		}

		// ******************************************************************

		List<Branch> region_list = new ArrayList<Branch>();

		public List<Branch> getRegion_list() {
			return region_list;
		}

		// *********************************************************************************************************

		public PaymentScheduleDetails psDetFirstPayment = new PaymentScheduleDetails();

		public PaymentScheduleDetails getPsDetFirstPayment() {
			return psDetFirstPayment;
		}

		public void setPsDetFirstPayment(PaymentScheduleDetails psDetFirstPayment) {
			this.psDetFirstPayment = psDetFirstPayment;
		}
		
		// ***************************************************************************************************
		
		public Staff p_director = new Staff();
		public String p_fioDirector;
		public Staff p_manager = new Staff();
		public String p_fioManager;
		
		public double p_dealerPremi;
		public double p_managerPremi;
		public double p_directorPremi;
		private List<Payroll> l_payrollList = new ArrayList<Payroll>();
			
		public double getP_dealerPremi() {
			return p_dealerPremi;
		}


		public void setP_dealerPremi(double p_dealerPremi) {
			this.p_dealerPremi = p_dealerPremi;
		}


		public double getP_managerPremi() {
			return p_managerPremi;
		}


		public void setP_managerPremi(double p_managerPremi) {
			this.p_managerPremi = p_managerPremi;
		}


		public double getP_directorPremi() {
			return p_directorPremi;
		}


		public void setP_directorPremi(double p_directorPremi) {
			this.p_directorPremi = p_directorPremi;
		}


		public String getP_fioDirector() {
			return p_fioDirector;
		}


		public void setP_fioDirector(String p_fioDirector) {
			this.p_fioDirector = p_fioDirector;
		}


		public String getP_fioManager() {
			return p_fioManager;
		}


		public void setP_fioManager(String p_fioManager) {
			this.p_fioManager = p_fioManager;
		}


		public Staff getP_director() {
			return p_director;
		}

		public void setP_director(Staff p_director) {
			this.p_director = p_director;
		}

		public Staff getP_manager() {
			return p_manager;
		}


		public void setP_manager(Staff p_manager) {
			this.p_manager = p_manager;
		}
}
