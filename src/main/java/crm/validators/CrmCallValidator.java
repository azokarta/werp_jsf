package crm.validators;

import org.springframework.stereotype.Service;

import crm.constants.CallConstants;
import crm.tables.CrmCall;
import general.Validation;

@Service("crmCallValidator")
public class CrmCallValidator implements Validator {

	private String error;

	@Override
	public boolean isValid(Object ob) {
		CrmCall entity = (CrmCall) ob;
		error = "";

		if (Validation.isEmptyString(entity.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(entity.getBranchId())) {
			error += "Выберите филиал \n";
		}

		if (Validation.isEmptyString(entity.getContext())) {
			error += "Ошибк в программе: Контекст не выбран\n";
		}

		if (Validation.isEmptyLong(entity.getContextId())) {
			error += "Ошибк в программе: Контекст ID не выбран\n";
		}
		if (Validation.isEmptyString(entity.getPhoneNumber())) {
			error += "Выберите номер по которому звоните \n";
		}

		if (entity.getDateTime() == null) {
			error += "Введите дата время звонка \n";
		}

		if (entity.getResultId() == 0) {
			error += "Выберите результат звонка \n";
		}

		if (CallConstants.RESULT_POSITIVE.equals(entity.getResultId())) {
			entity.setRecallDateTime(null);
		} else if (CallConstants.RESULT_RECALL.equals(entity.getResultId())) {
			if (entity.getRecallDateTime() == null) {
				error += "Введите дата-время перезвона \n";
			}
		} else if (CallConstants.RESULT_REFUSE.equals(entity.getResultId())) {
			if (Validation.isEmptyLong(entity.getReasonId())) {
				error += "Выберите причину отказа \n";
			}
		}

		return Validation.isEmptyString(getError());
	}

	@Override
	public String getError() {
		return error;
	}

}
