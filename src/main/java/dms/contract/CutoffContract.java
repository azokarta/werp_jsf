package dms.contract;

import f4.AddrTypeF4;
import f4.BranchF4;
import f4.BukrsF4;
import f4.CityF4;
import f4.CityregF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import f4.PaymentTemplateF4;
import f4.PriceListF4;
import f4.StateF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.MessageProvider;
import general.PermissionController;
import general.Validation;
import general.dao.AddressDao;
import general.dao.BonusDao;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.MatnrListDao;
import general.dao.StaffDao;
import general.dao.SubCompanyDao;
import general.dao.UserRoleDao;
import general.dao.WerksBranchDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.services.AddressService;
import general.services.ContractService;
import general.services.CustomerService;
import general.services.PromotionService;
import general.services.StaffService;
import general.tables.Address;
import general.tables.AddressType;
import general.tables.Bonus;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.City;
import general.tables.Cityreg;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Currency;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.PaymentSchedule;
import general.tables.PaymentTemplate;
import general.tables.PriceList;
import general.tables.Promotion;
import general.tables.Role_action;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.State;
import general.tables.SubCompany;
import general.tables.UserRole;
import general.tables.Werks;
import general.validators.ContractValidatorImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;
import datamodels.ContractModel;
import dms.promotion.PromoTable;

