package crm.validators;

import org.springframework.stereotype.Service;

import crm.constants.DemoConstants;
import crm.tables.CrmDocDemo;
import general.Validation;

@Service("demoValidator")
public class CrmDocDemoValidator implements Validator {

	private String error;

	@Override
	public boolean isValid(Object ob) {
		error = "";
		CrmDocDemo entity = (CrmDocDemo) ob;
		if (Validation.isEmptyString(entity.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(entity.getBranchId())) {
			error += " Выберите филиал \n";
		}
		if (Validation.isEmptyString(entity.getClientName())) {
			error += "Введите Имя или Фамилию клиента \n";
		}

		if (Validation.isEmptyLong(entity.getDealerId())) {
			error += " Выберите ответственного сотрудника \n";
		}

		if (entity.getCallDate() == null && !DemoConstants.APPOINTED_BY_CLIENT.equals(entity.getAppointedBy())) {
			error += "Введите Дата время звонка \n";
		}

//		if (Validation.isEmptyInteger(entity.getAppointedBy())) {
//			error += "Выберите кто назначел демо \n";
//		}

		if (Validation.isEmptyString(entity.getAddress())) {
			error += "Введите адрес \n";
		}

		if (entity.getLocationId() == null || entity.getLocationId() == 0) {
			error += "Выберите местоположение \n";
		}

		if (DemoConstants.RESULT_SOLD.equals(entity.getResultId())) {
			if (entity.getSaleDate() == null) {
				error += "Введите дату продажи \n";
			}
		}

		return Validation.isEmptyString(error);
	}

	@Override
	public String getError() {
		return error;
	}

}
