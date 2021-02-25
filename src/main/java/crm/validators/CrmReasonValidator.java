package crm.validators;

import org.springframework.stereotype.Service;

import crm.tables.CrmReason;
import general.Validation;

@Service("crmReasonValidator")
public class CrmReasonValidator implements Validator {

	private String error;

	@Override
	public boolean isValid(Object ob) {
		CrmReason entity = (CrmReason) ob;
		error = "";

		if (Validation.isEmptyString(entity.getName())) {
			error += "Введите название \n";
		}

		if (entity.getTypeId() == null || entity.getTypeId() == 0) {
			error += "Выберите тип причины \n";
		}

		return Validation.isEmptyString(getError());
	}

	@Override
	public String getError() {
		return error;
	}

}