@ManagedBean(name = "cutoffconBean", eager = true)
@ViewScoped
public class CutoffContract implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6951756462355187900L;
	private final static String transaction_code = "CUTOFFCON";
	private final static Long transaction_id = (long) 458;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

	private final static int MAX_NUMBER_OF_MONTHS = 12;

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
		out = 0;
		if (sec_currency != null && sec_currency.length() > 0) {
			System.out.println("INTERNAL REATE RERQUEST: " + "2" + sec_currency
					+ p_contract.getBukrs());

			Double out1 = getExrDao().getLastCurrencyRateInternal(
					p_contract.getBukrs(), main_currency, sec_currency);
			if (out1 != null)
				out = out1;
			// if (main_currency.equals(sec_currency)) return 1;
			// ExchangeRate wa_exr = p_exchangeRateF4Bean.getL_er_map_internal()
			// .get("2" + sec_currency + p_contract.getBukrs());
			// out = wa_exr.getSc_value();
			System.out.println("INTERNAL REATE: " + out);
		}
		return out;
	}

	private ExchangeRateDao getExrDao() {
		return (ExchangeRateDao) appContext.getContext().getBean(
				"exchangeRateDao");
	}

	// *********************************************************************
	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}

		psDetL = new ArrayList<PaymentScheduleDetails>();

		try {
			newCustomer = new Customer();
			disable_save_button();
			dis_field1 = true;
			dis_field2 = false;

			PermissionController.canWrite(userData, this.transaction_id);

			p_contract.setBukrs(userData.getBukrs());
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
			}

			p_contract.setContract_status_id((long) 1);
			p_contract.setLast_state(1);

			statusChangeRole = true;
			if (userData.isSys_admin()) {
				System.out.println("User is admin!");
				statusChangeRole = false;
			} else {
				System.out.println("User is not admin!");
				for (Role_action ra : userData.getUserRoleActions()) {
					if (ra.getTransaction_id().longValue() == this.transaction_id
							&& (ra.getAction_id() == 2 || ra.getAction_id() == 3)) {
						statusChangeRole = false;
						break;
					}
				}
			}

			loadBranch();

			p_currency = getCurrencyName(userData.getBranch_id());
			p_currate = 0;

			p_contract.setMarkedType(Contract.MT_CUT_OFF_PRICE);

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("breadcrumb");
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
			toMainPage();
		}

	}

	public void toMainPage() {
		GeneralUtil.doRedirect("/general/mainPage.xhtml");
	}

	public void toDmsc03(Long a_value) {
		GeneralUtil.doRedirect("/dms/contract/dmsc03.xhtml?contract_number="
				+ a_value);
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

	public void loadBranch() {
		branch_list = new ArrayList<Branch>();
		staffBranch_list = new ArrayList<Branch>();
		contractType_list = new ArrayList<ContractType>();
		p_contract.setBranch_id(null);
		p_contract.setContract_type_id(null);
		p_contract.setPrice_list_id(null);
		p_branchF4Bean.init();

		loadLEList();

		Long branchId = userData.getBranch_id();
		Long mainBranch = Branch.AURA_MAIN_BRANCH_ID;
		if (p_contract.getBukrs().equals("2000"))
			mainBranch = Branch.GREEN_LIGHT_MAIN_BRANCH_ID;
		if (userData.isMain() || userData.isSys_admin())
			branchId = mainBranch;

		// System.out.println("In BranchID: " + branchId);
		branch_list = loadBranchListByUserBranch(p_contract.getBukrs(),
				branchId, BusinessArea.AREA_ALL_EXCEPT_SERVICE);
		staffBranch_list = loadBranchListByUserBranch(p_contract.getBukrs(),
				mainBranch, -1);

		serv_branch_list = loadBranchList(p_contract.getBukrs(),
				BusinessArea.AREA_SERVICE);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		reqCtx.update("form:servBranch");
		reqCtx.update("form:contract_type_id");
		reqCtx.update("form:price_list_id");
		clearPaymentElements();
		clearMatnrListAll();
	}

	public List<Branch> loadBranchListByUserBranch(String a_bukr,
			Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));
		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		Branch inBranch = brDao.find(branchId);

		List<Branch> brL = new ArrayList<Branch>();
		if (inBranch.getType() != Branch.TYPE_BRANCH
				|| inBranch.getBusiness_area_id() == 0) {
			bf.addFilter(new BranchParentFilter(inBranch.getBranch_id()));
			brL = bf.filterBranch(p_branchF4Bean.getBranch_list());
		} else
			bf.addFilter(new BranchParentFilter(inBranch.getParent_branch_id()));

		if (ba >= 0)
			bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));

		output = bf.filterBranch(p_branchF4Bean.getBranch_list());

		for (Branch br : brL) {
			if ((br.getType() != Branch.TYPE_BRANCH)
					|| (br.getBusiness_area_id() == 0))
				output.addAll(loadBranchListByUserBranch(a_bukr,
						br.getBranch_id(), ba));
		}

		return output;
	}

	public List<Branch> loadBranchList(String a_bukr, int ba) {
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(p_contract.getBukrs()));
		bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
		return bf.filterBranch(p_branchF4Bean.getBranch_list());
	}

	public void assignBranchObject() {
		p_branch = p_branchF4Bean.getL_branch_map().get(
				p_contract.getBranch_id());
		p_contract.setFinBranchId(p_contract.getBranch_id());
		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		p_serv_br = brDao.findServBranchByBranch(p_contract.getBranch_id());
		if (p_serv_br != null && p_serv_br.getBranch_id() != null
				&& p_serv_br.getBranch_id() > 0) {
			p_contract.setServ_branch_id(p_serv_br.getBranch_id());
			System.out.println("ServBranch: " + p_serv_br.getText45());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:servBranch");
		reqCtx.update("form:finBranch");
	}

	// ****************************************************************************

	public void loadContractType() {
		assignBranchObject();
		clearMatnrListAll();
		System.out.println("Contract BranchID: " + p_contract.getBranch_id());
		loadContractTypeByBranchID(p_contract.getBranch_id());
		p_countryF4Bean.init();
		p_country = p_countryF4Bean.getL_country_map().get(
				p_branch.getCountry_id());
	}

	public void loadContractTypeByBranchID(Long wa_brId) {
		contractType_list = new ArrayList<ContractType>();
		p_contract.setContract_type_id(null);
		Branch wa_branch = p_branchF4Bean.getL_branch_map().get(wa_brId);
		p_contractTypeF4Bean.init();
		for (ContractType wa_contractType : p_contractTypeF4Bean
				.getContractType_list()) {
			if (wa_contractType.getTovar_category() == wa_branch
					.getTovarCategory()
					&& wa_contractType.getBukrs().equals(wa_branch.getBukrs())
					&& (wa_contractType.getCountryId() == null || wa_contractType
							.getCountryId() == wa_branch.getCountry_id())) {
				contractType_list.add(wa_contractType);
			}
		}

		// *****************_Loading_Currency_and
		if (p_contract.getBranch_id() != null) {
			p_currency = getCurrencyName(p_contract.getBranch_id());
		}
		p_currate = getCurrencyRate("USD", p_currency);

		// ********************************
		p_branch_id = p_contract.getBranch_id();
		loadDealers();
		p_country_id = null;
		p_state_id = null;

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:country_id");
		reqCtx.update("form:state_id");
		reqCtx.update("form:currencyName");
		reqCtx.update("form:currencyName2");
		reqCtx.update("form:exchageRate");
		reqCtx.update("form:contract_type_id");
		reqCtx.update("form:branchref");
		clearPaymentElements();

	}

	// ******************************* CUSTOMER *****************************

	public void to_search_customer() {
		try {
			CustomerService personService = (CustomerService) appContext
					.getContext().getBean("customerService");
			p_customer_list = personService.dynamicSearch(searchCustomer);

			if (p_customer_list.size() == 0) {
				p_customer_list = new ArrayList<Customer>();
				GeneralUtil.addInfoMessage("Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CustomerListForm");

		} catch (DAOException ex) {
			p_customer_list = new ArrayList<Customer>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CustomerListForm");
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void assignFoundCustomer() {
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (selectedCustomer != null && selectedCustomer.getId() != null) {
			p_contract.setCustomer_id(selectedCustomer.getId());
			if (selectedCustomer.getFiz_yur() == 1)
				p_contract.setWarranty(12);
			else if (selectedCustomer.getFiz_yur() == 2)
				p_contract.setWarranty(36);

			p_fio = selectedCustomer.getFullFIO();

			if (!Validation.isEmptyString(selectedCustomer.getEmail()))
				customer_email = selectedCustomer.getEmail();
		} else {
			p_contract.setCustomer_id(null);
			p_fio = "";
		}
		loadCustomerAddrList();
		for (Address wa_addr : addrList) {
			if (wa_addr.getAddrType() == 1) {
				addrHome = wa_addr;
				p_contract.setAddrHomeId(wa_addr.getAddr_id());
				addrService = wa_addr;
				p_contract.setAddrServiceId(wa_addr.getAddr_id());
			} else if (wa_addr.getAddrType() == 2) {
				addrWork = wa_addr;
				p_contract.setAddrWorkId(wa_addr.getAddr_id());
			}
		}
		reqCtx.update("form");
		reqCtx.update("form");
	}

	// *********************** STUFF ***************************************

	public void to_search_staff() {
		try {
			Salary p_salary = new Salary();
			p_salary.setBukrs(p_contract.getBukrs());
			p_salary.setBranch_id(searchStaff.getBranch_id());
			p_salary.setPosition_id((long) p_search_position_id);
			if (p_contract.getContract_status_id() != 1) {
				clearPaymentElements();
				GeneralUtil
						.addInfoMessage("Employee not applicable for the chosen contract status");
				return;
			}

			StaffService staffService = (StaffService) appContext.getContext()
					.getBean("staffService");
			p_staff_list = staffService.dynamicSearchStaffSalary(searchStaff,
					p_salary);

			if (p_staff_list.size() == 0) {
				p_staff_list = new ArrayList<Staff>();
				GeneralUtil.addInfoMessage("Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");

		} catch (DAOException ex) {
			p_staff_list = new ArrayList<Staff>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void assignFoundEmployee() {

		if (selectedStaff != null && selectedStaff.getStaff_id() != null) {
			if (p_selected_position_id == 4) {
				p_contract.setDealer(selectedStaff.getStaff_id());
				p_fioDealer = selectedStaff.getFullFIO();
			} else if (p_selected_position_id == 8) {
				p_contract.setDemo_sc(selectedStaff.getStaff_id());
				p_fioDemoSec = selectedStaff.getFullFIO();
			} else if (p_selected_position_id == 9) {
				p_contract.setCollector(selectedStaff.getStaff_id());
				p_fioColl = selectedStaff.getFullFIO();
			} else if (p_selected_position_id == 11) {
				p_contract.setFitter(selectedStaff.getStaff_id());
				p_fioFitter = selectedStaff.getFullFIO();
			}
		} else {
			if (p_selected_position_id == 4) {
				p_contract.setDealer(null);
				p_fioDealer = " ";
			} else if (p_selected_position_id == 8) {
				p_contract.setDemo_sc(null);
				p_fioDemoSec = " ";
			} else if (p_selected_position_id == 9) {
				p_contract.setCollector(null);
				p_fioColl = " ";
			} else if (p_selected_position_id == 11) {
				p_contract.setFitter(null);
				p_fioFitter = " ";
			}
		}

		selectedStaff = new Staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:demo_sc");
		reqCtx.update("form:dealer");
		reqCtx.update("form:collector");
		reqCtx.update("form:fitter");
		reqCtx.update("form");
	}

	public void setSearchPositionId(int a_pos_id) {
		p_selected_position_id = a_pos_id;
		p_staff_list = new ArrayList<Staff>();
		searchStaff.setBranch_id(p_contract.getBranch_id());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:staffTable");
		reqCtx.update("form:staffBranch");
		reqCtx.update("form:se_pos");
	}

	public void statusChanged() {
		if (p_contract.getContract_status_id().equals(1L)) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contract_status_id");
			return;
		} else if (p_contract.getContract_status_id().equals(2L)) {
			p_contract.setPrice_list_id((long) 0);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contract_status_id");
			reqCtx.update("form:price_list_id");
			clearPaymentElements();
		} else {
			p_contract.setContract_status_id(1L);
			GeneralUtil.addInfoMessage("Not applicable contract status");
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contract_status_id");
	}

	private PaymentSchedule[] getPS() {
		PaymentSchedule ps1[] = new PaymentSchedule[psDetL.size() + 1];
		ps1[0] = psDetFirstPayment.getPs();
		for (int i = 1; i <= psDetL.size(); i++) {
			ps1[i] = psDetL.get(i - 1).getPs();
		}
		return ps1;
	}

	public void to_save() {

		try {
			PermissionController.canWrite(userData, this.transaction_id);

			// Calendar cal = Calendar.getInstance();
			// p_contract.setContract_date(cal.getTime());

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

				if (getContractService().createCutoffpriceContract(p_contract,
						p_branch, userData, transaction_id, transaction_code, p_currency,
						p_currate, dmbtr, p_contract.getPrice(), ct.getMatnr(),
						ps, userData.getU_language()))
					GeneralUtil.addInfoMessage("Contract successfully created "
							+ p_contract.getContract_number());

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
				reqCtx.update("PromotionListForm");

				toDmsc03(p_contract.getContract_number());
				clearDMSC();
			} else {
				throw new DAOException(
						"Contract is empty! Please enter the contract data!");
			}
		} catch (DAOException ex) {
			p_contract.setContract_id(null);
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public ContractService getContractService() {
		ContractService contractService = (ContractService) appContext
				.getContext().getBean("contractService");
		return contractService;
	}

	public void clearDMSC() {
		p_contract = new Contract();
		selectedCustomer = new Customer();
		selectedStaff = new Staff();
		p_fio = "";
		p_fioDealer = "";
		p_fioDemoSec = "";
		p_fioColl = "";
		p_fioFitter = "";
		customer_email = "";
		p_currate = 0;
		p_currency = "";
		state_list = new ArrayList<State>();
		city_list = new ArrayList<City>();
		cityreg_list = new ArrayList<Cityreg>();
		p_disabled_save_button = "true";
		psDetL.clear();
		Calendar cal = Calendar.getInstance();
		p_contract.setContract_date(cal.getTime());
		p_contract.setContract_status_id((long) 1);
	}

	public void loadStates() {
		state_list = new ArrayList<State>();
		city_list = new ArrayList<City>();
		cityreg_list = new ArrayList<Cityreg>();
		newAddr.setStateId(null);
		newAddr.setCityId(null);
		newAddr.setRegId(null);

		p_stateF4Bean.init();
		for (State wa_state : p_stateF4Bean.getState_list()) {
			if (wa_state.getCountryid().equals(newAddr.getCountryId())) {
				state_list.add(wa_state);
			}
		}

		p_country = p_countryF4Bean.getL_country_map().get(
				newAddr.getCountryId());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewAddressForm:state_id");
		reqCtx.update("NewAddressForm:city_id");
		reqCtx.update("NewAddressForm:cityreg_id");
		reqCtx.update("NewAddressForm:tel_mob1");
		reqCtx.update("NewAddressForm:tel_mob1_phonecode");
	}

	public void loadCities() {
		city_list = new ArrayList<City>();
		cityreg_list = new ArrayList<Cityreg>();
		newAddr.setCityId(null);
		newAddr.setRegId(null);

		p_cityF4Bean.init();
		for (City wa_city : p_cityF4Bean.getCity_list()) {
			if (wa_city.getStateid().equals(newAddr.getStateId())) {
				city_list.add(wa_city);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewAddressForm:city_id");
		reqCtx.update("NewAddressForm:cityreg_id");
	}

	public void loadCityregs() {
		cityreg_list = new ArrayList<Cityreg>();
		newAddr.setRegId(null);

		p_cityregF4Bean.init();
		for (Cityreg wa_cityreg : p_cityregF4Bean.getCityreg_list()) {
			if (wa_cityreg.getCity_id().equals(newAddr.getCityId())) {
				cityreg_list.add(wa_cityreg);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewAddressForm:cityreg_id");
	}

	public void loadDealers() {
		try {
			// outputTable = new ArrayList<ContractModel>();
			p_dealer_id = null;
			l_dealer = new ArrayList<Staff>();
			String dynamicWhereClause = "";
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean(
					"staffDao");
			// "+p_bukrs+"
			dynamicWhereClause = dynamicWhereClause
					+ " and sal.position_id = 4 and sal.branch_id = "
					+ p_branch_id + " and sal.bukrs = '" + p_bukrs + "'";
			l_dealer = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			// System.out.println(l_dealer.size());
			if (l_dealer.size() > 0) {
				for (Staff wa_staff : l_dealer) {
					wa_staff.setFirstname(wa_staff.getLF());
				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("ReferenceListForm:dealerref");
			reqCtx.update("ReferenceListForm:outputTable");

		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// *************************************************************************************

	public void assignPrice() {
		try {
			disable_save_button();
			if (p_contract.getPrice() > 0) {
				System.out.println("Ok");

				remain = p_contract.getPrice() - p_contract.getFirst_payment();
				if (p_contract.getPayment_schedule() > 0) {
					average = (double) ((long) remain / p_contract
							.getPayment_schedule());
					remainder = (double) (remain % p_contract.getPrice());
				} else
					average = 0;

				p_contract.setFirst_payment(p_contract.getPrice());
				System.out.println("Price is being assigned!");

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:price_list_id");
				reqCtx.update("form:prepayment");
				reqCtx.update("form:prepayment_currency");
				reqCtx.update("form:remain");
				reqCtx.update("form:remainCurr");
				reqCtx.update("form:first_payment");
				loadMonthlyPaymentsAndPrice();

			} else {
				throw new DAOException("No price selected");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void loadMonthlyPaymentsAndPrice() {
		System.out.println(" - * - Inside LoadMonthlyPaymentsAndPrice - * -");
		psDetFirstPayment = new PaymentScheduleDetails();
		psDetL.clear();

		if (p_contract.getContract_status_id() != 1) {
			p_contract.setPrice_list_id((long) 0);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:price_list_id");
			GeneralUtil
					.addInfoMessage("Prices not applicable for the chosen contract status");
			System.out.println(" - * - Price NOT applicable! - * -");
			return;
		}

		System.out.println(" - * - Inside LoadMonthlyPaymentsAndPrice - * -");

		Calendar cal = Calendar.getInstance();
		cal.setTime(p_contract.getContract_date());

		p_contract.setMtConfirmed((byte) 0);

		PaymentSchedule ps = new PaymentSchedule();
		ps.setBukrs(p_contract.getBukrs());
		ps.setIsFirstPayment(PaymentSchedule.ISFIRSTPAYMENT);
		ps.setPaid(0);
		ps.setPayment_date(p_contract.getContract_date());

		System.out.println("Initial First Payment: "
				+ p_contract.getFirst_payment());

		ps.setSum(p_contract.getFirst_payment());
		psDetFirstPayment.setPs(ps);

		int j = 0;
		for (int k = 1; (k <= p_contract.getPayment_schedule()); k++) {
			PaymentScheduleDetails psd = new PaymentScheduleDetails(j);
			PaymentSchedule wa_ps = new PaymentSchedule();
			wa_ps.setPaid(0);

			if (k == p_contract.getPayment_schedule())
				wa_ps.setSum(average + remainder);
			else
				wa_ps.setSum(average);

			wa_ps.setBukrs(p_contract.getBukrs());
			cal.add(Calendar.MONTH, 1);
			wa_ps.setPayment_date(cal.getTime());
			psd.setInfo("Monthly_payment");
			psd.setPs(wa_ps);
			psd.mon_dis = true;
			psDetL.add(psd);
			j++;
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:payment_schedule");
		reqCtx.update("form:first_payment");
		reqCtx.update("form:PaymentTable");
		reqCtx.update("form");
	}

	private boolean isAroundAverage(double d, double eps, double sig) {
		return (d <= average * (1 + eps)) && (d >= average * (1 - sig));
		// return (d >= average * (1 - eps));
	}

	public Bonus getDynamicBonus(int btype, int posId) {
		try {
			BonusDao bonDao = (BonusDao) appContext.getContext().getBean(
					"bonusDao");
			String wcl = " bukrs = '" + p_contract.getBukrs()
					+ "' and bonus_type_id = " + btype + " and position_id = "
					+ posId;
			List<Bonus> bonList = bonDao.dynamicFindBonuses(wcl);
			Bonus bon = new Bonus();
			if (bonList.size() > 0)
				bon = bonList.get(0);
			return bon;
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
			throw new DAOException(ex.getMessage());
		}
	}

	public boolean isFirstPaymentInMinMaxRange(double wa_price, int wa_mon,
			double wa_prepayment, double wa_dsubtract) {
		boolean b = false;

		double rem = wa_price - wa_prepayment;

		Bonus db = getDynamicBonus(1, 4);
		double dilerPremi = db.getCoef() * p_currate;

		UserRoleDao urlDao = (UserRoleDao) appContext.getContext().getBean(
				"userRoleDao");
		List<UserRole> urL = new ArrayList<UserRole>();
		urL = urlDao.findUserRoles(userData.getUserid());
		boolean isCoordinator = false;
		for (UserRole wa_ur : urL)
			if (wa_ur.getRoleId() == 25 || wa_ur.getRoleId() == 19
					|| wa_ur.getRoleId() == 115)
				isCoordinator = true;
		System.out.println("IS COORDINATOR: " + isCoordinator);

		if ((rem >= 0) && (rem <= wa_price)) {
			if ((wa_dsubtract <= dilerPremi) && (wa_dsubtract >= 0)) {
				if (wa_mon == 0) {
					b = true;
				} else if (isAroundAverage(rem / wa_mon, PriceList.EPSILON,
						PriceList.SIGMA) || !isCoordinator) {
					b = true;
				} else {
					double min_mp = average * (1 - PriceList.SIGMA);
					String info = "Сумма ежемесячных платежей должна быть не меньше "
							+ min_mp
							+ " "
							+ p_currency
							+ "! (средней распределенной суммы "
							+ average
							+ " "
							+ p_currency
							+ " -"
							+ (PriceList.SIGMA * 100)
							+ "%)";
					GeneralUtil.addInfoMessage(info);
				}
			} else {

				String info = "Сумма скидки от дилера должна быть в пределах: 0 и "
						+ dilerPremi + " " + p_currency;
				GeneralUtil.addInfoMessage(info);
			}
		} else {
			String info = "Сумма предоплаты должна быть в пределах минимальной суммы предоплаты "
					+ min_first_payment
					+ " "
					+ p_currency
					+ " и суммы стоимости "
					+ p_contract.getPrice()
					+ " "
					+ p_currency;
			GeneralUtil.addInfoMessage(info);
		}
		return b;
	}

	public void loadMonthlyPayments(double wa_price, int wa_mon,
			double wa_prepayment, double wa_dsubtract, byte wa_refdisc) {

		if (wa_mon > 0) {
			if (wa_mon == 1
					|| isFirstPaymentInMinMaxRange(wa_price, wa_mon,
							wa_prepayment, wa_dsubtract)) {

				PaymentScheduleDetails psd;
				double remainder = (p_contract.getPrice() - wa_prepayment)
						% wa_mon;

				Calendar cal = Calendar.getInstance();
				if (psDetL.size() > 0)
					cal.setTime(psDetL.get(0).getPs().getPayment_date());

				psDetL.clear();
				for (int i = 1; i <= wa_mon; i++) {
					psd = new PaymentScheduleDetails(i);
					psd.getPs()
							.setSum((double) ((long) ((p_contract.getPrice() - wa_prepayment) / wa_mon)));
					psd.setMon_dis(false);
					psd.getPs().setBukrs(p_contract.getBukrs());
					psd.getPs().setPaid(0);

					if (i > 1)
						cal.add(Calendar.MONTH, 1);
					psd.getPs().setPayment_date(cal.getTime());

					if (i == wa_mon)
						psd.getPs()
								.setSum((double) ((long) ((p_contract
										.getPrice() - wa_prepayment) / wa_mon))
										+ remainder);
					psDetL.add(psd);
				}

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:payment_schedule");
				reqCtx.update("form:first_payment");
				reqCtx.update("form:prepayment");
				reqCtx.update("form:PaymentTable");
			}
		}
	}

	public void clearPaymentElements() {
		p_contract.setPayment_schedule(0);
		p_contract.setFirst_payment(0);
		p_contract.setPrice(0);
		p_contract.setPrice_list_id(null);
		p_contract.setDealer(null);
		p_contract.setDemo_sc(null);
		p_contract.setCollector(null);
		p_contract.setFitter(null);

		p_fio = "";
		p_fioColl = "";
		p_fioDemoSec = "";
		p_fioDealer = "";
		p_fioFitter = "";
		// selectedPrice = new PriceTableClass();

		psDetL = new ArrayList<PaymentScheduleDetails>();

		disable_save_button();

		p_contract.setDiscount_from_ref((byte) 0);
		// clearStuffField(-2L);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");

	}

	// *********************************************************************
	// ******************** Check Payment Table ****************************
	// *********************************************************************

	public void to_spread_payment_table() {
		disable_save_button();

		if (p_contract.getPrice() > 0
				&& p_contract.getMarkedType() != Contract.MT_GIFT_FOR_FREE) {
			double fp = p_contract.getFirst_payment();

			if (p_contract.getFirst_payment() > p_contract.getPrice()) {
				GeneralUtil
						.addInfoMessage("Сумма первоначальной оплаты не может превышать сумму цены товара!");
			} else {
				loadMonthlyPayments(p_contract.getPrice(),
						p_contract.getPayment_schedule(),
						p_contract.getFirst_payment(),
						p_contract.getDealer_subtract(),
						p_contract.getDiscount_from_ref());
				p_check_text = msgProvider.getValue("dmsc.hint_distributed")
						+ " " + msgProvider.getValue("dmsc.hint_check");
				// "График оплаты распределена. Пожалуйста не забудьте проверить суммы оплат перед сохранением!";
				p_check_text_color = "noteRegular";
			}

			remain = p_contract.getPrice() - p_contract.getFirst_payment();

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:remain");
			reqCtx.update("form:remainCurr");
			reqCtx.update("form:check_text");
		} else {
			GeneralUtil.addInfoMessage("Price is not selected!");
		}

	}

	public void assign_firstPayment() {
		disable_save_button();
		p_contract.setFirst_payment(psDetFirstPayment.getPs().getSum());
	}

	public void monthController() {
		if (p_contract.getPayment_schedule() > this.MAX_NUMBER_OF_MONTHS) {
			p_contract.setPayment_schedule(this.MAX_NUMBER_OF_MONTHS);
			MessageController.getInstance().addError(
					"Number of months cannot be greater than "
							+ this.MAX_NUMBER_OF_MONTHS + ".");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:payment_schedule");
		}
		remain = p_contract.getPrice() - p_contract.getFirst_payment();
		if (p_contract.getPayment_schedule() > 0)
			average = remain / p_contract.getPayment_schedule();
		else
			average = 0;
	}

	private ContractValidatorImpl getConVal() {
		return (ContractValidatorImpl) appContext.getContext().getBean(
				"contractValidator");
	}

	public void to_check_payment_table() {
		try {
			p_contract.setMtConfirmed((byte)0);
			p_contract.setMarkedType(Contract.MT_CUT_OFF_PRICE);
			
			PaymentSchedule a_ps[] = getPS();
			if (getConVal().validateContract(p_contract, a_ps, 1,
					p_contract.getWaers(), p_contract.getWaers(),
					userData.getU_language(), true, userData)) {

				disable_save_button();

				if (p_contract.getPrice() > 0) {
					double fp = p_contract.getFirst_payment();

					if (fp <= p_contract.getFirst_payment()) {
						if (checkPaymentScheduleAndPrice(p_contract)
								&& checkSpreadMonths()) {
							p_check_text = p_check_text = msgProvider
									.getValue("dmsc.hint_correct_distribute")
									+ " "
									+ msgProvider.getValue("dmsc.hint_check");
							// "Суммы оплат распределены правильно! Пожалуйста проверьте остальные данные перед сохранением!";
							p_check_text_color = "noteOk";
							p_check_icon = "ui-icon ui-icon-check";
							enable_save_button();
						} else {
							p_check_text = p_check_text = msgProvider
									.getValue("dmsc.hint_wrong_distribute");
							// "Суммы оплат распределены не правильно! Пожалуйста проверьте еще раз суммы!";
							p_check_text_color = "noteWarn";
						}
					} else {
						p_check_text = p_check_text = msgProvider
								.getValue("dmsc.hint_min_fp")
						// "Сумма первоначальной оплаты должна составлять не меньее "
								+ fp + " " + p_currency;
						p_check_text_color = "noteWarn";
						// throw new
						// DAOException("First payment cannot be less than 30% amount ("
						// + fp+ ") of the price.");
					}

				} else {
					throw new DAOException("No price selected!");
				}

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:check_text");
				reqCtx.update("form:save_button");
				reqCtx.update("form:check_button");
			}
		} catch (Exception ex) {
			disable_save_button();
			GeneralUtil.addInfoMessage(ex.getMessage());
		}
	}

	public void disable_save_button() {
		p_disabled_save_button = "true";
		p_check_text = msgProvider.getValue("dmsc.hint_check");
		// "Пожалуйста проверьте данные перед сохранением!";
		p_check_text_color = "noteRegular";
		p_check_icon = "ui-icon ui-icon-refresh";
		p_check_payments = false;

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:check_text");
		reqCtx.update("form:save_button");
		reqCtx.update("form:check_button");
	}

	public void enable_save_button() {
		p_disabled_save_button = "false";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:save_button");
	}

	// ***********************_CHECK_PAYMENT_SCHEDULE_&_PRICE_*********************

	private boolean isInMonDiff(Date d2, Date d1, int M, int N) {
		int md = GeneralUtil.calcAgeinMonths(d2, d1);
		// System.out.println("MD: " + md + "         D1 : " + d2 +
		// "     -     D2 : " + d1);
		if (md < M || md > N) {
			GeneralUtil
					.addErrorMessage("Incorrect sequence of months' in Payment Graphic!");
			return false;
		}
		return true;
	}

	public boolean isInDateRange(Date d2, Date d1, int date_con, int date_vznos) {
		int md = GeneralUtil.calcAgeinMonths(d2, d1);
		if (md == 2) {
			if (d2.getDate() > date_vznos || d1.getDate() < date_con) {
				GeneralUtil.addErrorMessage("Пропущен месяц! Можно только с "
						+ date_con + " числа, и дата взноса до " + date_vznos
						+ " числа следующего месяца!");
				return false;
			}
		}
		return true;
	}

	public boolean checkSpreadMonths() {
		if (p_contract.getPayment_schedule() > 0) {
			if (isInMonDiff(psDetL.get(0).getPs().getPayment_date(),
					p_contract.getContract_date(), 0, 2)
					&& isInDateRange(psDetL.get(0).getPs().getPayment_date(),
							p_contract.getContract_date(), 25, 5)) {
				for (int i = 1; i < psDetL.size(); i++) {
					PaymentScheduleDetails psd1 = psDetL.get(i - 1);
					PaymentScheduleDetails psd2 = psDetL.get(i);
					if (!isInMonDiff(psd2.getPs().getPayment_date(), psd1
							.getPs().getPayment_date(), 1, 1))
						return false;
				}
			} else
				return false;
		}
		return true;
	}

	public boolean checkPaymentScheduleAndPrice(Contract a_contract) {
		boolean res = false;
		if (a_contract.getPrice() > 0) {
			if (a_contract.getPayment_schedule() <= 1
					|| isFirstPaymentInMinMaxRange(a_contract.getPrice(),
							a_contract.getPayment_schedule(),
							a_contract.getFirst_payment(),
							a_contract.getDealer_subtract())) {
				int numPaySc = 0;
				double paymentDueSum = 0;

				System.out.println("Price: " + a_contract.getPrice());
				System.out.println("First payment: "
						+ a_contract.getFirst_payment());
				System.out.println("Months: "
						+ a_contract.getPayment_schedule());

				UserRoleDao urlDao = (UserRoleDao) appContext.getContext()
						.getBean("userRoleDao");
				List<UserRole> urL = new ArrayList<UserRole>();
				urL = urlDao.findUserRoles(userData.getUserid());
				boolean isCoordinator = false;
				for (UserRole wa_ur : urL)
					if (wa_ur.getRoleId() == 25 || wa_ur.getRoleId() == 19
							|| wa_ur.getRoleId() == 115
							|| userData.isSys_admin())
						isCoordinator = true;
				System.out.println("IS COORDINATOR: " + isCoordinator);

				for (PaymentScheduleDetails psd : psDetL) {
					if (psd.getPs().getSum() < 0) {
						GeneralUtil
								.addErrorMessage("Сумма ежемесячного платежа не может быть отрицательной!");
						return false;
					}

					if (a_contract.getPayment_schedule() > 1
							&& !isAroundAverage(psd.getPs().getSum(),
									PriceList.EPSILON, PriceList.SIGMA)
							&& !isCoordinator) {
						double min_mp = average * (1 - PriceList.SIGMA);
						double max_mp = average * (1 + PriceList.EPSILON);
						String info = "Сумма ежемесячных платежей должна быть не меньше "
								+ min_mp
								+ " "
								+ p_currency
								+ "! (средней распределенной суммы "
								+ average
								+ " "
								+ p_currency
								+ " -"
								+ (PriceList.SIGMA * 100)
								+ "%) и не больше "
								+ max_mp
								+ " "
								+ p_currency
								+ "! (средней распределенной суммы "
								+ average
								+ " +" + (PriceList.EPSILON * 100) + "%)";
						GeneralUtil.addErrorMessage(info);
						return false;
					}

					if (psd.getPs().getSum() > 0) {
						numPaySc += 1;
					}
					paymentDueSum += psd.getPs().getSum();
				}

				if (numPaySc <= a_contract.getPayment_schedule()
						&& a_contract.getPrice() == a_contract
								.getFirst_payment() + paymentDueSum) {
					res = true;
				}
			}
			return res;
		} else if (a_contract.getPayment_schedule() == 0
				&& a_contract.getPrice() == 0) {
			double paymentDueSum = 0;
			for (PaymentScheduleDetails psd : psDetL) {
				paymentDueSum += psd.getPs().getSum();
			}

			if ((a_contract.getPrice() == a_contract.getFirst_payment()
					+ paymentDueSum)
					&& (a_contract.getContract_status_id() == 2)) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public void save_customer() {
		try {
			PermissionController.canWrite(userData, transaction_id);
			newCustomer.setCreated_by(userData.getUserid());
			newCustomer.setUpdated_by(userData.getUserid());

			CustomerService cService = (CustomerService) appContext
					.getContext().getBean("customerService");
			cService.createCustomer(newCustomer);
			newCustomer = new Customer();
			GeneralUtil.addInfoMessage("New customer is saved! Success!");
			RequestContext.getCurrentInstance().execute(
					"PF('newCustomerWidget').hide();");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void change_fizyur() {
		System.out.println(newCustomer.getFiz_yur());
		if (newCustomer.getFiz_yur() == 2) {
			dis_field1 = true;
			dis_field2 = false;
		} else {
			dis_field1 = false;
			dis_field2 = true;
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewCustomerForm");
	}

	// *********************************************************************************
	public void clearStuffField(Long a_pos) {
		// System.out.println("Field position to clear: " + a_pos);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (a_pos == 11) {
			// System.out.println("Fitter is being cleaned.");
			p_contract.setFitter(null);
			p_fioFitter = "";
			reqCtx.update("form:fitter");
		} else if (a_pos == 9) {
			// System.out.println("Collector is being cleaned.");
			p_contract.setCollector(null);
			p_fioColl = "";
			reqCtx.update("form:collector");
		} else if (a_pos == -1) {
			// System.out.println("Customer is being cleaned.");
			p_contract.setCustomer_id(null);
			p_fio = "";
			reqCtx.update("form:fio");
		}
		reqCtx.update("form");
	}

	public void refreshMatnrListDlg() {
		mlDetail = new ArrayList<MatnrList>();
		if (!Validation.isEmptyString(mlSearch.getBarcode())) {
			for (MatnrList m : matnrListDetail) {
				if (!Validation.isEmptyString(m.getBarcode())) {
					if (m.getBarcode()
							.toLowerCase()
							.matches(mlSearch.getBarcode().toLowerCase() + ".*"))
						mlDetail.add(m);
				}
			}
		} else {
			mlDetail.addAll(matnrListDetail);
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("MatnrListForm:matnrListInWerksDlgTable");
	}

	public void assignSelectedMatnrList() {
		if (selectedML != null) {
			p_contract.setMatnr_list_id(selectedML.getMatnr_list_id());
			p_contract.setTovar_serial(selectedML.getBarcode());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tovarSerial");
		}
	}

	public void loadWerksMatnrList() {
		clearMatnrListAll();
		matnrListDetail = new ArrayList<MatnrList>();

		ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext()
				.getBean("contractTypeDao");
		ct = ctDao.find(p_contract.getContract_type_id());

		Matnr matnr = p_matnrF4Bean.getL_matnr_map().get(ct.getMatnr());
		WerksBranchDao wbDao = (WerksBranchDao) appContext.getContext()
				.getBean("werksBranchDao");
		Branch br = p_branchF4Bean.getL_branch_map().get(
				p_contract.getBranch_id());
		List<Werks> wl = wbDao.findAllWerksByBranch(br.getParent_branch_id());

		MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		List<MatnrList> ml = new ArrayList<MatnrList>();
		System.out.println("Werks found: " + wl.size());
		for (Werks w : wl) {
			System.out.println(w.getText45());
			String cond = String
					.format(" bukrs = '%s' AND status IN('%s','%s') AND matnr = %d and werks = %d",
							p_contract.getBukrs(),
							MatnrList.STATUS_ACCOUNTABILITY,
							MatnrList.STATUS_RECEIVED, ct.getMatnr(),
							w.getWerks());

			ml = mlDao.findAllWithStaff(cond);
			if (ml.size() > 0)
				matnrListDetail.addAll(ml);
		}

		for (MatnrList ml2 : matnrListDetail) {
			ml2.setMatnrObject(matnr);
		}
		mlSearch = new MatnrList();
		refreshMatnrListDlg();
	}

	public void clearMatnrList() {
		p_contract.setMatnr_list_id(null);
		p_contract.setTovar_serial(null);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tovarSerial");
	}

	public void clearMatnrListAll() {
		mlSearch = new MatnrList();
		matnrListDetail = new ArrayList<MatnrList>();
		mlDetail = new ArrayList<MatnrList>();
		selectedML = new MatnrList();
		clearMatnrList();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("MatnrListForm");
	}

	public void createNewAddress() {
		try {
			AddressService addrService = (AddressService) appContext
					.getContext().getBean("addressService");

			newAddr.setTelMob1(p_country.getPhonecode() + " "
					+ newAddr.getTelMob1());

			if (addrService.createAddress(newAddr, userData.getUserid(),
					this.transaction_code)) {
				GeneralUtil.addInfoMessage("New Address successfully saved!");
				GeneralUtil.hideDialog("NewAddressDlg");
				setCustomerAddressList();
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void prepareNewAddress() {
		if (!Validation.isEmptyLong(p_contract.getCustomer_id())) {
			GeneralUtil.showDialog("NewAddressDlg");
			newAddr = new Address();
			newAddr.setCustomerId(p_contract.getCustomer_id());
			if (p_branch.getState_id() != null && p_branch.getState_id() > 0) {
				for (State wa_state : p_stateF4Bean.getState_list()) {
					if (wa_state.getIdstate().equals(p_branch.getState_id())) {
						newAddr.setCountryId(wa_state.getCountryid());
						// System.out.println("Country : " +
						// newAddr.getCountryId());
						break;
					}
				}
				loadStates();
				newAddr.setStateId(p_branch.getState_id());
				loadCities();
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("NewAddressForm");
		} else {
			GeneralUtil.addInfoMessage("Please select Customer first!");
		}
	}

	public void loadCustomerAddrList() {
		addrList = new ArrayList<Address>();
		AddressDao addrDao = (AddressDao) appContext.getContext().getBean(
				"addressDao");
		addrList = addrDao.findAllByCustomerId(p_contract.getCustomer_id());
	}

	public void setCustomerAddressList() {
		loadCustomerAddrList();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("AddressListForm");
		GeneralUtil.showDialog("AddressListDlg");
	}

	public void cancelNewAddress() {
		newAddr = new Address();
	}

	public void assignSelectedAddr() {
		try {
			if (selectedAddr != null
					&& !Validation.isEmptyLong(selectedAddr.getAddr_id())) {
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				if (currentAddr == 1) {
					System.out.println("Assigning Home Address!");
					p_contract.setAddrHomeId(selectedAddr.getAddr_id());
					addrHome = (Address) selectedAddr.clone();
					System.out
							.println("Home Address: " + addrHome.getAddress());
					reqCtx.update("form:addrHome");
				} else if (currentAddr == 2) {
					p_contract.setAddrWorkId(selectedAddr.getAddr_id());
					addrWork = (Address) selectedAddr.clone();
				} else if (currentAddr == 3) {
					p_contract.setAddrServiceId(selectedAddr.getAddr_id());
					addrService = (Address) selectedAddr.clone();
				}
				reqCtx.update("form");
			}
		} catch (Exception ex) {
			GeneralUtil.addInfoMessage(ex.getMessage());
		}
	}

	public void clearAddressField(int i) {
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		switch (i) {
		case 1: {
			p_contract.setAddrHomeId(null);
			addrHome = new Address();
			reqCtx.update("form:addrHome");
			break;
		}
		case 2: {
			p_contract.setAddrWorkId(null);
			addrWork = new Address();
			break;
		}
		case 3: {
			p_contract.setAddrServiceId(null);
			addrService = new Address();
		}
		}
		reqCtx.update("form");
	}

	// *******************************************************************

	public void prepareCustomerDlg() {
		ref = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	// *******************************************************************************************************

	// *******************************************************************************************************

	// *******************************************************************************************************

	// *******************************************************************************************************

	// *******************************************************************************************************

	public double remain;
	public double remainder;
	public double average;

	public double getRemain() {
		return remain;
	}

	public void setRemain(double remain) {
		this.remain = remain;
	}

	public Country p_country;

	public Country getP_country() {
		return p_country;
	}

	public void setP_country(Country p_country) {
		this.p_country = p_country;
	}

	private boolean ref = false;

	public boolean isRef() {
		return ref;
	}

	public void setRef(boolean ref) {
		this.ref = ref;
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

	// *********************************************************************************

	MessageProvider msgProvider = new MessageProvider();

	// *************************************************************************
	public List<MatnrList> matnrListDetail = new ArrayList<MatnrList>();

	public List<MatnrList> getMatnrListDetail() {
		return matnrListDetail;
	}

	public void setMatnrListDetail(List<MatnrList> matnrListDetail) {
		this.matnrListDetail = matnrListDetail;
	}

	public List<MatnrList> mlDetail = new ArrayList<MatnrList>();

	public List<MatnrList> getMlDetail() {
		return mlDetail;
	}

	public void setMlDetail(List<MatnrList> mlDetail) {
		this.mlDetail = mlDetail;
	}

	public MatnrList selectedML = new MatnrList();

	public MatnrList getSelectedML() {
		return selectedML;
	}

	public void setSelectedML(MatnrList selectedML) {
		this.selectedML = selectedML;
	}

	MatnrList mlSearch = new MatnrList();

	public MatnrList getMlSearch() {
		return mlSearch;
	}

	public void setMlSearch(MatnrList mlSearch) {
		this.mlSearch = mlSearch;
	}

	// *************************************************************************
	public Address newAddr = new Address();
	public List<Address> addrList = new ArrayList<Address>();
	public Address selectedAddr = new Address();
	public Address addrHome = new Address();
	public Address addrWork = new Address();
	public Address addrService = new Address();
	private int currentAddr;

	public int getCurrentAddr() {
		return currentAddr;
	}

	public void setCurrentAddr(int currentAddr) {
		this.currentAddr = currentAddr;
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

	public Address getSelectedAddr() {
		return selectedAddr;
	}

	public void setSelectedAddr(Address selectedAddr) {
		this.selectedAddr = selectedAddr;
	}

	public Address getNewAddr() {
		return newAddr;
	}

	public void setNewAddr(Address newAddr) {
		this.newAddr = newAddr;
	}

	public List<Address> getAddrList() {
		return addrList;
	}

	public void setAddrList(List<Address> addrList) {
		this.addrList = addrList;
	}

	private ContractType ct = new ContractType();

	// *********************************************************************************
	public Long p_contr_num;

	public Long getP_contr_num() {
		return p_contr_num;
	}

	public void setP_contr_num(Long p_contr_num) {
		this.p_contr_num = p_contr_num;
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
	List<Branch> staffBranch_list = new ArrayList<Branch>();

	public List<Branch> getStaffBranch_list() {
		return staffBranch_list;
	}

	public void setStaffBranch_list(List<Branch> staffBranch_list) {
		this.staffBranch_list = staffBranch_list;
	}

	public List<Branch> getBranch_list() {
		return branch_list;
	}

	List<Branch> serv_branch_list = new ArrayList<Branch>();

	public List<Branch> getServ_branch_list() {
		return serv_branch_list;
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

	// ***************************State*******************************
	@ManagedProperty(value = "#{stateF4Bean}")
	private StateF4 p_stateF4Bean;

	public StateF4 getP_stateF4Bean() {
		return p_stateF4Bean;
	}

	public void setP_stateF4Bean(StateF4 p_stateF4Bean) {
		this.p_stateF4Bean = p_stateF4Bean;
	}

	List<State> state_list = new ArrayList<State>();

	public List<State> getState_list() {
		return state_list;
	}

	// ***************************City*******************************
	@ManagedProperty(value = "#{cityF4Bean}")
	private CityF4 p_cityF4Bean;

	public CityF4 getP_cityF4Bean() {
		return p_cityF4Bean;
	}

	public void setP_cityF4Bean(CityF4 p_cityF4Bean) {
		this.p_cityF4Bean = p_cityF4Bean;
	}

	List<City> city_list = new ArrayList<City>();

	public List<City> getCity_list() {
		return city_list;
	}

	// ***************************Cityreg*******************************
	@ManagedProperty(value = "#{cityregF4Bean}")
	private CityregF4 p_cityregF4Bean;

	public CityregF4 getP_cityregF4Bean() {
		return p_cityregF4Bean;
	}

	public void setP_cityregF4Bean(CityregF4 p_cityregF4Bean) {
		this.p_cityregF4Bean = p_cityregF4Bean;
	}

	List<Cityreg> cityreg_list = new ArrayList<Cityreg>();

	public List<Cityreg> getCityreg_list() {
		return cityreg_list;
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

	// ***************************AddrTypeF4*****************************
	@ManagedProperty(value = "#{addrTypeF4Bean}")
	private AddrTypeF4 p_addrTypeF4Bean;
	List<AddressType> addrType_list = new ArrayList<AddressType>();

	public AddrTypeF4 getP_addrTypeF4Bean() {
		return p_addrTypeF4Bean;
	}

	public void setP_addrTypeF4Bean(AddrTypeF4 p_addrTypeF4Bean) {
		this.p_addrTypeF4Bean = p_addrTypeF4Bean;
	}

	public List<AddressType> getAddrType_list() {
		return p_addrTypeF4Bean.getAddrType_list();
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

	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ***************************Contract**********************************
	private Contract p_contract = new Contract();

	public Contract getP_contract() {
		return p_contract;
	}

	public void setP_contract(Contract p_contract) {
		this.p_contract = p_contract;
	}

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

	public String p_fioFitter;

	public String getP_fioFitter() {
		return p_fioFitter;
	}

	public void setP_fioFitter(String p_fioFitter) {
		this.p_fioFitter = p_fioFitter;
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

	// *********************************************************************
	private Long p_country_id;

	public Long getP_country_id() {
		return p_country_id;
	}

	public void setP_country_id(Long p_country_id) {
		this.p_country_id = p_country_id;
	}

	private Long p_state_id;

	public Long getP_state_id() {
		return p_state_id;
	}

	public void setP_state_id(Long p_state_id) {
		this.p_state_id = p_state_id;
	}

	private Long p_city_id;

	public Long getP_city_id() {
		return p_city_id;
	}

	public void setP_city_id(Long p_city_id) {
		this.p_city_id = p_city_id;
	}

	private Long p_cityreg_id;

	public Long getP_cityreg_id() {
		return p_cityreg_id;
	}

	public void setP_cityreg_id(Long p_cityreg_id) {
		this.p_cityreg_id = p_cityreg_id;
	}

	public String p_str_prep;
	public String p_str_name;

	public String getP_str_name() {
		return p_str_name;
	}

	public void setP_str_name(String p_str_name) {
		this.p_str_name = p_str_name;
	}

	public String getP_str_prep() {
		return p_str_prep;
	}

	public void setP_str_prep(String p_str_prep) {
		this.p_str_prep = p_str_prep;
	}

	private int p_search_position_id;
	private int p_selected_position_id;

	public int getP_selected_position_id() {
		return p_selected_position_id;
	}

	public void setP_selected_position_id(int p_selected_position_id) {
		this.p_selected_position_id = p_selected_position_id;
	}

	public int getP_search_position_id() {
		return p_search_position_id;
	}

	public void setP_search_position_id(int p_search_position_id) {
		this.p_search_position_id = p_search_position_id;
	}

	public String customer_email;

	public String getCustomer_email() {
		return customer_email;
	}

	public void setCustomer_email(String customer_email) {
		this.customer_email = customer_email;
	}

	public Branch p_serv_br;

	public Branch getP_serv_br() {
		return p_serv_br;
	}

	public void setP_serv_br(Branch p_serv_br) {
		this.p_serv_br = p_serv_br;
	}

	public Branch p_branch;

	public Branch getP_branch() {
		return p_branch;
	}

	public void setP_branch(Branch p_branch) {
		this.p_branch = p_branch;
	}

	// ***************************************************************************

	List<SubCompany> sc_list = new ArrayList<SubCompany>();

	public List<SubCompany> getSc_list() {
		return sc_list;
	}

	public void setSc_list(List<SubCompany> sc_list) {
		this.sc_list = sc_list;
	}

	// *********************************************************************
	private boolean statusChangeRole;

	public boolean isStatusChangeRole() {
		return statusChangeRole;
	}

	public void setStatusChangeRole(boolean statusChangeRole) {
		this.statusChangeRole = statusChangeRole;
	}

	// **************************Employee*******************************
	private List<Staff> l_dealer = new ArrayList<Staff>();

	public List<Staff> getL_dealer() {
		return l_dealer;
	}

	public void setL_dealer(List<Staff> l_dealer) {
		this.l_dealer = l_dealer;
	}

	private Long p_dealer_id;

	public Long getP_dealer_id() {
		return p_dealer_id;
	}

	public void setP_dealer_id(Long p_dealer_id) {
		this.p_dealer_id = p_dealer_id;
	}

	// **********************************************************************
	private String p_bukrs;

	public String getP_bukrs() {
		return p_bukrs;
	}

	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	private Long p_branch_id;

	public Long getP_branch_id() {
		return p_branch_id;
	}

	public void setP_branch_id(Long p_branch_id) {
		this.p_branch_id = p_branch_id;
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

	// ******************************************************************

	public PaymentScheduleDetails psDetFirstPayment = new PaymentScheduleDetails(
			0);

	public PaymentScheduleDetails getPsDetFirstPayment() {
		return psDetFirstPayment;
	}

	public void setPsDetFirstPayment(PaymentScheduleDetails psDetFirstPayment) {
		this.psDetFirstPayment = psDetFirstPayment;
	}

	public List<PaymentScheduleDetails> psDetL = new ArrayList<PaymentScheduleDetails>();

	public List<PaymentScheduleDetails> getPsDetL() {
		return psDetL;
	}

	public void setPsDetL(List<PaymentScheduleDetails> psDetL) {
		this.psDetL = psDetL;
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

	public String p_check_text;
	public boolean p_check_payments;
	public String p_check_icon; // ui-icon ui-icon-check
	public String p_check_text_color;
	public String p_disabled_save_button;
	public double min_first_payment;

	public String getP_disabled_save_button() {
		return p_disabled_save_button;
	}

	public void setP_disabled_save_button(String p_disabled_save_button) {
		this.p_disabled_save_button = p_disabled_save_button;
	}

	public String getP_check_text_color() {
		return p_check_text_color;
	}

	public void setP_check_text_color(String p_check_text_color) {
		this.p_check_text_color = p_check_text_color;
	}

	public String getP_check_text() {
		return p_check_text;
	}

	public void setP_check_text(String p_check_text) {
		this.p_check_text = p_check_text;
	}

	public boolean isP_check_payments() {
		return p_check_payments;
	}

	public void setP_check_payments(boolean p_check_payments) {
		this.p_check_payments = p_check_payments;
	}

	public String getP_check_icon() {
		return p_check_icon;
	}

	public void setP_check_icon(String p_check_icon) {
		this.p_check_icon = p_check_icon;
	}

	Customer newCustomer = new Customer();

	public Customer getNewCustomer() {
		return newCustomer;
	}

	public void setNewCustomer(Customer newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean dis_field1;
	public boolean dis_field2;

	public boolean isDis_field1() {
		return dis_field1;
	}

	public void setDis_field1(boolean dis_field1) {
		this.dis_field1 = dis_field1;
	}

	public boolean isDis_field2() {
		return dis_field2;
	}

	public void setDis_field2(boolean dis_field2) {
		this.dis_field2 = dis_field2;
	}

}