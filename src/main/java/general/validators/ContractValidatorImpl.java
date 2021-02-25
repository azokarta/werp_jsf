package general.validators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;
import general.GeneralUtil;
import general.Validation;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.PaymentScheduleDao;
import general.dao.UserRoleDao;
import general.tables.Contract;
import general.tables.ContractStatus;
import general.tables.PaymentSchedule;
import general.tables.UserRole;

@Service("contractValidator")
public class ContractValidatorImpl implements ContractValidator {

	@Autowired
	ContractDao conDao;

	@Autowired
	PaymentScheduleDao psDao;

	@Autowired
	CustomerDao cusDao;

	@Override
	public boolean validateContract(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers,
			String refDiscountWaers, String locale, boolean isNew, User userData) throws Exception {
		try {
			validateContractEssentials(a_contract, a_ps, refRate, refWaers, refDiscountWaers, locale, isNew);
			validateContractStatus(a_contract, a_ps, refRate, refWaers, refDiscountWaers, locale, isNew, userData);
			if (!Validation.isEmptyLong(a_contract.getRef_contract_id()))
				validateReferencer(a_contract, refRate, refWaers, refDiscountWaers, locale);
			if (isNew)
				validateContractDate(a_contract, locale, userData);

			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Override
	public boolean validateContractEssentials(Contract a_contract, PaymentSchedule a_ps[], double refRate,
			String refWaers, String refDiscountWaers, String locale, boolean isNew) throws Exception {
		try {
			Map<String, String> msg = new HashMap<String, String>();

			msg.clear();
			msg.put("ru", "Выберите компанию!");
			msg.put("en", "Please select company");
			msg.put("tr", "Lütfen şirketi seçiniz!");
			if (Validation.isEmptyString(a_contract.getBukrs()) || a_contract.getBukrs().equals("0"))
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Выберите филиал!");
			msg.put("en", "Please select branch!");
			msg.put("tr", "Lütfen şübeyi seçiniz!");
			if (Validation.isEmptyLong(a_contract.getBranch_id()))
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Выберите Сервис-Филиал!");
			msg.put("en", "Please select Service-Branch!");
			msg.put("tr", "Lütfen Servis-Şübeyi seçiniz!");
			if (Validation.isEmptyLong(a_contract.getServ_branch_id()))
				throw new DAOException(msg.get(locale));
			
			msg.clear();
			msg.put("ru", "Выберите филиал по Финансам!");
			msg.put("en", "Please select Finance-branch!");
			msg.put("tr", "Lütfen Finans-şübeyi seçiniz!");
			if (Validation.isEmptyLong(a_contract.getFinBranchId()))
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Выберите вид договора!");
			msg.put("en", "Please select contract type!");
			msg.put("tr", "Lütfen sözleşme tipini seçiniz!");
			if (Validation.isEmptyLong(a_contract.getContract_type_id()))
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Укажите дату договора!");
			msg.put("en", "Please enter contract date!");
			msg.put("tr", "Lütfen sözleşme tarihini belirtin!");
			if (a_contract.getContract_date() == null)
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Выберите клиента!");
			msg.put("en", "Please select customer!");
			msg.put("tr", "Lütfen müşteriyi seçiniz!");
			if (Validation.isEmptyLong(a_contract.getCustomer_id()))
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Выберите заводской номер товара!");
			msg.put("en", "Please select Barcode!");
			msg.put("tr", "Lütfen cihaz seri numarasını seçiniz!");
			if (Validation.isEmptyString(a_contract.getTovar_serial()))
				throw new DAOException(msg.get(locale));

			// ********************************NEW_ADDRESS************************************

			msg.clear();
			msg.put("ru", "Выберите домашний адрес!");
			msg.put("en", "Please select home address!");
			msg.put("tr", "Lütfen ev adresini seçiniz!");
			if (Validation.isEmptyLong(a_contract.getAddrHomeId()))
				throw new DAOException(msg.get(locale));

			msg.clear();
			msg.put("ru", "Выберите адрес для сервиса!");
			msg.put("en", "Please select service address!");
			msg.put("tr", "Lütfen servis adresi seçiniz!");
			if (Validation.isEmptyLong(a_contract.getAddrServiceId()))
				throw new DAOException(msg.get(locale));

			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Override
	public boolean validateContractStatus(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers, String refDiscountWaers, String locale, boolean isNew, User userData) throws Exception {
		try {
			Map<String, String> msg = new HashMap<String, String>();
			
			List<UserRole> urL  = new ArrayList<UserRole>();
			urL = urlDao.findUserRoles(userData.getUserid());
			
			boolean isCoordinator = false;
			//System.out.println("Role list: " + urL.size());
			for (UserRole wa_ur : urL) {
				//System.out.println(wa_ur.getRoleId());
				if (wa_ur.getRoleId() == 25 || wa_ur.getRoleId() == 19 || wa_ur.getRoleId() == 115) {
					isCoordinator = true;
					//System.out.println("IS COORDINATOR!");
				}
			}				
			System.out.println("IS COORDINATOR: " + isCoordinator);
			
			// STANDARD
			if (a_contract.getContract_status_id() == ContractStatus.STATUS_STANDARD) { 
				
				if (a_contract.getPaid() == a_contract.getPrice()) {
					msg.clear();
					msg.put("ru", "Статус договора неправильный!");
					msg.put("en", "Contract status not correct!");
					msg.put("tr", "Sözleşme durumu yanlış!");
					throw new DAOException(msg.get(locale));	
				}
				// if Customer is NOT our employee
				if (cusDao.countCustomerEmployeeByDate(a_contract.getCustomer_id(), a_contract.getContract_date()) == 0) {
			
					msg.clear();
					msg.put("ru", "Выберите Дилера!");
					msg.put("en", "Please select Dealer!");
					msg.put("tr", "Lütfen Dileri seçiniz!");
					if (Validation.isEmptyLong(a_contract.getDealer())
							&& a_contract.getMarkedType() == Contract.MT_STANDARD_PRICE)
						throw new DAOException(msg.get(locale));
					
					msg.clear();
					msg.put("ru", "Выберите Финагента!");
					msg.put("en", "Please select Collector!");
					msg.put("tr", "Lütfen Tahsilatçıyı seçiniz!");
					if (Validation.isEmptyLong(a_contract.getCollector())
							&& a_contract.getPayment_schedule() > 0)
						throw new DAOException(msg.get(locale));
				}
				// if Customer is our employee
				else {					
					
					if (a_contract.getDealer() != null
							&& a_contract.getDealer() > 0 && !isCoordinator) {
						msg.clear();
						msg.put("ru", "Дилер не может быть выбран! Клиент является сотрудником компании");
						msg.put("en", "Dealer cannot be chosen, Customer is our employee!");
						msg.put("tr", "Diler seçilemez! Müşteri şirketi elamanı!");
						throw new DAOException(msg.get(locale));
					}
					if (a_contract.getDemo_sc() != null
							&& a_contract.getDemo_sc() > 0) {
						msg.clear();
						msg.put("ru", "Демо-секретарь не может быть выбран! Клиент является сотрудником компании");
						msg.put("en", "Demo-secretary cannot be chosen, Customer is our employee!");
						msg.put("tr", "Demo-sekreter seçilemez! Müşteri şirketi elamanı!");
						throw new DAOException(msg.get(locale));
					}
					if (a_contract.getCollector() != null
							&& a_contract.getCollector() > 0) {
						msg.clear();
						msg.put("ru", "Финагент не может быть выбран! Клиент является сотрудником компании");
						msg.put("en", "Collector cannot be chosen, Customer is our employee!");
						msg.put("tr", "Tahsilatçı seçilemez! Müşteri şirketi elamanı!");
						throw new DAOException(msg.get(locale));
					}					
				}
				
				validateContractPrice(a_contract, a_ps, refRate, refWaers, refDiscountWaers, locale);

			} else 
			// PRESENT
			if (a_contract.getContract_status_id() == ContractStatus.STATUS_GIFT) {
				if (!Validation.isEmptyLong(a_contract.getPrice_list_id())
						|| a_contract.getPrice() > 0
						|| a_contract.getPayment_schedule() > 0) {
					
					msg.clear();
					msg.put("ru", "Прайс и срок рассрочки должна быть равна 0 для такого статуса договора! ");
					msg.put("en", "Price and payment schedule must be 0 for this kind of contract status!");
					msg.put("tr", "Fiyat ve taksit sayısı 0 olası lazım bu sözleşme durumu için!");
					throw new DAOException(msg.get(locale));
				}
				if (a_contract.getDealer() != null
						&& a_contract.getDealer() > 0) {
					msg.clear();
					msg.put("ru", "Дилер не может быть выбран! Статус договора!");
					msg.put("en", "Dealer cannot be chosen, Contract Status!");
					msg.put("tr", "Diler seçilemez! Sözleşme durumu!");
					throw new DAOException(msg.get(locale));
					
				}
				if (a_contract.getDemo_sc() != null
						&& a_contract.getDemo_sc() > 0) {
					msg.clear();
					msg.put("ru", "Демо-секретарь не может быть выбран! Статус договора!");
					msg.put("en", "Demo-secretary cannot be chosen, Contract Status!");
					msg.put("tr", "Demo-sekreter seçilemez! Sözleşme durumu!");
					throw new DAOException(msg.get(locale));
					
				}
				if (a_contract.getCollector() != null
						&& a_contract.getCollector() > 0) {
					msg.clear();
					msg.put("ru", "Финагент не может быть выбран! Статус договора!");
					msg.put("en", "Collector cannot be chosen, Contract Status!");
					msg.put("tr", "Tahsilatçı seçilemez! Sözleşme durumu!");
					throw new DAOException(msg.get(locale));
				}
				if (a_contract.getFitter() != null
						&& a_contract.getFitter() > 0) {
					msg.clear();
					msg.put("ru", "Установщик не может быть выбран! Статус договора!");
					msg.put("en", "Fitter cannot be chosen, Contract Status!");
					msg.put("tr", "Montajcı seçilemez! Sözleşme durumu!");
					throw new DAOException(msg.get(locale));
				}
			} else {
				msg.clear();
				msg.put("ru", "Статус договора неправильный!");
				msg.put("en", "Contract status not correct!");
				msg.put("tr", "Sözleşme durumu yanlış!");
				if (isNew && a_contract.getContract_status_id() > 2)
					throw new DAOException(msg.get(locale));
				
				if (a_contract.getContract_status_id() == ContractStatus.STATUS_CANCELLED
						|| (a_contract.getContract_status_id() == ContractStatus.STATUS_CLOSED
						&& a_contract.getPrice() > a_contract.getPaid())) {
					throw new DAOException(msg.get(locale));				
				}
			}						
			
			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Override
	public boolean validateContractPrice(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers,
			String refDiscountWaers, String locale) throws Exception {
		try {
			Map<String, String> msg = new HashMap<String, String>();

			// =============================Price&CCustomer&Staff=============================
			msg.clear();
			msg.put("ru", "Выберите прайс!");
			msg.put("en", "Please select price!");
			msg.put("tr", "Lütfen fiyatı seçiniz!");
			if (Validation.isEmptyLong(a_contract.getPrice_list_id())
					&& (a_contract.getContract_status_id() != ContractStatus.STATUS_CANCELLED
							|| a_contract.getContract_status_id() != ContractStatus.STATUS_GIFT)
					&& a_contract.getMarkedType() == Contract.MT_STANDARD_PRICE 
					&& (Validation.isEmptyLong(a_contract.getOld_id())))
				throw new DAOException(msg.get(locale));
			
			if (!checkPaymentScheduleAndPrice(a_contract, a_ps)) {
				msg.clear();
				msg.put("ru", "Ошибка в сумме графика платежей!");
				msg.put("en", "Payment Schedule and Price not correct!");
				msg.put("tr", "Ödeme grafiğinde sayılarda yanlış var!");
				throw new DAOException(msg.get(locale));
			}

			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	@Autowired
	UserRoleDao urlDao;

	@Override
	public boolean validateContractDate(Contract a_contract, String locale, User userData) throws Exception {
		try {
			
			List<UserRole> urL  = new ArrayList<UserRole>();
			urL = urlDao.findUserRoles(userData.getUserid());
			
			boolean isCoordinator = false;
			//System.out.println("Role list: " + urL.size());
			for (UserRole wa_ur : urL) {
				//System.out.println(wa_ur.getRoleId());
				if (wa_ur.getRoleId() == 25 || wa_ur.getRoleId() == 19 || wa_ur.getRoleId() == 115) {
					isCoordinator = true;
					//System.out.println("IS COORDINATOR!");
				}
			}				
			System.out.println("IS COORDINATOR: " + isCoordinator);
			
			Map<String, String> msg = new HashMap<String, String>();

			Calendar curDate = Calendar.getInstance();
			Calendar conDate = Calendar.getInstance();
			conDate.setTime(a_contract.getContract_date());
			Calendar firstDay = Calendar.getInstance();
			Calendar lastDay = Calendar.getInstance();
			firstDay.setTime(GeneralUtil.getFirstDateOfMonth(curDate.getTime()));
			lastDay.setTime(GeneralUtil.getLastDateOfMonth(curDate.getTime()));

			System.out.println("In Date: " + conDate.getTime().getYear() + "-" + conDate.getTime().getMonth() + "-"+conDate.getTime().getDate());
			System.out.println("First Date: " + firstDay.getTime().getYear() + "-" + firstDay.getTime().getMonth() + "-"+firstDay.getTime().getDate());
			System.out.println("Last Date: " + lastDay.getTime().getYear() + "-" + lastDay.getTime().getMonth() + "-"+lastDay.getTime().getDate());
			// firstDay.set(curDate.get(Calendar.YEAR),curDate.get(Calendar.MONTH),
			// 1, 0, 1, 1);
			if (curDate.getTime().getDate() < 10) {
				System.out.println("CurDate: " + curDate.getTime().getDate());
				firstDay.add(Calendar.MONTH, -1);
			}

			curDate.set(Calendar.HOUR_OF_DAY, 23);
			curDate.set(Calendar.MINUTE, 59);
			curDate.set(Calendar.SECOND, 59);
			curDate.set(Calendar.MILLISECOND, curDate.getActualMaximum(Calendar.MILLISECOND));
			// System.out.println("ConDate: " + conDate.getTime() + " InMillis:
			// " + conDate.getTimeInMillis());
			// System.out.println("FirstDate: " + firstDay.getTime() + "
			// InMillis: " + firstDay.getTimeInMillis());
			// System.out.println("LastDate: " + lastDay.getTime() + " InMillis:
			// " + lastDay.getTimeInMillis());
			if ((isCoordinator && conDate.getTimeInMillis() > lastDay.getTimeInMillis()) 
					|| (!((conDate.getTimeInMillis() >= firstDay.getTimeInMillis())
					&& (conDate.getTimeInMillis() <= curDate.getTimeInMillis())) 
					&& !isCoordinator)) {
				msg.clear();
//				msg.put("ru", "Дата договора должна быть в пределах текущего месяца!");
//				msg.put("en", "Contract date must be in the current month!");
//				msg.put("tr", "Sözleşme tarihi bu ayın içerisinde olması lazım!");
				msg.put("ru", "Дата договора не должна превышать текущую дату!");
				msg.put("en", "Contract date cannot exceed current date!");
				msg.put("tr", "Sözleşme tarihi bugünün tarihini aşamaz!");
				throw new DAOException(msg.get(locale));
			}

			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Override
	public boolean validateReferencer(Contract a_contract, double refRate, String refWaers, String refDiscountWaers,
			String locale) throws Exception {
		Map<String, String> msg = new HashMap<String, String>();
		msg.clear();
		msg.put("ru", "Укажите курс валюты рекомендателя!");
		msg.put("en", "Please write the Referencer's currency rate!");
		msg.put("tr", "Lütfen tavsiye edenin para birimi kurunu belirtin!");

		if (!Validation.isEmptyLong(a_contract.getRef_contract_id()) && refRate <= 1
				&& !refWaers.equals(refDiscountWaers))
			throw new DAOException(msg.get(locale));
		return true;
	}

	@Override
	public boolean checkFirstPayment(Long a_con_number) throws DAOException {
		try {
			boolean res = false;
			Contract con = conDao.findByContractNumber(a_con_number);
			List<PaymentSchedule> psList = psDao.findAllByAwkey(con.getAwkey(),con.getBukrs());
			if (psList.size() > 0 && (psList.get(0).getPaid() >= psList.get(0).getSum())) {
				res = true;
			}
			return res;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean checkPaymentScheduleAndPrice(Contract a_contract, PaymentSchedule a_ps[]) {
		if (a_contract.getPrice() > 0) {

			int numPaySc = 0;
			double paymentDueSum = 0;

			for (int i = 1; i <= a_contract.getPayment_schedule(); i++) {
				if (a_ps[i].getSum() > 0) {
					numPaySc += 1;
				}
				paymentDueSum += a_ps[i].getSum();
			}

			if (numPaySc <= a_contract.getPayment_schedule()
					&& a_contract.getPrice() == a_contract.getFirst_payment() + paymentDueSum) {
				return true;
			} else
				return false;

		} else if (a_contract.getPayment_schedule() == 0 && a_contract.getPrice() == 0) {
			double paymentDueSum = 0;

			for (int i = 1; i <= a_contract.getPayment_schedule(); i++) {
				paymentDueSum += a_ps[i].getSum();
			}

			if ((a_contract.getPrice() == a_contract.getFirst_payment() + paymentDueSum)
					&& (a_contract.getContract_status_id() == 2)) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public boolean validateServiceContract(Contract a_contract, User userData) throws Exception {
		Map<String, String> msg = new HashMap<String, String>();
		
		msg.clear();
		msg.put("ru", "Выберите компанию!");
		msg.put("en", "Please select company");
		msg.put("tr", "Lütfen şirketi seçiniz!");
		if (Validation.isEmptyString(a_contract.getBukrs()) || a_contract.getBukrs().equals("0"))
			throw new DAOException(msg.get(userData.getU_language()));

		msg.clear();
		msg.put("ru", "Выберите филиал!");
		msg.put("en", "Please select branch!");
		msg.put("tr", "Lütfen şübeyi seçiniz!");
		if (Validation.isEmptyLong(a_contract.getBranch_id()))
			throw new DAOException(msg.get(userData.getU_language()));

		msg.clear();
		msg.put("ru", "Выберите Сервис-Филиал!");
		msg.put("en", "Please select Service-Branch!");
		msg.put("tr", "Lütfen Servis-Şübeyi seçiniz!");
		if (Validation.isEmptyLong(a_contract.getServ_branch_id()))
			throw new DAOException(msg.get(userData.getU_language()));
		
		msg.clear();
		msg.put("ru", "Выберите вид договора!");
		msg.put("en", "Please select contract type!");
		msg.put("tr", "Lütfen sözleşme tipini seçiniz!");
		if (Validation.isEmptyLong(a_contract.getContract_type_id()))
			throw new DAOException(msg.get(userData.getU_language()));

		msg.clear();
		msg.put("ru", "Укажите дату договора!");
		msg.put("en", "Please enter contract date!");
		msg.put("tr", "Lütfen sözleşme tarihini belirtin!");
		if (a_contract.getContract_date() == null)
			throw new DAOException(msg.get(userData.getU_language()));

		msg.clear();
		msg.put("ru", "Выберите клиента!");
		msg.put("en", "Please select customer!");
		msg.put("tr", "Lütfen müşteriyi seçiniz!");
		if (Validation.isEmptyLong(a_contract.getCustomer_id()))
			throw new DAOException(msg.get(userData.getU_language()));

		msg.clear();
		msg.put("ru", "Выберите заводской номер товара!");
		msg.put("en", "Please select Barcode!");
		msg.put("tr", "Lütfen cihaz seri numarasını seçiniz!");
		if (Validation.isEmptyString(a_contract.getTovar_serial()))
			throw new DAOException(msg.get(userData.getU_language()));

		// ********************************NEW_ADDRESS************************************

		msg.clear();
		msg.put("ru", "Выберите адрес для сервиса!");
		msg.put("en", "Please select service address!");
		msg.put("tr", "Lütfen servis adresi seçiniz!");
		if (Validation.isEmptyLong(a_contract.getAddrServiceId()))
			throw new DAOException(msg.get(userData.getU_language()));
		
		return true;
	}
}