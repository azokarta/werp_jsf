package dms.contract;

import f4.BranchF4;
import f4.ContractStatusF4;
import f4.CountryF4;
import f4.PaymentTemplateF4;
import f4.PriceListF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.MessageProvider;
import general.PermissionController;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.BonusDao;
import general.dao.BranchDao;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.PaymentScheduleDao;
import general.dao.PriceListDao;
import general.dao.StaffDao;
import general.dao.UserRoleDao;
import general.services.ContractService;
import general.tables.Bkpf;
import general.tables.Bonus;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractStatus;
import general.tables.ContractType;
import general.tables.PaymentSchedule;
import general.tables.PaymentTemplate;
import general.tables.PriceList;
import general.tables.Staff;
import general.tables.UserRole;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.BeanUtils;

import user.User;

@ManagedBean(name = "editPriceDMSCBean", eager = true)
@ViewScoped
public class EditPriceDmsc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 116L;
	private final static String transaction_code = "EDITPRICEDMSC";
	private final static Long transaction_id = 116L;
	
	@ManagedProperty(value = "#{countryF4Bean}")
	private CountryF4 p_countryF4Bean;

	public CountryF4 getP_countryF4Bean() {
		return p_countryF4Bean;
	}

	public void setP_countryF4Bean(CountryF4 p_countryF4Bean) {
		this.p_countryF4Bean = p_countryF4Bean;
	}
	
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;

	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}

	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	// ******************************************************************

	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}
		disable_save_button();
		try {
			PermissionController.canWrite(userData, this.transaction_id);
			if (contract_number != null) {
				contract_number_search = contract_number;
				System.out
						.println("Contract number: " + contract_number_search);
				to_search();
				contract_number = null;
			}
		} catch (DAOException ex) {
			contract_number = null;
			GeneralUtil.addMessage("Error", ex.getMessage());
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		}
	}

	public void to_update() {
		try {
			Calendar cal = Calendar.getInstance();
			p_contract.setUpdated_date(cal.getTime());
			p_contract.setUpdated_by(userData.getUserid());

			PermissionController.canWrite(userData, this.transaction_id);
			ContractService contractService = (ContractService) appContext
					.getContext().getBean("contractService");

			ps = new PaymentSchedule[psDetL.size() + 1];

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

			boolean priceChange = false;

			for (ContractHistory ch : chL) {
				switch (Integer.parseInt(ch.getOper_on().toString())) {
				case 20: {
					priceChange = true;
					double wrbtr = p_contract.getPrice();
					double dmbtr = wrbtr / p_currate;

					contractService.updateContractPrice(p_contract, p_bkpf,
							p_branch, userData,
							EditPriceDmsc.transaction_code, p_currency,
							p_currate, dmbtr, wrbtr, p_contrType.getMatnr(),
							ps, ch);
					break;
				}
				case 41: { // Dealer discount
					contractService.updateDealerDiscount(p_contract, ps, ch,
							userData, transaction_id, transaction_code);
					break;
				}
				default: break; 
				}
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
				GeneralUtil.addMessage("Success", "Contract updated");
				GeneralUtil
						.doRedirect("/dms/contract/dmsc03.xhtml?contract_number="
								+ p_contract.getContract_number());
			}

			if (paymentGraphicChange && !priceChange) {
				// p_contract.setContract_status_id(6L);
				ContractHistory wa_ch = new ContractHistory();

				wa_ch.setContract_id(p_contract.getContract_id());
				wa_ch.setOld_id(p_contract.getPrice_list_id());
				wa_ch.setNew_id(p_contract.getPrice_list_id());
				wa_ch.setOper_on(21L);
				wa_ch.setOper_type(3L); // 3 - Change
				wa_ch.setProcessed(1);
				wa_ch.setRec_date(cal.getTime());
				wa_ch.setOld_text(oldPaymentGraphic + " month(s)");
				wa_ch.setNew_text(p_contract.getPayment_schedule()
						+ " month(s).");

				String wa_info = "Payment graphic change from "
						+ oldPaymentGraphic + " to "
						+ p_contract.getPayment_schedule() + " month(s).";
				wa_ch.setInfo(wa_info);

				System.out.println("Updating payment graphic!");
				contractService.updateContractPaymentGraphic(p_contract, ps,
						wa_ch, userData.getUserid());
				System.out.println(wa_ch.getOld_text());
				System.out.println(wa_ch.getNew_text());
				System.out.println("Success! Payment graphic updated!");

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
				GeneralUtil.addMessage("Success", "Contract updated");
				GeneralUtil
						.doRedirect("/dms/contract/dmsc03.xhtml?contract_number="
								+ p_contract.getContract_number());
			} else {
				GeneralUtil
						.addMessage("Empty price!", "No new price selected!");
			}
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
		}
	}

	public void to_search() {
		try {
			disable_save_button();
			new_cd = new ContractDetails();
			chL.clear();
			p_fioDealer = "";
			p_fioCustomer = "";
			new_adding_priceCheck = true;
			p_contract = new Contract();
			p_oldContract = new Contract();

			ContractService contractService = (ContractService) appContext
					.getContext().getBean("contractService");
			p_contract = contractService
					.searchByContractNumber(contract_number_search);

			if (p_contract != null) {
				if (p_contract.getPaid() < p_contract.getPrice()) {
					p_oldContract = p_contract.clone();

					String wcl = "contract_number = "
							+ p_contract.getContract_number()
							+ " and blart = 'GC' and storno = 0";
					BkpfDao bkpfDao = (BkpfDao) appContext.getContext()
							.getBean("bkpfDao");

					ContractTypeDao ctDao = (ContractTypeDao) appContext
							.getContext().getBean("contractTypeDao");
					p_contrType = ctDao.find(p_contract.getContract_type_id());
					p_bkpf = bkpfDao.dynamicFindSingleBkpf(wcl,p_contract.getBukrs());
					p_currency = p_contract.getWaers();
					p_currate = p_contract.getRate();
					BranchDao branchDao = (BranchDao) appContext.getContext()
							.getBean("branchDao");
					p_branch = branchDao.find(p_contract.getBranch_id());

					CustomerDao cusDao = (CustomerDao) appContext.getContext()
							.getBean("customerDao");

					p_fioCustomer = cusDao.find(p_contract.getCustomer_id())
							.getFIO();
					System.out.println("Customer: " + p_fioCustomer);

					StaffDao stfDao = (StaffDao) appContext.getContext()
							.getBean("staffDao");

					Staff staff = new Staff();
					if (p_contract.getDealer() != null
							&& p_contract.getDealer() > 0) {
						staff = stfDao.find(p_contract.getDealer());
						if (staff != null && staff.getStaff_id() > 0) {
							p_fioDealer = staff.getLF();
							System.out.println("Dealer: " + p_fioDealer);
						}
					}

					loadPriceList();
					currentPrice = new PriceTableClass();
					if (p_contract != null
							&& p_contract.getContract_id() != null
							&& !Validation.isEmptyLong(p_contract
									.getPrice_list_id())) {
						for (PriceTableClass ptc : priceTableOld) {
							if (ptc.getpListId().equals(
									p_contract.getPrice_list_id())) {
								BeanUtils.copyProperties(ptc, currentPrice);
								// currentPrice = ptc;
								System.out
										.println("Default Price is selected!");
								break;
							}
						}
					}
					remain = currentPrice.getPriceNative()
							- currentPrice.getFirstPayment();
					min_first_payment = currentPrice.getFirstPayment();
					if (currentPrice.getMonth() != null
							&& currentPrice.getMonth() > 0)
						average = remain / currentPrice.getMonth();
					else
						average = 0;

					oldPaymentGraphic = p_contract.getPayment_schedule();

					BeanUtils.copyProperties(currentPrice, selectedPrice);

					System.out.println("Default Price is "
							+ currentPrice.getPriceNative() + " " + p_currency);
					System.out.println("Default Price ID: "
							+ currentPrice.getpListId());

					loadPriceList2();

					// ***************************_Load_payment_schedule_*********************
					PaymentScheduleDao psDao = (PaymentScheduleDao) appContext
							.getContext().getBean("paymentScheduleDao");

					List<PaymentSchedule> ps_list = new ArrayList<PaymentSchedule>();
					String whereClauseForPS = "awkey = "
							+ p_contract.getAwkey();
					ps_list = psDao.findAll(whereClauseForPS,p_contract.getBukrs());

					psDetFirstPayment.setIndex(0);
					psDetFirstPayment.setPs(ps_list.get(0));

					psDetL = new ArrayList<PaymentScheduleDetails>();

					for (int i = 1; i < ps_list.size(); i++) {
						PaymentScheduleDetails psd = new PaymentScheduleDetails(
								i);
						psd.setPs(ps_list.get(i));
						psd.setMon_dis(true);

						psDetL.add(psd);
					}

					// **********************************************************************

					Calendar cal = Calendar.getInstance();
					cal = Calendar.getInstance();

					p_contract.setNew_contract_date(cal.getTime());
					p_contract.setUpdated_date(cal.getTime());

					System.out.println("SelectedPrice: "
							+ selectedPrice.getPrice());

				} else {
					GeneralUtil
							.addInfoMessage("Договор не подлежит перефоромлению! Договор уже погашен!");
					disable_save_button();
				}
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
			ex.printStackTrace();
		} catch (CloneNotSupportedException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
			ex.printStackTrace();
		}
	}

	// *********************************************************************
	// ******************** Check Payment Table ****************************
	// *********************************************************************

	public void preparePrice() {
		selectedPrice = new PriceTableClass();
		BeanUtils.copyProperties(currentPrice, selectedPrice);
		System.out.println("Default Price is selected!");
		System.out.println("Last selected Price is "
				+ selectedPrice.getPriceNative() + " " + p_currency);
		System.out.println("Last selected Price ID: "
				+ selectedPrice.getpListId());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:priceTable");
	}

	public void to_spread_payment_table() {
		disable_save_button();

		double fp = min_first_payment;

		if (fp <= p_contract.getFirst_payment()) {
			if (p_contract.getFirst_payment() > p_contract.getPrice())
				GeneralUtil
						.addInfoMessage("Сумма первоначальной оплаты не может превышать сумму цены товара!");
			else {
				loadMonthlyPayments(p_contract.getPrice(),
						p_contract.getPayment_schedule(),
						p_contract.getFirst_payment(),
						p_contract.getDealer_subtract(),
						p_contract.getDiscount_from_ref());
				p_check_text = msgProvider.getValue("dmsc.hint_distributed")
						+ " " + msgProvider.getValue("dmsc.hint_check");
				// p_check_text =
				// "График оплаты распределена. Пожалуйста не забудьте проверить суммы оплат перед сохранением!";
				p_check_text_color = "noteRegular";
				paymentGraphicChange = true;
			}
		} else {
			p_check_text = msgProvider.getValue("dmsc.hint_min_fp")
			// "Сумма первоначальной оплаты должна составлять не меньее "
					+ fp + " " + p_currency;
			p_check_text_color = "noteWarn";
			GeneralUtil.addInfoMessage(p_check_text);
			// throw new
			// DAOException("First payment cannot be less than 30% amount ("
			// + fp+ ") of the price.");
		}
		remain = currentPrice.getPriceNative() - p_contract.getFirst_payment();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:check_text");

	}

	public void assign_firstPayment() {
		disable_save_button();
		p_contract.setFirst_payment(psDetFirstPayment.getPs().getSum());
	}

	public void monthController() {
		if (p_contract.getPayment_schedule() > selectedPrice.month) {
			p_contract.setPayment_schedule(selectedPrice.month.intValue());
			MessageController.getInstance().addError(
					"Number of months cannot be greater than "
							+ selectedPrice.month + ".");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:payment_schedule");
		}

		int monthes = GeneralUtil.calcAgeinMonths(new Date(), p_contract.getContract_date());
				
		if (p_oldContract.getPayment_schedule() > 1 && p_contract.getPayment_schedule() < monthes) {
			p_contract.setPayment_schedule(selectedPrice.month.intValue());
			MessageController.getInstance().addError(
					"Number of months cannot be less than "
							+ monthes + ".");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:payment_schedule");
		}			
		
		remain = selectedPrice.getPriceNative()
				- selectedPrice.getFirstPayment();
		if (p_contract.getPayment_schedule() > 0)
			average = remain / p_contract.getPayment_schedule();
		else
			average = 0;

		if (p_contract.getPayment_schedule() != p_oldContract
				.getPayment_schedule()) {
			ContractHistory wa_ch = new ContractHistory();
			wa_ch.setContract_id(p_contract.getContract_id());
			wa_ch.setOld_id(p_oldContract.getPrice_list_id());
			wa_ch.setNew_id(p_contract.getPrice_list_id());
			wa_ch.setOper_on(21L);
			wa_ch.setOper_type(3L); // 3 - Change
			wa_ch.setProcessed(1);
			Calendar cal = Calendar.getInstance();
			wa_ch.setRec_date(cal.getTime());
			wa_ch.setOld_text(oldPaymentGraphic + " month(s)");
			wa_ch.setNew_text(p_contract.getPayment_schedule() + " month(s).");
			String wa_info = "Payment graphic change from " + oldPaymentGraphic
					+ " to " + p_contract.getPayment_schedule() + " month(s).";
			wa_ch.setInfo(wa_info);
			chL.add(wa_ch);
		}
	}

	public void to_check_payment_table() {

		System.out.println("CHECK PAYMENT TABLE!");
//		System.out.println("Current Price: " + currentPrice);
//		System.out.println("Selected Price: " + currentPrice.getPrice());

		disable_save_button();

		if (min_first_payment <= p_contract.getFirst_payment()) {
			if (checkPaymentScheduleAndPrice(p_contract) //&& checkSpreadMonths()
					) {
				p_check_text = msgProvider.getValue("dmsc.hint_correct_distribute")
						+ " " + msgProvider.getValue("dmsc.hint_check");
				// "Суммы оплат распределены правильно! Пожалуйста проверьте остальные данные перед сохранением!";
				p_check_text_color = "noteOk";
				p_check_icon = "ui-icon ui-icon-check";
				enable_save_button();
				System.out.println("CORRECT!");
			} else {
				p_check_text = msgProvider.getValue("dmsc.hint_wrong_distribute");
				// "Суммы оплат распределены не правильно! Пожалуйста проверьте еще раз суммы!";
				p_check_text_color = "noteWarn";
				GeneralUtil.addErrorMessage(p_check_text);
			}
		} else {
			p_check_text = msgProvider.getValue("dmsc.hint_min_fp")
			// "Сумма первоначальной оплаты должна составлять не меньее "
					+ min_first_payment + " " + p_currency;
			p_check_text_color = "noteWarn";
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:check_text");
		reqCtx.update("form:save_button");
		reqCtx.update("form:check_button");
	}

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
			if (isInMonDiff(psDetL.get(0).getPs().getPayment_date(), Calendar
					.getInstance().getTime(), 0, 2)
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

	public void disable_save_button() {
		p_disabled_save_button = "true";
		p_check_text = msgProvider.getValue("dmsc.hint_check");
		// p_check_text = "Пожалуйста проверьте данные перед сохранением!";
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
					|| wa_ur.getRoleId() == 115 || userData.isSys_admin())
				isCoordinator = true;
		System.out.println("IS COORDINATOR: " + isCoordinator);

		if ((rem >= 0) && (rem <= wa_price)) {
			String p_waers=p_countryF4Bean.getL_country_map().get(p_branchF4Bean.getL_branch_map().get(p_contract.getBranch_id()).getCountry_id()).getCurrency();
			if (p_waers!=null && p_waers.equals("KZT") && wa_dsubtract>35000)
			{
				String info = "Сумма скидки от дилера должна быть в пределах: 0 и 35000 " + p_currency;
				GeneralUtil.addInfoMessage(info);
			}
			else if (p_waers!=null && p_waers.equals("KGS") && wa_dsubtract>6800)
			{
				String info = "Сумма скидки от дилера должна быть в пределах: 0 и 6800 " + p_currency;
				GeneralUtil.addInfoMessage(info);
			}
			else if (p_waers!=null && p_waers.equals("UZS") && wa_dsubtract>860000)
			{
				String info = "Сумма скидки от дилера должна быть в пределах: 0 и 860000 " + p_currency;
				GeneralUtil.addInfoMessage(info);
			}
			else if (p_waers!=null && p_waers.equals("AZN") && wa_dsubtract>160)
			{
				String info = "Сумма скидки от дилера должна быть в пределах: 0 и 160 " + p_currency;
				GeneralUtil.addInfoMessage(info);
			}
			else
			{	
//			if ((wa_dsubtract <= dilerPremi) && (wa_dsubtract >= 0)) {
				if (wa_mon == 0) {
					b = true;
				} else if (isAroundAverage(rem / wa_mon, PriceList.EPSILON,
						PriceList.SIGMA) || isCoordinator) {
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
			}
//			} else {
//				String info = "Сумма скидки от дилера должна быть в пределах: 0 и "
//						+ dilerPremi + " " + p_currency;
//				GeneralUtil.addInfoMessage(info);
//			}
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

	public boolean checkPaymentScheduleAndPrice(Contract a_contract) {
		boolean res = false;
		double paid = psDetFirstPayment.getPs().getPaid();
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
								+ min_mp + " " + p_currency + "! (средней распределенной суммы "
								+ average + " " + p_currency + " -" + (PriceList.SIGMA * 100)
								+ "%) и не больше " + max_mp + " " + p_currency
								+ "! (средней распределенной суммы " 
								+ average + " +" + (PriceList.EPSILON * 100) + "%)";
						GeneralUtil.addErrorMessage(info);
						return false;
					}

					if (psd.getPs().getSum() > 0) {
						numPaySc += 1;
					}
					paymentDueSum += psd.getPs().getSum();
					paid += psd.getPs().getPaid();					
				}

				System.out.println("PAID: " + paid);
				if (numPaySc <= a_contract.getPayment_schedule()
						&& a_contract.getPrice() == a_contract
								.getFirst_payment() + paymentDueSum
								&& (p_contract.getPaid() == paid)) {
					res = true;
				}
			}
			return res;
		} else if (a_contract.getPayment_schedule() == 0
				&& a_contract.getPrice() == 0) {
			double paymentDueSum = 0;
			for (PaymentScheduleDetails psd : psDetL) {
				paymentDueSum += psd.getPs().getSum();
				paid += psd.getPs().getPaid();
			}

			if ((a_contract.getPrice() == a_contract.getFirst_payment()
					+ paymentDueSum)
					&& (a_contract.getContract_status_id() == 2)
					&& p_contract.getPaid() == paid) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public void loadMonthlyPayments(double wa_price, int wa_mon,
			double wa_prepayment, double wa_dsubtract, byte wa_refdisc) {

		if (wa_mon > 0) {
			if (p_contract.getPayment_schedule() == 1
					|| isFirstPaymentInMinMaxRange(wa_price, wa_mon,
							wa_prepayment, wa_dsubtract)) {

				PaymentScheduleDetails psd;

				double remainder = (p_contract.getPrice() - wa_prepayment)
						% wa_mon;

				Calendar cal = Calendar.getInstance();
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

				double wa_paid = p_contract.getPaid();

				if (wa_paid < psDetFirstPayment.getPs().getSum()) {
					psDetFirstPayment.getPs().setPaid(wa_paid);
				} else {
					psDetFirstPayment.getPs().setPaid(
							psDetFirstPayment.getPs().getSum());
				}

				wa_paid -= psDetFirstPayment.getPs().getSum();

				int j = psDetL.size() - 1;
				while (wa_paid > 0) {
					if (wa_paid < psDetL.get(j).getPs().getSum()) {
						psDetL.get(j).getPs().setPaid(wa_paid);
					} else {
						psDetL.get(j).getPs()
								.setPaid(psDetL.get(j).getPs().getSum());
					}
					wa_paid -= psDetL.get(j).getPs().getSum();
					j--;
				}

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:payment_schedule");
				reqCtx.update("form:first_payment");
				reqCtx.update("form:first_paid");
				reqCtx.update("form:PaymentTable");
			}
		}
	}

	// ***************************************************************************************************

	public void loadMonthlyPaymentsAndPrice() {
		psDetFirstPayment = new PaymentScheduleDetails();
		psDetL.clear();

		PriceList wa_priceList = new PriceList();

		p_contract.setPayment_schedule(0);
		p_contract.setFirst_payment(0);
		p_contract.setPrice(0);

		PriceListDao plDao = (PriceListDao) appContext.getContext().getBean(
				"priceListDao");
		wa_priceList = plDao.find(p_contract.getPrice_list_id());
		
		double rate = 1;
		if (wa_priceList.getWaers().equals("USD")) rate = p_currate;
		else rate = 1;
		
		

		double wa_paid = p_contract.getPaid();
		double wa_remain = wa_priceList.getPrice() * rate
				- p_contract.getPaid();
		int mon = 0;

		List<PaymentTemplate> pt_l = p_paymentTemplateF4Bean
				.getPaymentTemplateOf(wa_priceList.getPrice_list_id());

		Calendar cal = Calendar.getInstance();
		// cal.setTime(p_contract.getContract_date());

		int j = 0;
		for (int i = 0; i < pt_l.size(); i++) {
			for (int k = 1; (k <= pt_l.get(i).getMonth_num()); k++) {
				PaymentScheduleDetails psd = new PaymentScheduleDetails(j);
				PaymentSchedule wa_ps = new PaymentSchedule();
				wa_ps.setPaid(0);
				wa_ps.setSum(pt_l.get(i).getMonthly_payment_sum() * rate);
				wa_ps.setBukrs(p_contract.getBukrs());

				if (j > 0) {
					cal.add(Calendar.MONTH, 1);
					wa_ps.setPayment_date(cal.getTime());
				} else {
					wa_ps.setIsFirstPayment(PaymentSchedule.ISFIRSTPAYMENT);
					wa_ps.setPayment_date(p_contract.getContract_date());
				}

				psd.setInfo(pt_l.get(i).getInfo());
				psd.setPs(wa_ps);
				psd.mon_dis = true;

				if (j == 0) {
					/*
					 * if (wa_paid > psd.getPs().getSum()) {
					 * psd.getPs().setSum(wa_paid); }
					 * psd.getPs().setPaid(wa_paid);
					 */
					psDetFirstPayment = psd;
				} else {
					psDetL.add(psd);
				}

				j++;
			}
		}

		if (wa_paid < psDetFirstPayment.getPs().getSum()) {
			psDetFirstPayment.getPs().setPaid(wa_paid);
		} else {
			psDetFirstPayment.getPs().setPaid(
					psDetFirstPayment.getPs().getSum());
		}
		wa_paid -= psDetFirstPayment.getPs().getSum();

		j = psDetL.size() - 1;
		while (wa_paid > 0) {
			if (wa_paid < psDetL.get(j).getPs().getSum()) {
				psDetL.get(j).getPs().setPaid(wa_paid);
			} else {
				psDetL.get(j).getPs().setPaid(psDetL.get(j).getPs().getSum());
			}
			wa_paid -= psDetL.get(j).getPs().getSum();
			j--;
		}

		p_contract.setPayment_schedule(wa_priceList.getMonth());
		p_contract.setPrice((double) wa_priceList.getPrice() * rate);
		p_contract.setFirst_payment((double) pt_l.get(0)
				.getMonthly_payment_sum() * rate);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:payment_schedule");
		reqCtx.update("form:first_payment");
		reqCtx.update("form:PaymentTable");

	}

	// ***************************************************************************************************

	public void loadPriceList() {
		priceTableOld = new ArrayList<PriceTableClass>();
		selectedPrice = new PriceTableClass();

		ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext()
				.getBean("contractTypeDao");
		ContractType ct = ctDao.find(p_contract.getContract_type_id());

		System.out.println("CountryID: " + p_branch.getCountry_id());
		System.out.println("ContractTypeID: "
				+ p_contract.getContract_type_id() + "   " + ct.getName());
		System.out.println("PriceListID: " + p_contract.getPrice_list_id());

		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		Branch p_br = brDao.find(p_contract.getBranch_id());

		double rate = 1;
		
		List<PriceList> wal_pl = new ArrayList<PriceList>();
		wal_pl = p_priceListF4Bean
				.getPriceListByContractTypeAndDate(ct, p_branch.getCountry_id(), p_contract.getContract_date());
				
//		if (userData.getUserid().equals(1L))
//		{
//			PriceListDao plDao = (PriceListDao) appContext.getContext().getBean(
//					"priceListDao");
//			PriceList wa_priceList = plDao.find(815L);
//			if (wa_priceList!=null)
//			{
//				wal_pl.add(wa_priceList);
//			}
//		}
		
		for (PriceList wa_priceList2 : wal_pl) {
			
			if (wa_priceList2.getWaers().equals("USD")) rate = p_currate;
			else rate = 1;

			if (wa_priceList2.getMatnr().equals(ct.getMatnr())
					&& wa_priceList2.getCountry_id().equals(
							p_br.getCountry_id())
					&& wa_priceList2.getBukrs().equals(ct.getBukrs())) {

				List<PaymentTemplate> wa_pt = p_paymentTemplateF4Bean
						.getPaymentTemplateOf(wa_priceList2.getPrice_list_id());

				PriceTableClass pt = new PriceTableClass();
				pt.setpListId(wa_priceList2.getPrice_list_id());
				pt.setPrice(wa_priceList2.getPrice());
				pt.setMonth(new Long(wa_priceList2.getMonth()));
				pt.setPriceNative((double) wa_priceList2.getPrice() * rate);
				pt.setMatnrId(wa_priceList2.getMatnr());
				pt.setFirstPayment(wa_pt.get(0).getMonthly_payment_sum()
						* rate);
				double mrest = pt.priceNative - pt.firstPayment;
				pt.setMrest(mrest);
				pt.setPremDiv(wa_priceList2.getPrem_div());

				priceTableOld.add(pt);

				// System.out.println("Price: " + pt.getPrice() +
				// " NativePrice: "
				// + pt.getPriceNative() + " Month: " + pt.getMonth());
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:priceTable");

	}

	public void loadPriceList2() {
		p_contract.setPrice_list_id(null);
		priceList_list = new ArrayList<PriceList>();
		priceTable = new ArrayList<PriceTableClass>();

		Branch wa_branch = p_branch;
		ContractType wa_contractType = p_contrType;

		ContractType ct3 = wa_contractType;
		System.out.println("Country: " + wa_branch.getCountry_id()
				+ "     Bukrs: " + wa_branch.getBukrs() + "      Branch: "
				+ wa_branch.getBranch_id());
		System.out.println("ContractType: " + ct3.getContract_type_id() + " "
				+ ct3.getBukrs() + " " + ct3.getName() + " " + ct3.getMatnr()
				+ " " + ct3.getBusiness_area_id());

		priceTable = loadPriceByBranch(wa_branch, ct3);
		// if (priceTable.size() == 0)
		priceTable.addAll(loadPriceByCountry(wa_branch, ct3));

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:priceTable");
	}

	public List<PriceTableClass> loadPriceByBranch(Branch a_branch,
			ContractType a_ct) {
		List<PriceTableClass> ptl = new ArrayList<PriceTableClass>();

		p_priceListF4Bean.init();
		p_paymentTemplateF4Bean.init();

		System.out.println("A_BRANCH: " + a_branch.getText45());
		List<PriceList> plL = p_priceListF4Bean.getPriceListByContractTypeAndDate(
				a_ct, a_branch.getCountry_id(), p_contract.getContract_date());
		double rate = 1;
		
		
		PriceListDao plDao = (PriceListDao) appContext.getContext().getBean(
				"priceListDao");
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd"); 
		String tradeInSql = "";
		String bankPartnerSql = "";
		
		String qry = "select p"+
		" FROM  PriceList p"+
		" where "
		+ "p.matnr = " + a_ct.getMatnr()
		+ " AND p.branch_id = " + p_contract.getBranch_id()
		+ " and p.from_date <= '" + format1.format(p_contract.getContract_date())+"'"
		+ " AND p.active = 1";
		
		
		
		if(p_contract.getTradeIn()>0){
			tradeInSql = tradeInSql + " and p.tradeIn="+ p_contract.getTradeIn();
		}
		qry = qry + tradeInSql;
		
		if(p_contract.getBank_partner_id()>0L){
			bankPartnerSql = bankPartnerSql + " and p.bankPartnerId="+ p_contract.getBank_partner_id();
		}else bankPartnerSql = bankPartnerSql + " and p.bankPartnerId=0";
		qry = qry + bankPartnerSql;

		
		plL = plDao.findAllQuery(qry);
		
		if ((plL==null || plL.size()==0) && (userData.getUserid().equals(51L)||userData.getUserid().equals(50L)||userData.getUserid().equals(42L)||userData.getUserid().equals(1L)||userData.getUserid().equals(191L)))
		{
			if(p_contract.getTradeIn()>0){
				String qry2 = " SELECT p"
			            +" FROM PriceList p"
			            +" where p.branch_id="+a_branch.getBranch_id()
			            +" and p.bukrs='"+p_contract.getBukrs()+"'"
			                +" AND p.matnr = " + a_ct.getMatnr() + tradeInSql
			                +" order by p.from_date desc, p.month asc,p.price asc";
				plL = plDao.findAllQuery(qry2);
			}
			else
			{
				String qry2 = " SELECT p"
			            +" FROM PriceList p"
			            +" where p.branch_id="+a_branch.getBranch_id()
			            +" and p.bukrs='"+p_contract.getBukrs()+"'"
			                +" AND p.matnr = " + a_ct.getMatnr()
			                +" order by p.from_date desc, p.month asc,p.price asc";
			                
			                
//						String qry2 = "select p"
//										+" FROM  PriceList p"
//										+" where "
//										+" p.price_list_id = some ("
//												+" SELECT max(p2.price_list_id)"
//												+" FROM PriceList p2 "
//												+" where p2.from_date <= '"+format1.format(p_contract.getContract_date())+"'  and p2.branch_id="+a_branch.getBranch_id()
//														+ " and p2.month=1 and p2.bukrs='"+p_contract.getBukrs()+"'"
//														+ " AND p2.matnr = " + a_ct.getMatnr()
//												+" group by p2.country_id, p2.matnr, p2.waers, p2.month, p2.month_type, p2.branch_id )"
//												+" AND p.matnr = " + a_ct.getMatnr()
//												+" and p.branch_id="+a_branch.getBranch_id()
//												+ "and p.bukrs='"+p_contract.getBukrs()+"'";
						
						plL = plDao.findAllQuery(qry2);
						
						qry2 = " SELECT p"
					            +" FROM PriceList p"
					            +" where (p.branch_id is null or p.branch_id = 0)"
					            +" and p.bukrs='"+p_contract.getBukrs()+"'"
					                +" AND p.matnr = " + a_ct.getMatnr()
					                +" and p.country_id="+a_branch.getCountry_id()
					                +" order by p.from_date desc, p.month asc,p.price asc";
						
						plL.addAll(plDao.findAllQuery(qry2));
						
						
//						if ((plL==null || plL.size()==0) && (userData.getUserid().equals(51L)||userData.getUserid().equals(50L)||userData.getUserid().equals(42L)||userData.getUserid().equals(1L)))
//						{
//							String qry3 = "select p"
//									       +" FROM  PriceList p"
//									       +" where "
//									        +" p.price_list_id = some ("
//									      +" SELECT max(p2.price_list_id)"
//									       +" FROM PriceList p2 "
//									        +" where p2.from_date <= '"+format1.format(p_contract.getContract_date())+"' and p2.country_id="+a_branch.getCountry_id()
//									        		+ " and p2.month=1 "
//									        		+ "and p2.bukrs='"+p_contract.getBukrs()+"' AND p2.matnr =   " + a_ct.getMatnr()
//									        +" )"
//									          +" AND p.matnr =   " + a_ct.getMatnr()
//									        +" and p.country_id="+a_branch.getCountry_id()
//									        +" and p.bukrs='"+p_contract.getBukrs()+"'";
//							
//							plL = plDao.findAllQuery(qry3);
//							
//							
//						}
			}
			
			
		}
		
		
//		if (userData.getUserid().equals(1L))
//		{
//			PriceListDao plDao = (PriceListDao) appContext.getContext().getBean(
//					"priceListDao");
//			PriceList wa_priceList1 = plDao.find(815L);
//			if (wa_priceList1!=null)
//			{
//				plL.add(wa_priceList1);
//			}
//			PriceList wa_priceList2 = plDao.find(876L);
//			if (wa_priceList2!=null)
//			{
//				plL.add(wa_priceList2);
//			}
//			
//			PriceList wa_priceList3 = plDao.find(2391L);
//			if (wa_priceList3!=null)
//			{
//				plL.add(wa_priceList3);
//			}
//			
//			PriceList wa_priceList4 = plDao.find(872L);
//			if (wa_priceList4!=null)
//			{
//				plL.add(wa_priceList4);
//			}
//			
//			PriceList wa_priceList5 = plDao.find(886L);
//			if (wa_priceList5!=null)
//			{
//				plL.add(wa_priceList5);
//			}
//			
//			PriceList wa_priceList6 = plDao.find(902L);
//			if (wa_priceList6!=null)
//			{
//				plL.add(wa_priceList6);
//			}
//			
//			
//		}
		
		

		for (PriceList wa_priceList2 : plL) {

			// System.out.println("--- " + wa_priceList2.getBranch_id());
//			if (wa_priceList2.getPrice_list_id().equals(815L))
//			{
//				System.out.println("ZZZ");
//			}
//|| wa_priceList2.getPrice_list_id().equals(815L)
//			if ((wa_priceList2.getMatnr().equals(a_ct.getMatnr()) && (!Validation.isEmptyLong(wa_priceList2.getBranch_id()))) 
//					|| ((wa_priceList2.getPrice_list_id().equals(815L) || wa_priceList2.getPrice_list_id().equals(876L) || wa_priceList2.getPrice_list_id().equals(2391L)
//							 || wa_priceList2.getPrice_list_id().equals(872L)
//							 || wa_priceList2.getPrice_list_id().equals(886L)
//							 || wa_priceList2.getPrice_list_id().equals(902L)
//							) 
//							&& wa_priceList2.getCountry_id().equals(a_branch.getCountry_id()) && wa_priceList2.getMatnr().equals(a_ct.getMatnr()))) {
//				
//				
//				if ((wa_priceList2.getBranch_id()
//						.equals(a_branch.getBranch_id())
//						&& wa_priceList2.getBukrs().equals(a_ct.getBukrs())) || 
//						
//						((wa_priceList2.getPrice_list_id().equals(815L) || wa_priceList2.getPrice_list_id().equals(876L) || wa_priceList2.getPrice_list_id().equals(2391L)
//								|| wa_priceList2.getPrice_list_id().equals(872L)
//								 || wa_priceList2.getPrice_list_id().equals(886L)
//								 || wa_priceList2.getPrice_list_id().equals(902L))
//								
//								&& wa_priceList2.getCountry_id().equals(a_branch.getCountry_id()) && wa_priceList2.getMatnr().equals(a_ct.getMatnr()))) {

					// System.out.println("Inside: " +
					// wa_priceList2.getBranch_id());
					
					
					if (wa_priceList2.getWaers().equals("USD")) rate = p_currate;
					else rate = 1;

					List<PaymentTemplate> wa_pt = p_paymentTemplateF4Bean
							.getPaymentTemplateOf(wa_priceList2
									.getPrice_list_id());
					priceList_list.add(wa_priceList2);

					PriceTableClass pt = new PriceTableClass();
					pt.setpListId(wa_priceList2.getPrice_list_id());
					pt.setPrice(wa_priceList2.getPrice());
					pt.setMonth(new Long(wa_priceList2.getMonth()));
					pt.setPriceNative((double) wa_priceList2.getPrice()
							* rate);
					pt.setMatnrId(wa_priceList2.getMatnr());
					pt.setFirstPayment(wa_pt.get(0).getMonthly_payment_sum()
							* rate);
					double mrest = pt.priceNative - pt.firstPayment;
					pt.setMrest(mrest);
					pt.setPremDiv(wa_priceList2.getPrem_div());
					pt.setWaers(wa_priceList2.getWaers());
					pt.setFrom_date(wa_priceList2.getFrom_date());
					ptl.add(pt);
					
					

					System.out.println("Price: " + pt.getPrice()
							+ " NativePrice: " + pt.getPriceNative()
							+ " Month: " + pt.getMonth());

//				}
//			}
		}

		return ptl;
	}

	public List<PriceTableClass> loadPriceByCountry(Branch a_branch,
			ContractType a_ct) {
		List<PriceTableClass> ptl = new ArrayList<PriceTableClass>();
		
		double rate = 1;
		

		for (PriceList wa_priceList2 : p_priceListF4Bean
				.getPriceListByContractTypeAndDate(a_ct, a_branch.getCountry_id(), p_contract.getContract_date())) {
			// System.out.println("Price: " + wa_priceList2.getPrice() + " " +
			// wa_priceList2.getWaers() + " Month: " + wa_priceList2.getMonth()
			// + " - Matnr: " + wa_priceList2.getMatnr());
			// System.out.println(pl3.getPrice_list_id()+" "+pl3.getBukrs() +
			// " " + pl3.getMatnr() + " " + pl3.getPrice() + " " +
			// pl3.getWaers());
			
			if (wa_priceList2.getWaers().equals("USD")) rate = p_currate;
			else rate = 1;

			if (wa_priceList2.getMatnr().equals(a_ct.getMatnr())
					&& wa_priceList2.getCountry_id().equals(
							a_branch.getCountry_id())
					&& wa_priceList2.getBukrs().equals(a_ct.getBukrs())
					&& Validation.isEmptyLong(wa_priceList2.getBranch_id())) {

				boolean sameType = false;

				for (PriceList pl : priceList_list) {
					if (wa_priceList2.getMonth_type()
							.equals(pl.getMonth_type())) {
						sameType = true;
						break;
					}
				}

				if (!sameType) {
					List<PaymentTemplate> wa_pt = p_paymentTemplateF4Bean
							.getPaymentTemplateOf(wa_priceList2
									.getPrice_list_id());
					priceList_list.add(wa_priceList2);

					PriceTableClass pt = new PriceTableClass();
					pt.setpListId(wa_priceList2.getPrice_list_id());
					pt.setPrice(wa_priceList2.getPrice());
					pt.setMonth(new Long(wa_priceList2.getMonth()));
					pt.setPriceNative((double) wa_priceList2.getPrice()
							* rate);
					pt.setMatnrId(wa_priceList2.getMatnr());
					pt.setFirstPayment(wa_pt.get(0).getMonthly_payment_sum()
							* rate);
					double mrest = pt.priceNative - pt.firstPayment;
					pt.setMrest(mrest);
					pt.setPremDiv(wa_priceList2.getPrem_div());
					ptl.add(pt);

					System.out.println("Price: " + pt.getPrice()
							+ " NativePrice: " + pt.getPriceNative()
							+ " Month: " + pt.getMonth());
				}
			}
		}

		return ptl;
	}

	List<PriceList> priceList_list = new ArrayList<PriceList>();

	public List<PriceList> getPriceList_list() {
		return priceList_list;
	}

	public void assignPriceListId() {

		if ((p_contract.getContract_status_id().equals(2))
				|| (p_contract.getContract_status_id().equals(3))
				|| (p_contract.getContract_status_id().equals(4))) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:price_list_id");
			GeneralUtil.addMessage("Info",
					"Prices not applicable for the chosen contract status");
			System.out
					.println("Prices not applicable for the chosen contract status");
			System.out.println("Contract status ID: "
					+ p_contract.getContract_status_id());
			return;
		} else if (selectedPrice != null && selectedPrice.getpListId() != null
				&& selectedPrice.getpListId() > 0) {

			removeCHLif(20L);

			BeanUtils.copyProperties(selectedPrice, selectedPrice1);
			disable_save_button();
			new_adding_priceCheck = false;
			// ContractHistoryDetails wa_chd = new ContractHistoryDetails(
			// chdL.size() + 1);
			ContractHistoryDetails wa_chd = new ContractHistoryDetails(1);
			ContractHistory wa_ch = new ContractHistory();

			new_cd.setPriceList(p_priceListF4Bean.getPl_map_l().get(
					selectedPrice.getpListId()));
			new_cd.setPrice(selectedPrice.getPriceNative());

			wa_ch.setContract_id(p_contract.getContract_id());

			wa_ch.setOld_id(currentPrice.getpListId());

			wa_ch.setNew_id(selectedPrice.pListId);
			Calendar cal = Calendar.getInstance();
			wa_ch.setRec_date(cal.getTime());
			wa_ch.setOper_on(20L);
			wa_ch.setOper_type(3L); // 3 -Change

			String new_text = new_cd.getPrice() + " " + p_currency + " for "
					+ new_cd.getPriceList().getMonth() + " month(s).";
			String old_text = currentPrice.getPriceNative() + " " + p_currency
					+ " for " + oldPaymentGraphic + " month(s) ";

			wa_ch.setOld_text(old_text);
			wa_ch.setNew_text(new_text);

			String wa_info = "Price change from: " + old_text + " to: "
					+ new_text;
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(0);

			min_first_payment = selectedPrice.getFirstPayment();
			System.out.println("Min first payment: " + min_first_payment);

			p_contract.setPrice_list_id(selectedPrice.getpListId());
			p_contract.setPrice(selectedPrice.getPriceNative());
			p_contract.setNew_contract_date(cal.getTime());

			remain = selectedPrice.getPriceNative()
					- selectedPrice.getFirstPayment();
			if (selectedPrice.getMonth() > 0)
				average = remain / selectedPrice.getMonth();
			else
				average = 0;

			chL.add(wa_ch);

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			loadMonthlyPaymentsAndPrice();

		} else {
			throw new DAOException("No price selected");
		}
	}

	// ***************************************************************************************************

	public void dealerDiscController() {
		try {
			disable_save_button();
			removeCHLif(41L);
			if (p_contract.getDealer_subtract() != p_oldContract
					.getDealer_subtract()) {
				if (checkDiscountPremi()) {
					ContractHistory wa_ch = new ContractHistory();

					wa_ch.setContract_id(p_contract.getContract_id());
					wa_ch.setOld_text(p_oldContract.getDealer_subtract() + " "
							+ p_oldContract.getWaers());
					wa_ch.setNew_text(p_contract.getDealer_subtract() + " "
							+ p_contract.getWaers());

					Calendar cal = Calendar.getInstance();
					wa_ch.setRec_date(cal.getTime());
					wa_ch.setOper_on(41L);
					Long operType = (p_contract.getDealer_subtract() == 0 ? 2L
							: (p_oldContract.getDealer_subtract() == 0 ? 1L
									: 3L));
					wa_ch.setOper_type(operType); // 3 -Change

					String wa_info = "Dealer discount change from: "
							+ wa_ch.getOld_text() + " to: "
							+ wa_ch.getNew_text();
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);

					chL.add(wa_ch);
				}
			}
			double diff = p_oldContract.getDealer_subtract()
					- p_contract.getDealer_subtract();
			double p = p_oldContract.getPaid() - diff;
			p_contract.setPaid(p);
		} catch (Exception ex) {
			// System.out.println(ex.getMessage());
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public boolean checkDiscountPremi() throws Exception {
		
//		Bonus db = getDynamicBonus(1, 4);
//		double dilerPremi = db.getCoef() * p_currate;
//		if (((p_contract.getDealer_subtract() <= dilerPremi) && (p_contract
//				.getDealer_subtract() >= 0))) {
//			return true;
//		} else {
//			disable_save_button();
//			String info = "Сумма скидки от дилера должна быть в пределах: 0 и "
//					+ dilerPremi + " " + p_contract.getWaers();
//			throw new Exception(info);
//		}
//		String p_waers=p_countryF4Bean.getL_country_map().get(p_branchF4Bean.getL_branch_map().get(p_contract.getBranch_id()).getCountry_id()).getCurrency();

		int skidkaOtDileraKlientu = 11;//Скидка от дилера клиенту
		BonusDao bonDao = (BonusDao) appContext.getContext().getBean("bonusDao");
		Bonus wa_bonus  = bonDao.dynamicFindBonus(" bonus_type_id = "+skidkaOtDileraKlientu+" and branch_id = "+p_contract.getBranch_id());
		double summaSkidkaOtDileraKlientu = 0;
		if (wa_bonus==null){

			throw new Exception("Скидка от дилера клиенту не определена.");	
		}
		else
		{
			summaSkidkaOtDileraKlientu = wa_bonus.getCoef();
		}
		
		if (p_contract.getDealer_subtract()>summaSkidkaOtDileraKlientu)
		{
			String info = "Сумма скидки от дилера должна быть в пределах: 0 и "+summaSkidkaOtDileraKlientu +" " + p_currency;
			disable_save_button();
			throw new Exception(info);			
		}else return true;
		
//		if (p_waers!=null && p_waers.equals("KZT") && p_contract.getDealer_subtract()>30000)
//		{
//			String info = "Сумма скидки от дилера должна быть в пределах: 0 и 30000 " + p_currency;
//			disable_save_button();
//			throw new Exception(info);			
//		}
//		else if (p_waers!=null && p_waers.equals("KGS") && p_contract.getDealer_subtract()>6800)
//		{
//			String info = "Сумма скидки от дилера должна быть в пределах: 0 и 6800 " + p_currency;
//			disable_save_button();
//			throw new Exception(info);
//		}
//		else if (p_waers!=null && p_waers.equals("UZS") && p_contract.getDealer_subtract()>650000)
//		{
//			String info = "Сумма скидки от дилера должна быть в пределах: 0 и 650000 " + p_currency;
//			disable_save_button();
//			throw new Exception(info);
//		}
//		else if (p_waers!=null && p_waers.equals("AZN") && p_contract.getDealer_subtract()>160)
//		{
//			String info = "Сумма скидки от дилера должна быть в пределах: 0 и 160 " + p_currency;
//			disable_save_button();
//			throw new Exception(info);
//		}
//		else return true;
	}

	public void removeCHLif(Long a_oper_on) {
		int j = chL.size();
		while (j > 0) {
			j--;
			ContractHistory wa_ch = chL.get(j);
			if (wa_ch.getOper_on().equals(a_oper_on)) {
				chL.remove(wa_ch);
			}
		}
	}

	// ***************************************************************************************************
	// ***************************************************************************************************
	// ***************************************************************************************************

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
	// ***************************Contract**********************************
	private Contract p_contract = new Contract();
	private Contract p_oldContract = new Contract();

	public Contract getP_contract() {
		return p_contract;
	}

	public void setP_contract(Contract p_contract) {
		this.p_contract = p_contract;
	}

	// ******************************************************************
	// ****************************ContractTypeF4***************************
	@ManagedProperty(value = "#{contractStatusF4Bean}")
	private ContractStatusF4 p_csF4Bean;

	public ContractStatusF4 getP_csF4Bean() {
		return p_csF4Bean;
	}

	public void setP_csF4Bean(ContractStatusF4 p_csF4Bean) {
		this.p_csF4Bean = p_csF4Bean;
	}

	List<ContractStatus> cs_list = new ArrayList<ContractStatus>();

	public List<ContractStatus> getCs_list() {
		return cs_list;
	}

	// *********************************************************************
	// ***************************PriceListF4*******************************
	@ManagedProperty(value = "#{priceListF4Bean}")
	private PriceListF4 p_priceListF4Bean;

	public PriceListF4 getP_priceListF4Bean() {
		return p_priceListF4Bean;
	}

	public void setP_priceListF4Bean(PriceListF4 p_priceListF4Bean) {
		this.p_priceListF4Bean = p_priceListF4Bean;
	}

	// ********************************************************************
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

	// ********************************************************************
	// ********************************************************************
	// ********************************************************************

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

	// *************************PRICE_TABLE_CLASS_*******************************

	public PriceTableClass currentPrice;

	public PriceTableClass selectedPrice;
	public PriceTableClass selectedPrice1 = new PriceTableClass();

	public PriceTableClass getSelectedPrice() {
		return selectedPrice;
	}

	public void setSelectedPrice(PriceTableClass selectedPrice) {
		this.selectedPrice = selectedPrice;
	}

	private List<PriceTableClass> priceTable = new ArrayList<PriceTableClass>();
	private List<PriceTableClass> priceTableOld = new ArrayList<PriceTableClass>();

	public List<PriceTableClass> getPriceTable() {
		return priceTable;
	}

	public void setPriceTable(List<PriceTableClass> priceTable) {
		this.priceTable = priceTable;
	}

	// *********************************************************************

	public Bkpf p_bkpf;

	public Bkpf getP_bkpf() {
		return p_bkpf;
	}

	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}

	public String p_currency;

	public double p_currate;

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

	public String p_check_text;
	public String p_check_text_color;

	public String getP_check_text() {
		return p_check_text;
	}

	public void setP_check_text(String p_check_text) {
		this.p_check_text = p_check_text;
	}

	public String getP_check_text_color() {
		return p_check_text_color;
	}

	public void setP_check_text_color(String p_check_text_color) {
		this.p_check_text_color = p_check_text_color;
	}

	public double min_first_payment;
	public int oldPaymentGraphic;
	public boolean paymentGraphicChange;

	public boolean new_adding_priceCheck;

	public boolean isNew_adding_priceCheck() {
		return new_adding_priceCheck;
	}

	public void setNew_adding_priceCheck(boolean new_adding_priceCheck) {
		this.new_adding_priceCheck = new_adding_priceCheck;
	}

	// *********************************************************************
	public String p_fioCustomer;
	public String p_fioDealer;
	public String p_check_icon; // ui-icon ui-icon-check
	public String p_disabled_save_button;
	public boolean p_check_payments;

	public String getP_disabled_save_button() {
		return p_disabled_save_button;
	}

	public void setP_disabled_save_button(String p_disabled_save_button) {
		this.p_disabled_save_button = p_disabled_save_button;
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

	public String getP_fioCustomer() {
		return p_fioCustomer;
	}

	public void setP_fioCustomer(String p_fioCustomer) {
		this.p_fioCustomer = p_fioCustomer;
	}

	public String getP_fioDealer() {
		return p_fioDealer;
	}

	public void setP_fioDealer(String p_fioDealer) {
		this.p_fioDealer = p_fioDealer;
	}

	// ***************************************************************************************

	public PaymentScheduleDetails psDetFirstPayment = new PaymentScheduleDetails();
	public List<PaymentScheduleDetails> psDetL = new ArrayList<PaymentScheduleDetails>();

	public PaymentScheduleDetails getPsDetFirstPayment() {
		return psDetFirstPayment;
	}

	public void setPsDetFirstPayment(PaymentScheduleDetails psDetFirstPayment) {
		this.psDetFirstPayment = psDetFirstPayment;
	}

	public List<PaymentScheduleDetails> getPsDetL() {
		return psDetL;
	}

	public void setPsDetL(List<PaymentScheduleDetails> psDetL) {
		this.psDetL = psDetL;
	}

	// ****************************************HISTORY********************************************

	public List<ContractHistory> chL = new ArrayList<ContractHistory>();

	public List<ContractHistory> getChL() {
		return chL;
	}

	public void setChL(List<ContractHistory> chL) {
		this.chL = chL;
	}

	// *********************************OLD_AND_NEW_CONTRACT_DETAILS******************************

	public ContractDetails new_cd;

	public ContractDetails getNew_cd() {
		return new_cd;
	}

	public void setNew_cd(ContractDetails new_cd) {
		this.new_cd = new_cd;
	}

	// *********************************************************************
	public PaymentSchedule ps[] = new PaymentSchedule[36];

	public PaymentSchedule[] getPs() {
		return ps;
	}

	public void setPs(PaymentSchedule[] ps) {
		this.ps = ps;
	}

	public Branch p_branch;

	public Branch getP_branch() {
		return p_branch;
	}

	public void setP_branch(Branch p_branch) {
		this.p_branch = p_branch;
	}

	public ContractType p_contrType;

	public ContractType getP_contrType() {
		return p_contrType;
	}

	public void setP_contrType(ContractType p_contrType) {
		this.p_contrType = p_contrType;
	}

	MessageProvider msgProvider = new MessageProvider();

	public double remain;
	public double average;
}
