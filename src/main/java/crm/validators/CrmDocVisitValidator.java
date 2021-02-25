package crm.validators;

import org.springframework.stereotype.Service;

import crm.tables.CrmDocVisit;
import general.Validation;

@Service("crmVisitValidator")
public class CrmDocVisitValidator implements Validator {

	private String error;

	@Override
	public boolean isValid(Object ob) {
		error = "";
		CrmDocVisit entity = (CrmDocVisit) ob;
		if (Validation.isEmptyString(entity.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(entity.getBranchId())) {
			error += " Выберите филиал \n";
		}
		if (Validation.isEmptyString(entity.getClientName())) {
			error += "Введите Имя или Фамилию клиента \n";
		}

		if (Validation.isEmptyLong(entity.getVisitorId())) {
			error += " Выберите посетителя \n";
		}

		if (entity.getDocDate() == null) {
			error += "Введите Дату визита \n";
		}

		return Validation.isEmptyString(error);
	}

	@Override
	public String getError() {
		return error;
	}

}
