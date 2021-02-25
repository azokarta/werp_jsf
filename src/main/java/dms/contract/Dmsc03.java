package dms.contract;

import f4.BlartF4;
import f4.BranchF4;
import f4.BukrsF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.AddressDao;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.BukrsDao;
import general.dao.ContractDao;
import general.dao.ContractHistoryDao;
import general.dao.ContractPromosDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.PaymentScheduleDao;
import general.dao.PromotionDao;
import general.dao.ServCRMHistoryDao;
import general.dao.ServFilterDao;
import general.dao.StaffDao;
import general.dao.SubCompanyDao;
import general.output.tables.ServCRMHistoryOutput;
import general.output.tables.ServFilterOutput;
import general.services.ContractService;
import general.tables.Address;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractPromos;
import general.tables.Country;
import general.tables.Currency;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.PaymentSchedule;
import general.tables.Promotion;
import general.tables.ServCRMHistory;
import general.tables.Staff;
import general.tables.SubCompany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import service.szf.CrmHistory;
import user.User;
import dms.promotion.PromoTable;

@ManagedBean(name = "dmsc03Bean", eager = true)
@ViewScoped
public class Dmsc03 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 22L;
	private final static String transaction_code = "DMSC03";
	private final static Long transaction_id = (long) 22;
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

	// ******************************************************************
	// ******************************************************************

	public String p_fioFitter;
	public String p_fioRef;

	public String getP_fioFitter() {
		return p_fioFitter;
	}

	public void setP_fioFitter(String p_fioFitter) {
		this.p_fioFitter = p_fioFitter;
	}

	public String getP_fioRef() {
		return p_fioRef;
	}

	public void setP_fioRef(String p_fioRef) {
		this.p_fioRef = p_fioRef;
	}

	// ***************************Contract**********************************
	private Contract p_contract = new Contract();
	private Contract p_refContr = new Contract();

	public Contract getP_refContr() {
		return p_refContr;
	}

	public void setP_refContr(Contract p_refContr) {
		this.p_refContr = p_refContr;
	}

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

	// ******************** List of payment history ********************
	private List<PaymentHistory> l_bkpfPaid = new ArrayList<PaymentHistory>();

	public List<PaymentHistory> getL_bkpfPaid() {
		return l_bkpfPaid;
	}

	public void setL_bkpfPaid(List<PaymentHistory> l_bkpfPaid) {
		this.l_bkpfPaid = l_bkpfPaid;
	}

	public double sum_dmbtr;

	public double sum_wrbtr;

	public double getSum_dmbtr() {
		return sum_dmbtr;
	}

	public void setSum_dmbtr(double sum_dmbtr) {
		this.sum_dmbtr = sum_dmbtr;
	}

	public double getSum_wrbtr() {
		return sum_wrbtr;
	}

	public void setSum_wrbtr(double sum_wrbtr) {
		this.sum_wrbtr = sum_wrbtr;
	}

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

	// ************************************************************************
	// ***************************ExchangeRateF4*******************************
	@ManagedProperty(value = "#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4Bean;

	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}

	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}

	List<ExchangeRate> exchangeRate_list = new ArrayList<ExchangeRate>();

	public List<ExchangeRate> getExchangeRate_list() {
		return exchangeRate_list;
	}

	// ******************************************************************

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
	// ****************************************HISTORY********************************************

	public List<ContractHistory> chL = new ArrayList<ContractHistory>();

	public List<ContractHistory> getChL() {
		return chL;
	}

	public void setChdL(List<ContractHistory> chL) {
		this.chL = chL;
	}

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
			
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			
			
			
			if (contract_id != null) {
				try {
					context.redirect(userData.getLinkToReact()+"/marketing/mainoperation/mmcv?contractNumber="+contract_id);
//					context.redirect(linkToReact+"/marketing/mainoperation/mmcv?contractNumber="+contract_id);
//					context.redirect("http://localhost:3000/marketing/mainoperation/mmcv="+contract_number);
				} catch (Exception ex) {
					GeneralUtil.addErrorMessage(ex.getMessage());
				}

			}
			if (contract_number != null) {
				try {
					context.redirect(userData.getLinkToReact()+"/marketing/mainoperation/mmcv?contractNumber="+contract_number);
//					context.redirect(linkToReact+"/marketing/mainoperation/mmcv?contractNumber="+contract_number);

				} catch (Exception ex) {
					GeneralUtil.addErrorMessage(ex.getMessage());
				}
			}
			
			
