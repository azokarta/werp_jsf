package general.validators;

import user.User;
import general.dao.DAOException;
import general.tables.Contract;
import general.tables.PaymentSchedule;

public interface ContractValidator {
	boolean validateContract(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers, String refDiscountWaers, String locale, boolean isNew, User userData) throws Exception;
	boolean validateContractEssentials(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers, String refDiscountWaers, String locale, boolean isNew) throws Exception;
	boolean validateContractStatus(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers, String refDiscountWaers, String locale, boolean isNew, User userData) throws Exception;
	boolean validateContractPrice(Contract a_contract, PaymentSchedule a_ps[], double refRate, String refWaers, String refDiscountWaers, String locale) throws Exception;
	public boolean validateContractDate(Contract a_contract, String locale, User userData) throws Exception;
	public boolean validateReferencer(Contract a_contract, double refRate, String refWaers, String refDiscountWaers, String locale) throws Exception;
	public boolean checkFirstPayment(Long a_con_number) throws DAOException;
	boolean checkPaymentScheduleAndPrice(Contract a_contract, PaymentSchedule a_ps[]);
	boolean validateServiceContract(Contract a_con, User userData) throws Exception;
}
