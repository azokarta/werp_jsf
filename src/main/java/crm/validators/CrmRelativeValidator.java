package crm.validators;

import org.springframework.stereotype.Service;

import crm.tables.CrmRelative;
import general.Validation;

@Service("crmRelativeValidator")
public class CrmRelativeValidator implements Validator {

	private String error;

	@Override
	public boolean isValid(Object ob) {
		CrmRelative entity = (CrmRelative) ob;
		error = "";

		if (Validation.isEmptyString(entity.getName())) {
			error += "Введите название \n";
		}

		return Validation.isEmptyString(getError());
	}

	@Override
	public String getError() {
		return error;
	}

}
