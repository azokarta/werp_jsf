package general.validators;

import org.springframework.stereotype.Component;

import general.Validation;
import general.tables.HrDocTransfer;
import general.tables.HrDocTransferItem;

@Component("hrDocTransferValidator")
public class HrDocTransferValidatorImpl implements HrDocTransferValidator {

	private String error;

	@Override
	public boolean isValid(HrDocTransfer hrDocTransfer) {
		error = "";
		validateHrDoc(hrDocTransfer);
		validateItems(hrDocTransfer);
		return error.length() == 0;
	}

	private void validateHrDoc(HrDocTransfer hrDocTransfer) {
		if (Validation.isEmptyString(hrDocTransfer.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(hrDocTransfer.getBranchId())) {
			error += "Выберите филиал \n";
		}
	}

	private void validateItems(HrDocTransfer hrDocTransfer) {
		if (hrDocTransfer.getHrDocTransferItems() == null || hrDocTransfer.getHrDocTransferItems().size() == 0) {
			error += "Выберите сотрудника" + "\n";
		} else {
			for (HrDocTransferItem item : hrDocTransfer.getHrDocTransferItems()) {
				if (!Validation.isEmptyString(error)) {
					break;
				}
				if (Validation.isEmptyLong(item.getBranchId())) {
					error += "Выберите филиал" + "\n";
				}

				if (Validation.isEmptyLong(item.getPositionId())) {
					error += "Выберите должность" + "\n";
				}

				if (item.getBeginDate() == null) {
					error += "Введите дату начало" + "\n";
				}
			}
		}
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return error;
	}

}
