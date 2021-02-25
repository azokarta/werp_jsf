package service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.ServConMatnrWarDao;
import general.dao.ServPacketDao;
import general.dao.ServPacketPosDao;
import general.dao.ServPacketWarDao;
import general.dao.ServPosDao;
import general.dao.ServiceDao;
import general.dao.StaffDao;
import general.output.tables.ServConMatnrWarOutput;
import general.output.tables.ServPacketWarOutput;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Currency;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.ServConMatnrWar;
import general.tables.ServPacket;
import general.tables.ServPacketPos;
import general.tables.ServPacketWar;
import general.tables.ServicePos;
import general.tables.ServiceTable;
import general.tables.Staff;
import general.tables.search.ServiceSearch;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import logistics.order.MatnrPriceList;

import org.primefaces.context.RequestContext;

import user.User;
import dms.contract.ContractDetails;

@ManagedBean(name = "service03Bean", eager = true)
@ViewScoped
public class Service03 {
	/**
	 * 
	 */
	private static final long serialVersionUID = 97L;
	private final static String transaction_code = "SERVICE03";
	private final static Long transaction_id = (long) 97;
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

	// *********************************************************************
	// ***************** Get Currency & Rate from branch *******************

	public String getCurrencyName(Long wa_branch_id) {
		String out;
		out = "";
		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (wa_branch.getBranch_id().equals(wa_branch_id)) {
				for (Country wa_country : p_countryF4Bean.getCountry_list()) {
					if (wa_country.getCountry_id().equals(
							wa_branch.getCountry_id())) {
						for (Currency wa_currency : p_currencyF4Bean
								.getCurrency_list()) {
							if (wa_currency.getCurrency_id().equals(
									wa_country.getCurrency_id())) {
								out = wa_currency.getCurrency();
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return out;
	}

	public double getCurrencyRate(String main_currency, String sec_currency) {
		double out;
		out = 1;
		ExchangeRate wa_exr = p_exchangeRateF4Bean.getL_er_map_national().get(
				"1" + sec_currency);
		out = wa_exr.getSc_value();

		return out;
	}

	// *********************************************************************

	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}
		p_service = new ServiceTable();
		try {
			PermissionController.canRead(userData, this.transaction_id);
			if (service_number != null) {
				p_service_search.setServ_num(service_number);
				if (!userData.isSys_admin()) {
					try {					

						ExternalContext context = FacesContext.getCurrentInstance()
								.getExternalContext();
						
							context.redirect(userData.getLinkToReact()+"/service/mainoperation/smvs?serviceNumber="+service_number);
					} catch (Exception ex) {
						GeneralUtil.addErrorMessage(ex.getMessage());
					}
				}
				
				
//				to_search();
//				service_number = null;
			}
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
			toMainPage();
		}
	}

	public void to_search() {
		try {
			ServiceDao sDao = (ServiceDao) appContext.getContext().getBean("serviceDao");
			System.out.println(p_service_search.getCondition()); 
			p_service = sDao.dynamicFindSingle(p_service_search.getCondition());
			if (Validation.isEmptyString(p_service.getBukrs())) 
				GeneralUtil.addInfoMessage("Service not found!");
			else {
				System.out.println("Bukrs: " + p_service.getBukrs());
				System.out.println("Paid: " + p_service.getPaid());
				
				ContractDao conDao = (ContractDao) appContext.getContext().getBean("contractDao");
				p_contract = conDao.find(p_service.getContract_id());
				
//				ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext().getBean("contractTypeDao");
//				ct = ctDao.find(p_contract.getContract_type_id());
				
				MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
				Matnr matnr = mDao.find(p_service.getTovar_id());
				
				p_tovar_name = matnr.getText45();
				
				CustomerDao cusDao = (CustomerDao) appContext.getContext().getBean("customerDao");
				p_customer = cusDao.find(p_service.getCustomer_id());
				
				StaffDao stfDao = (StaffDao) appContext.getContext().getBean("staffDao");
				p_master = stfDao.find(p_service.getMaster_id());
				if (p_service.getOper_id() != null && p_service.getOper_id() > 0)
					p_oper = stfDao.find(p_service.getOper_id());
				
				// Load ServPos List
				ServPosDao spDao = (ServPosDao) appContext.getContext().getBean("servPosDao");
				servPosListTable = new ArrayList<ServPosListTable>();
				List<ServicePos> sp_l = spDao.findAllByServNumber(p_service.getServ_num());
				int i = 1;
				sum_wrbtr = 0;
				sum_dmbtr = 0;
				for (ServicePos sp:sp_l) {
					ServPosListTable splt = new ServPosListTable();
					splt.setServPos(sp);
					splt.setDis_mat(true);
					splt.setDis_qq(true);
					splt.setGroup(1);
					splt.setIndex(i++);
					if (sp.getMatnr_id() != null && sp.getMatnr_id() > 0) {
						Matnr wa_matnr = mDao.find(sp.getMatnr_id());
						splt.setMatnr(wa_matnr);
					}
					splt.setSum_sc(sp.getSumm());
					sum_wrbtr += sp.getSumm();
					// no dmbtr
					if (sp.getWarranty() > 0)
						splt.setWarranty(true);
					else splt.setWarranty(false);
					servPosListTable.add(splt);
				}
				
				
				String awkey = ((p_service.getAwkey() != null && p_service.getAwkey() > 0) ? p_service.getAwkey().toString() : ""); 
				belnr = "";
				gjahr= "";				
				if (!Validation.isEmptyString(awkey)) {
					belnr = awkey.substring(0, awkey.length() - 4);
					gjahr = awkey.substring(awkey.length() - 4, awkey.length());
				}  					
				
				disPayBtn = false;
				if ((p_service.getServ_status() == 4 && p_service.getPaid() >= p_service.getPayment_due())
						|| p_service.getServ_type() == 2 || p_service.getServ_status() == ServiceTable.STATUS_CANCELLED)
					disPayBtn = true;
				
				disFinBtn = false;
				if (p_service.getServ_type() == 2)
					disFinBtn = true;
				
				loadSpList();
				loadSpWarDetails(p_service.getId());
				
				if (p_service.getServ_type() == 4) 
					enableServPacket();
				else 
					disableServPacket();
				
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		}
		catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	public boolean disFinBtn = false;
	public boolean isDisFinBtn() {
		return disFinBtn;
	}
	public void setDisFinBtn(boolean disFinBtn) {
		this.disFinBtn = disFinBtn;
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
	ServiceSearch p_service_search = new ServiceSearch();
	public ServiceSearch getP_service_search() {
		return p_service_search;
	}
	public void setP_service_search(ServiceSearch p_service_search) {
		this.p_service_search = p_service_search;
	}

	public Long service_number;
	public Long getService_number() {
		return service_number;
	}
	public void setService_number(Long service_number) {
		this.service_number = service_number;
	}

	public List<general.tables.User> user_list = new ArrayList<general.tables.User>();

	public List<general.tables.User> getUser_list() {
		return user_list;
	}

	public List<ServPosListTable> servPosListTable;
	
	public void toMainPage() {
		GeneralUtil.doRedirect("/general/mainPage.xhtml");
	}
	
	public void toEditPage() {
		if (p_service != null && p_service.getServ_num() != null && p_service.getServ_num() > 0)
			GeneralUtil.doRedirect("/service/maintenance/service02.xhtml?service_number=" + p_service.getServ_num());
	}
	
	public void toPaymentPage() {
		if (p_service != null && p_service.getAwkey() != null && p_service.getAwkey() > 0) {
			String a = p_service.getAwkey().toString();
			System.out.println("Awkey = " + a);
			String belnr = a.substring(0,a.length()-4);
			System.out.println("Belnr: " + belnr);
			String gjahr = a.substring(a.length()-4,a.length());
			System.out.println("Gjahr: " + gjahr);
			GeneralUtil.doRedirect("/finance/cashbankoperations/faci01.xhtml?belnr=" + belnr 
					+ "&gjahr=" + gjahr + "&bukrs=" + p_service.getBukrs());
		}	
	}
	
	public List<Branch> loadBranchList(String a_bukr) {
		List<Branch> wa_brlist = new ArrayList<Branch>();

		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (a_bukr.equals(wa_branch.getBukrs()) && wa_branch.getType() == 3) {
				if (wa_branch.getBusiness_area_id() != null
						&& wa_branch.getBusiness_area_id().intValue() == 5)
					wa_brlist.add(wa_branch);
			}
		}
		return wa_brlist;
	}

	// ***************************************************************************************
	// ***************************MatnrF4*****************************************************

	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;
	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}
	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}

	public List<MatnrPriceList> p_matnr_list = new ArrayList<MatnrPriceList>();
	public List<MatnrPriceList> getP_matnr_list() {
		return p_matnr_list;
	}
	public void setP_matnr_list(List<MatnrPriceList> p_matnr_list) {
		this.p_matnr_list = p_matnr_list;
	}

	private MatnrPriceList selectedMatnr = new MatnrPriceList();
	public MatnrPriceList getSelectedMatnr() {
		return selectedMatnr;
	}
	public void setSelectedMatnr(MatnrPriceList selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public boolean dis_selectCustomer;
	public boolean isDis_selectCustomer() {
		return dis_selectCustomer;
	}
	public void setDis_selectCustomer(boolean dis_selectCustomer) {
		this.dis_selectCustomer = dis_selectCustomer;
	}

	public boolean dis_selectMatnr;
	public boolean isDis_selectMatnr() {
		return dis_selectMatnr;
	}
	public void setDis_selectMatnr(boolean dis_selectMatnr) {
		this.dis_selectMatnr = dis_selectMatnr;
	}

	public void calcRow(int index) {
		int pos = index - 1;
		int sumRow = 0;

		ServicePos sp2 = servPosListTable.get(pos).getServPos();

		if (sp2 != null && sp2.getQuantity() != null) {
			servPosListTable.get(pos).setSum_sc(
					sp2.getQuantity() * sp2.getMatnr_price());
			servPosListTable.get(pos).getServPos().setSumm(sp2.getQuantity() * sp2.getMatnr_price());
			servPosListTable.get(pos).setSum(
					servPosListTable.get(pos).getSum_sc() / p_currate);
		}

		sum_dmbtr = 0;
		sum_wrbtr = 0;

		for (int i = 0; i < servPosListTable.size(); i++) {
			sum_dmbtr += servPosListTable.get(i).getSum();
			sum_wrbtr += servPosListTable.get(i).getSum_sc();
			sumRow += 1;
		}

		// System.out.println("Pos: " + pos);
		// System.out.println("SummaryRow: " + sumRow);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olTable:" + pos + ":b_dmbtr");
		reqCtx.update("form:olTable:" + pos + ":b_wrbtr");
		reqCtx.update("form:olTable:" + sumRow + ":summaryMC");
		reqCtx.update("form:olTable:" + sumRow + ":summarySC");
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

	public String p_mcRatetext;

	public String getP_mcRatetext() {
		return p_mcRatetext;
	}

	public ServPosListTable p_spTableRow;

	public ServPosListTable getP_spTableRow() {
		return p_spTableRow;
	}

	public void setP_spTableRow(ServPosListTable p_spTableRow) {
		this.p_spTableRow = p_spTableRow;
	}

	public void updateMCRateText() {
		//p_main_currency = p_service.getCurrency();
		p_currency = getCurrencyName(userData.getBranch_id());
		p_currate = 0;
		p_currate = getCurrencyRate(p_service.getCurrency(), p_currency);
		
		p_mcRatetext = " 1 USD = " + p_currate + " " + p_currency;

		System.out.println("Rate is: " + p_mcRatetext);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:mainCurRateToLocalCurrency");
	}

	// *****************************************************************************************************

	public void addRow() {
		servPosListTable.add(new ServPosListTable(servPosListTable.size() + 1,
				p_service.getBukrs(), p_main_currency));
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
	}

	public void deleteTableRow(int i) {
		int j = i - 1;
		servPosListTable.remove(j);

		while (j < servPosListTable.size()) {
			servPosListTable.get(j).setIndex(j + 1);
			j++;
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
	}

	public void removeEmptyRows() {
		addRow();
		for (int i = 0; i < servPosListTable.size(); i++) {
			// System.out.println("Inspecting the Row: " + i);
			if (servPosListTable.get(i).getMatnr() == null 
					|| servPosListTable.get(i).getMatnr()
					.getMatnr() == null) {
				if ((servPosListTable.get(i).getServPos().getOperation() != null)
						&& ((servPosListTable.get(i).getServPos().getOperation() == 1)
						|| (servPosListTable.get(i).getServPos().getOperation() == 2 
						&& servPosListTable.get(i).getServPos().getInfo2().isEmpty()))
					) {
					deleteTableRow(i + 1);
					//System.out.println("Deleting the row: " + i);
					i--;
				}
			}
		}
		calcRow(servPosListTable.size());
	}
	
	// ************************************************************************
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
	// *****************************************************************************************************

	public ServiceTable p_service;
	public String p_main_currency;
	public String p_currency;
	public double p_currate;
	public Customer p_customer;
	public Contract p_contract;
	public String p_contr_output;
	public Staff p_master;
	public String p_master_pos;
	public Staff p_oper;
	public String p_oper_pos;
	public String p_tovar_name;

	public String getP_tovar_name() {
		return p_tovar_name;
	}

	public void setP_tovar_name(String p_tovar_name) {
		this.p_tovar_name = p_tovar_name;
	}

	public String getP_contr_output() {
		return p_contr_output;
	}

	public void setP_contr_output(String p_contr_output) {
		this.p_contr_output = p_contr_output;
	}

	public Staff getP_master() {
		return p_master;
	}

	public void setP_master(Staff p_master) {
		this.p_master = p_master;
	}

	public Staff getP_oper() {
		return p_oper;
	}

	public void setP_oper(Staff p_oper) {
		this.p_oper = p_oper;
	}

	public String getP_master_pos() {
		return p_master_pos;
	}

	public void setP_master_pos(String p_master_pos) {
		this.p_master_pos = p_master_pos;
	}

	public String getP_oper_pos() {
		return p_oper_pos;
	}

	public void setP_oper_pos(String p_oper_pos) {
		this.p_oper_pos = p_oper_pos;
	}
	
	public Customer getP_customer() {
		return p_customer;
	}

	public void setP_customer(Customer p_customer) {
		this.p_customer = p_customer;
	}

	public Contract getP_contract() {
		return p_contract;
	}

	public void setP_contract(Contract p_contract) {
		this.p_contract = p_contract;
	}
	
	public List<ServPosListTable> getServPosListTable() {
		return servPosListTable;
	}

	public void setServPosListTable(List<ServPosListTable> servPosListTable) {
		this.servPosListTable = servPosListTable;
	}


	public ServiceTable getP_service() {
		return p_service;
	}

	public void setP_service(ServiceTable p_service) {
		this.p_service = p_service;
	}

	public String getP_main_currency() {
		return p_main_currency;
	}

	public void setP_main_currency(String p_main_currency) {
		this.p_main_currency = p_main_currency;
	}

	public String getP_currency() {
		return p_currency;
	}

	public void setP_currency(String p_currency) {
		this.p_currency = p_currency;
	}

	public double getP_currate() {
		return p_currate;
	}

	public void setP_currate(double p_currate) {
		this.p_currate = p_currate;
	}

	public void setP_mcRatetext(String p_mcRatetext) {
		this.p_mcRatetext = p_mcRatetext;
	}

	public void clearService() {
		p_service = new ServiceTable();
		p_customer = new Customer();
		p_master = new Staff();
		p_oper = new Staff();
		p_contract = new Contract();
		p_contr_output = "";
		p_tovar_name = "";

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	// **********************************CONTRACT_SEARCH_FORM**********************************************

	public ContractDetails selectedContr;
	public ContractDetails getSelectedContr() {
		return selectedContr;
	}

	public void setSelectedContr(ContractDetails selectedContr) {
		this.selectedContr = selectedContr;
	}

	public void clearContractField() {
		selectedContr = new ContractDetails();
		p_contract = new Contract();
		p_service.setContract_info(" - ");
		p_service.setContract_id(null);
		p_tovar_name = "";
		p_service.setTovar_id(null);
		p_service.setTovar_sn(null);
		p_customer = new Customer();
		p_service.setAddr(null);
		p_service.setTel(null);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contract");
		reqCtx.update("form:tovar");
		reqCtx.update("form:tovar_sn");
		reqCtx.update("form:cusLastName");
		reqCtx.update("form:cusAddr");
		reqCtx.update("form:cusTel");
	}

	// *************************** Customer ********************************
	
	private Customer selectedCustomer = new Customer();
	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}
	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	// ***********************************************************************

	public void loadCategories() {
		st_l = new ArrayList<Service03.ServType_Loc>();
		ServType_Loc st = new ServType_Loc("Сервисное обслуживание", 3);
		st_l.add(st);
		st = new ServType_Loc("Замена фильтров", 1);
		st_l.add(st);
		st = new ServType_Loc("Установка", 2);
		st_l.add(st);
		
		Comparator<ServType_Loc> c = new Comparator<Service03.ServType_Loc>() {
			@Override
			public int compare(ServType_Loc o1, ServType_Loc o2) {
				// TODO Auto-generated method stub
				int res = -1;
				if (o1.getV() > o2.getV()) res = 1;
				return res;
			}
		}; 
		st_l.sort(c);
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:serv_type");
	}

	public List<Service03.ServType_Loc> st_l = new ArrayList<Service03.ServType_Loc>();
	public List<Service03.ServType_Loc> getSt_l() {
		return st_l;
	}

	public class ServType_Loc {
		ServType_Loc(String s, int v) {
			this.n = s;
			this.v = v;
		}

		public String n;
		public int v;

		public String getN() {
			return n;
		}

		public void setN(String n) {
			this.n = n;
		}

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}
	}

	public boolean disPayBtn;
	public boolean isDisPayBtn() {
		return disPayBtn;
	}
	public void setDisPayBtn(boolean disPayBtn) {
		this.disPayBtn = disPayBtn;
	}
	
	public void enableServPacket() {
		disServPacket = false;
		System.out.println("Enabling Service Packets!");
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public void disableServPacket() {
		disServPacket = true;
		System.out.println("Disabling Service Packets!");
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}
	
	public boolean disServPacket;
	public boolean isDisServPacket() {
		return disServPacket;
	}
	public void setDisServPacket(boolean disServPacket) {
		this.disServPacket = disServPacket;
	}
	
	// ***************************************************************************
	 
	public List<ServConMatnrWarOutput> spWarL;
	public List<ServConMatnrWarOutput> getSpWarL() {
		return spWarL;
	}
	public void setSpWarL(List<ServConMatnrWarOutput> spWarL) {
		this.spWarL = spWarL;
	}
	private ContractType ct;
	
	public void loadSpList() {
		ServPacketDao spDao = (ServPacketDao) appContext.getContext().getBean(
				"servPacketDao");
		
		if (ct != null && ct.getMatnr() != null) {

			Branch p_branch = p_branchF4Bean.getL_branch_map().get(p_service.getBranch_id()); 
			spList = spDao.findAllByTovarID(
					p_service.getBukrs(),
					p_countryF4Bean.getL_country_map()
							.get(p_branch.getCountry_id()).getCountry_id(),
					ct.getMatnr());
			System.out.println("Found SP for Matnr " + ct.getMatnr()
					+ " total: " + spList.size());
		} else
			System.out
					.println("Couldn't load SP List since ContractType is null!");
	}
	
	public void loadSpWarDetails(Long servId) {
		if (servId != null && servId > 0) {
			System.out.println("Service Id: " + servId);
			ServConMatnrWarDao spDao = (ServConMatnrWarDao) appContext.getContext()
					.getBean("servContrMatnrWar");
			List<ServConMatnrWar> spWars = spDao.findAllByServId(servId);
			
			int i = 0;
			spWarL = new ArrayList<ServConMatnrWarOutput>();
			System.out.println("Warranties given: " + spWars.size());
			for (ServConMatnrWar spWar : spWars) {
					i++;
					ServConMatnrWarOutput spWarO = new ServConMatnrWarOutput(i);
					spWarO.setScMW(spWar);
					if (spWar.getMatnr_id() != null && spWar.getMatnr_id() > 0)
						spWarO.setMatnr(p_matnrF4Bean.getL_matnr_map().get(spWar.getMatnr_id()));
					spWarL.add(spWarO);
				}
			
		}
	}
	
	public ServPacket spSelected;
	public Long selectedSPId;
	public List<ServPacket> spList;
	
	public List<ServPacket> getSpList() {
		return spList;
	}
	public void setSpList(List<ServPacket> spList) {
		this.spList = spList;
	}
	public ServPacket getSpSelected() {
		return spSelected;
	}

	public void setSpSelected(ServPacket spSelected) {
		this.spSelected = spSelected;
	}

	public Long getSelectedSPId() {
		return selectedSPId;
	}

	public void setSelectedSPId(Long selectedSPId) {
		this.selectedSPId = selectedSPId;
	}
	
	
}
