package crm.validators;

import org.springframework.stereotype.Service;

import crm.tables.CrmDocReco;
import crm.tables.CrmPhone;
import general.Validation;

@Service("recoValidator")
public class CrmDocRecoValidator implements Validator {

	private String error;

	@Override
	public boolean isValid(Object ob) {
		error = "";
		CrmDocReco entity = (CrmDocReco) ob;
		if (Validation.isEmptyString(entity.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(entity.getBranchId())) {
			error += " Выберите филиал \n";
		}

		if (Validation.isEmptyString(entity.getTempRecommender())) {
			error += "Введите имя рекомендателя \n";
		}

		if (Validation.isEmptyString(entity.getClientName())) {
			error += "Введите ФИО клиента \n";
		}

		if (Validation.isEmptyLong(entity.getResponsibleId())) {
			error += " Выберите ответственного сотрудника \n";
		}

		// if (Validation.isEmptyString(entity.getMobPhone()) &&
		// Validation.isEmptyString(entity.getHomePhone())
		// && Validation.isEmptyString(entity.getWorkPhone())) {
		// error += "Введите номер для звонка (Моб. или Дом. или Раб.) \n";
		// }

		if (entity.getCallDate() == null) {
			error += "Введите Дата время звонка \n";
		}

		for (CrmPhone phone : entity.getPhones()) {
			if (Validation.isEmptyString(phone.getPhoneNumber())) {
				error += "Введите хотя бы 1 номер телефона \n";
				break;
			}
		}

		return Validation.isEmptyString(error);
	}

	@Override
	public String getError() {
		return error;
	}

}