//			
//			
//			if (contract_id != null) {
//				try {
//					ExternalContext context = FacesContext.getCurrentInstance()
//							.getExternalContext();
//					context.redirect(userData.getLinkToReact()+"/marketing/mainoperation/mmcv?contractNumber="+contract_number);
////					context.redirect("http://localhost:3000/marketing/mainoperation/mmcv="+contract_number);
//				} catch (Exception ex) {
//					GeneralUtil.addErrorMessage(ex.getMessage());
//				}
//
//			}
//			if (contract_number != null) {
//				try {
//					ExternalContext context = FacesContext.getCurrentInstance()
//							.getExternalContext();
//					context.redirect(userData.getLinkToReact()+"/marketing/mainoperation/mmcv?contractNumber="+contract_number);
////					context.redirect("http://localhost:3000/marketing/mainoperation/mmcv="+contract_number);
//				} catch (Exception ex) {
//					GeneralUtil.addErrorMessage(ex.getMessage());
//				}
//			}
			
//			if (contract_id != null) {
//				contract_id_search = contract_id;
//				to_search();
//				contract_id = null;
//
//			}
//			if (contract_number != null) {
//				contract_number_search = contract_number;
//				to_search();
//				contract_number = null;
//			}
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

	public void to_search() {
		try {
			p_contract = new Contract();
			p_refContr = new Contract();
			selectedStaff = new Staff();
			selectedCustomer = new Customer();
			p_fioColl = "";
			p_fioDealer = "";
			p_fioFitter = "";
			p_fioRef = "";
			p_fio = "";
			p_fioDemoSec = "";
			remain = 0;
			ContractService contractService = (ContractService) appContext
					.getContext().getBean("contractService");

			ContractDao conDao = (ContractDao) appContext.getContext().getBean(
					"contractDao");
			if (contract_id_search != null && contract_id_search > 0) {
				p_contract = conDao.find(contract_id_search);
			} else if (contract_number_search != null
					&& contract_number_search > 0) {
				p_contract = contractService
						.searchByContractNumber(contract_number_search);
			}

			if (p_contract != null) {
				loadLEList();

				selectedCustomer = contractService.getCustomerById(p_contract
						.getCustomer_id());

				p_fio = selectedCustomer.getFullFIO();

				if (p_contract.getDealer() != null
						&& p_contract.getDealer() > 0) {
					selectedStaff = contractService.getStaffById(p_contract
							.getDealer());
					p_fioDealer = selectedStaff.getLF();
					selectedStaff = new Staff();
				}

				if (p_contract.getDemo_sc() != null
						&& p_contract.getDemo_sc() > 0) {
					selectedStaff = contractService.getStaffById(p_contract
							.getDemo_sc());
					p_fioDemoSec = selectedStaff.getLF();
					selectedStaff = new Staff();
				}

				if (p_contract.getCollector() != null
						&& p_contract.getCollector() > 0) {
					selectedStaff = contractService.getStaffById(p_contract
							.getCollector());
					p_fioColl = selectedStaff.getLF();
					selectedStaff = new Staff();
				}

				if (p_contract.getFitter() != null
						&& p_contract.getFitter() > 0) {
					selectedStaff = contractService.getStaffById(p_contract
							.getFitter());
					p_fioFitter = selectedStaff.getLF();
					selectedStaff = new Staff();
				}

				if (p_contract.getRef_contract_id() != null
						&& p_contract.getRef_contract_id() > 0) {
					Contract refcontr = new Contract();
					ContractDao contractDao = (ContractDao) appContext
							.getContext().getBean("contractDao");
					refcontr = contractDao.getByContractId(p_contract
							.getRef_contract_id());

					Customer wa_customer = new Customer();
					CustomerDao customerDao = (CustomerDao) appContext
							.getContext().getBean("customerDao");
					wa_customer = customerDao.find(refcontr.getCustomer_id());
					if (wa_customer != null && wa_customer.getId() != null) {
						p_fioRef = "SN:" + refcontr.getContract_number() + " "
								+ wa_customer.getFullFIO();
					} else {
						p_fioRef = "No customer found";
					}
				}

				if (!Validation.isEmptyLong(p_contract.getAwkey())) {
					belnr = "";
					gjahr = "";
					String awkey = p_contract.getAwkey().toString();
					if (!Validation.isEmptyString(awkey)) {
						belnr = awkey.substring(0, awkey.length() - 4);
						gjahr = awkey.substring(awkey.length() - 4,
								awkey.length());
					}
				}

				ref_disc = false;
				if (p_contract.getDiscount_from_ref() > 0) {
					ref_disc = true;
				}

				p_bukrs = p_contract.getBukrs();

				// ***************************_Load_payment_schedule_*********************
				PaymentScheduleDao psDao = (PaymentScheduleDao) appContext
						.getContext().getBean("paymentScheduleDao");

				List<PaymentSchedule> ps_list = new ArrayList<PaymentSchedule>();
				ps_list = psDao.findAllByAwkeyOrderById(p_contract.getAwkey(),p_contract.getBukrs());

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

				// ********************************************************************

				Calendar cal = Calendar.getInstance();
				if (p_contract.getContract_status_id() == 2
						|| p_contract.getContract_status_id() == 3
						|| p_contract.getContract_status_id() == 5) {
					cal.setTime(p_contract.getContract_date());
				} else if (p_contract.getContract_status_id() == 1
						|| p_contract.getContract_status_id() == 4) {
					cal.setTime(p_contract.getContract_date());

				} else if (p_contract.getContract_status_id() == 6) {
					cal.setTime(p_contract.getNew_contract_date());
				} else {
					cal.setTime(p_contract.getContract_date());
				}

				// ********************************************************************
				// **************** Load payment history **********************

				sum_dmbtr = 0;
				sum_wrbtr = 0;
				l_bkpfPaid = new ArrayList<PaymentHistory>();

				// ***************************_Load_PAYMENt_HISTORY_*******************

				String conNumbers = "";
				List<Contract> conL = conDao
						.dynamicFindContracts("ref_contract_id  = "
								+ p_contract.getContract_id());
				System.out.println("Recommended contracts: " + conL.size());
				for (Contract c : conL) {
					conNumbers += (conNumbers.length() > 0 ? ", " : "");
					conNumbers += String.valueOf(c.getContract_number());
				}

				String dynamicWhereClause = "";
				dynamicWhereClause = dynamicWhereClause + "(awkey = "
						+ p_contract.getAwkey() + " and contract_number = "
						+ p_contract.getContract_number()
						+ " and blart <> 'GW')";

				if (conL.size() > 0) {
					dynamicWhereClause += "or (contract_number in ( "
							+ conNumbers + ") and blart in ('VV','SK', 'VZ'))";
					// + " and blart <> 'AD'";
					// + " and blart <> 'GC'";
				}

				BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean(
						"bkpfDao");
				List<Bkpf> l_bkpfPayments = bkpfDao
						.dynamicFindBkpf(dynamicWhereClause);

				if (l_bkpfPayments.size() > 0) {
					int k = 0;
					for (Bkpf wa_bkpf1 : l_bkpfPayments) {
						PaymentHistory ph = new PaymentHistory(k++);
						ph.setBkpf(wa_bkpf1);

						System.out.println("BLART: " + wa_bkpf1.getBlart());
						ph.setInfo(p_blartF4Bean.getL_blart_map()
								.get(wa_bkpf1.getBlart()).getText45());

						if (wa_bkpf1.getBlart().equals("VZ")
								|| wa_bkpf1.getBlart().equals("SK")
								|| wa_bkpf1.getBlart().equals("VV")) {
							if (wa_bkpf1.getBlart().equals("VZ")
									|| wa_bkpf1.getBlart().equals("SK"))
								ph.setInfo(ph.getInfo() + " от");
							ph.setInfo(ph.getInfo() + " "
									+ wa_bkpf1.getContract_number());
						}
						
						l_bkpfPaid.add(ph);
						if (wa_bkpf1.getBlart().equals("CP")
								|| wa_bkpf1.getBlart().equals("SK")) {
							sum_dmbtr = sum_dmbtr + wa_bkpf1.getDmbtr();
							sum_wrbtr = sum_wrbtr + wa_bkpf1.getWrbtr();
						}
					}
				}

				remain = p_contract.getPrice() - p_contract.getPaid();

				String cond = " contract_number = "
						+ p_contract.getContract_number()
						+ " and blart = 'GC' and storno = 0";
				Bkpf bkpf = bkpfDao.dynamicFindSingleBkpf(cond,p_contract.getBukrs());

				if (bkpf != null && !Validation.isEmptyString(bkpf.getWaers())) {
					p_currency = bkpf.getWaers();
					p_currate = bkpf.getKursf();
				}

				// ***********************************************************
				loadPromoTable();
				loadContractHistory();

				// ***********************************************************
				loadCustomerAddrList();
				for (Address wa_addr : addrList) {
					System.out.println("AddrId: " + wa_addr.getAddr_id());
					if (wa_addr.getAddr_id().equals(p_contract.getAddrHomeId())) {
						addrHome = wa_addr;
						// System.out.println("HomeAddress: " +
						// addrHome.getAddress());
					}
					if (wa_addr.getAddr_id().equals(
							p_contract.getAddrServiceId())) {
						addrService = wa_addr;
						// System.out.println("ServiceAddress: " +
						// addrService.getAddress());
					}
					if (wa_addr.getAddr_id().equals(p_contract.getAddrWorkId())) {
						addrWork = wa_addr;
						// System.out.println("WorkAddress: " +
						// addrWork.getAddress());
					}
				}

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
			}

		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

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

	public void loadContractHistory() {
		ContractHistoryDao chDao = (ContractHistoryDao) appContext.getContext()
				.getBean("contractHistoryDao");
		chL = chDao.findAllByContractID(p_contract.getContract_id());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contractHistoryTable");
	}

	public void toMainPage() {
		GeneralUtil.doRedirect("/general/mainPage.xhtml");
	}

	// ******************************************************************

	public void go_editPage() {
		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			String wa_link = new String();

			if (p_contract.getContract_number() != null
					&& p_contract.getContract_number() > 0) {
				wa_link = "/dms/contract/dmsc02.xhtml?contract_number="
						+ p_contract.getContract_number();
			} else {
				throw new DAOException("Empty contract number!");
			}

			context.redirect(context.getRequestContextPath() + wa_link);
		} catch (Exception ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void go_toNewContractPage() {
		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			String wa_link = new String();
			wa_link = "/dms/contract/dmsc01.xhtml";

			context.redirect(context.getRequestContextPath() + wa_link);
		} catch (Exception ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// *********************************************************************
	public PaymentSchedule ps[] = new PaymentSchedule[13];

	public PaymentSchedule[] getPs() {
		return ps;
	}

	public void setPs(PaymentSchedule[] ps) {
		this.ps = ps;
	}

	// *********************************************************************
	// *********************** PROMO-CAMPAIGN ******************************
	// *********************************************************************

	public void loadPromoTable() {

		List<Promotion> pm_list = new ArrayList<Promotion>();
		promoTable = new ArrayList<PromoTable>();

		// PromotionService promoService = (PromotionService) appContext
		// .getContext().getBean("promotionService");
		PromotionDao promoDao = (PromotionDao) appContext.getContext().getBean(
				"promotionDao");

		pm_list = promoDao.findAll();

		System.out.println("Total PM List found: " + pm_list.size());

		Map<Long, Promotion> pm_list_map = new HashMap<Long, Promotion>();
		for (Promotion pp : pm_list) {
			pm_list_map.put(pp.getId(), pp);
		}

		ContractPromosDao cpDao = (ContractPromosDao) appContext.getContext()
				.getBean("cpDao");
		List<ContractPromos> cps = cpDao.findAllByContrID(p_contract
				.getContract_id());

		System.out.println("Total Promos on this Contract: " + cps.size());

		BukrsF4 bukrsF4 = new BukrsF4();
		int index = 0;
		p_promoName = "";

		for (ContractPromos cp : cps) {
			index++;
			PromoTable pt = new PromoTable(index);

			Promotion p = pm_list_map.get(cp.getPromo_id());

			if (p != null) {
				pt.setPromo(p);

				if (p.getBranch_id() != null && p.getBranch_id() > 0) {
					pt.setBranch(p_branchF4Bean.getL_branch_map().get(
							p.getBranch_id()));
				}

				for (Bukrs b : bukrsF4.getBukrs_list()) {
					if (b.getBukrs().equals(p.getBukrs())) {
						pt.setBukr(b);
					}
				}

				if (p.getCountry_id() != null && p.getCountry_id() > 0) {
					for (Country c : p_countryF4Bean.getCountry_list()) {
						if (c.getCountry_id() == p.getCountry_id()) {
							pt.setCountry(c);
						}
					}
				}

				pt.setMatnr(p_matnrF4Bean.getL_matnr_map().get(p.getMatnr()));

				promoTable.add(pt);

				if (index > 1) {
					p_promoName += ", ";
				}

				p_promoName += p.getName();

				System.out.println(index + ". Promo: " + p.getName());
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionListForm");
		reqCtx.update("form:promoName");
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
	// *****************************************************************************************************

	public List<PromoTable> selectedPTS;

	public List<PromoTable> getSelectedPTS() {
		return selectedPTS;
	}

	public void setSelectedPTS(List<PromoTable> selectedPTS) {
		this.selectedPTS = selectedPTS;
	}

	public List<PromoTable> promoTable;

	public List<PromoTable> getPromoTable() {
		return promoTable;
	}

	public void setPromoTable(List<PromoTable> promoTable) {
		this.promoTable = promoTable;
	}

	// ******************************************************************************************************

	public void loadPromoMatnrListByBukrs(String a_bukrs) {
		p_matnr_list = new ArrayList<Matnr>();
		for (Matnr m : p_matnrF4Bean.getByBukrs(p_contract.getBukrs())) {
			if (m.getType() == 8) {
				p_matnr_list.add(m);
				// System.out.println(m.getText45());
			}
		}
	}

	public void loadRegionsByBukrs(String a_bukrs) {
		region_list = new ArrayList<Branch>();
		for (Branch b : p_branchF4Bean.getBranch_list()) {
			if ((b.getType() == 2) && (b.getBukrs().equals(a_bukrs))) {
				region_list.add(b);
			}
		}
	}

	// ***************************MatnrF4*****************************************************
	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;

	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}

	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}

	public List<Matnr> p_matnr_list = new ArrayList<Matnr>();

	public List<Matnr> getP_matnr_list() {
		return p_matnr_list;
	}

	// ******************************************************************

	List<Branch> region_list = new ArrayList<Branch>();

	public List<Branch> getRegion_list() {
		return region_list;
	}

	public void assignSelectedPromos() {
		p_promoName = "";

		promos = new Long[selectedPTS.size()];

		if (selectedPTS != null) {
			for (int i = 0; i < selectedPTS.size(); i++) {
				promos[i] = selectedPTS.get(i).getPromo().getId();
				if (i > 0)
					p_promoName += ", ";
				p_promoName += selectedPTS.get(i).getPromo().getName();
				// System.out.println("Selected Promos: " + p_promoName);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:promoName");
		reqCtx.execute("PF('PromotionDlg').hide();");
	}

	private Long promos[];

	public String p_promoName;

	public String getP_promoName() {
		return p_promoName;
	}

	public void setP_promoName(String p_promoName) {
		this.p_promoName = p_promoName;
	}

	// *********************************************************************************************************

	public PaymentScheduleDetails psDetFirstPayment = new PaymentScheduleDetails();

	public PaymentScheduleDetails getPsDetFirstPayment() {
		return psDetFirstPayment;
	}

	public void setPsDetFirstPayment(PaymentScheduleDetails psDetFirstPayment) {
		this.psDetFirstPayment = psDetFirstPayment;
	}

	// *********************************************************************************************************
	public void GoToEditPrice() {
		GeneralUtil
				.doRedirect("/dms/contract/editpricedmsc.xhtml?contract_number="
						+ p_contract.getContract_number());
	}

	// *********************************************************************************************************

	public void prepareCRMHisDlg() {
		loadSCHOutput();
		//onclick="PF('servHistoryWidget').show();"
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("servHisForm");		
		GeneralUtil.showDialog("servHistoryWidget");
		//reqCtx.update("form");
	}
	
	public void loadSCHOutput() {
		crmHisL = new ArrayList<ServCRMHistoryOutput>();
		schL = getCRMHistory(p_contract.getContract_id());
		int i = 1;
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");
		for (ServCRMHistory sch : schL) {
			ServCRMHistoryOutput schO = new ServCRMHistoryOutput(i);
			schO.setSch(sch);
			if (!Validation.isEmptyLong(sch.getStaffId())) {
				Staff stf = stfDao.find(sch.getStaffId());
				schO.setStaff(stf);
			}
			crmHisL.add(schO);
			i++;
		}
		System.out.println("ServHistory found: " + schL.size());		
	}
	
	public List<ServCRMHistory> getCRMHistory(Long con_id) {
		ServCRMHistoryDao crmhDao = (ServCRMHistoryDao) appContext.getContext()
				.getBean("servCRMHistoryDao");
		List<ServCRMHistory> crmhL = crmhDao.findAllByContractID(con_id);
		return crmhL;
	}
	
	public List<ServCRMHistoryOutput> crmHisL;
	public List<ServCRMHistory> schL;
	
	public List<ServCRMHistory> getSchL() {
		return schL;
	}

	public void setSchL(List<ServCRMHistory> schL) {
		this.schL = schL;
	}

	public List<ServCRMHistoryOutput> getCrmHisL() {
		return crmHisL;
	}

	public void setCrmHisL(List<ServCRMHistoryOutput> crmHisL) {
		this.crmHisL = crmHisL;
	}
}
