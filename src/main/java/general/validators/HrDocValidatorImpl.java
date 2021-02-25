package general.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.HrDocItemDao;
import general.tables.HrDoc;
import general.tables.HrDocItem;

@Component("hrDocValidator")
public class HrDocValidatorImpl implements HrDocValidator {

	@Autowired
	HrDocItemDao hrDocItemDao;

	private String error;

	@Override
	public boolean isValid(HrDoc doc) {
		error = "";
		validateHrDoc(doc);
		validateItems(doc);
		return error.length() == 0;
	}

	private void validateHrDoc(HrDoc doc) {
		if (Validation.isEmptyString(doc.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(doc.getBranchId())) {
			error += "Выберите филиал \n";
		}
	}

	private void validateItems(HrDoc doc) {
		if (doc.getHrDocItems() == null || doc.getHrDocItems().size() == 0) {
			error += "Выберите сотрудника" + "\n";
		} else {
			if (HrDoc.TYPE_RECRUITMENT == doc.getTypeId()) {
				for (HrDocItem item : doc.getHrDocItems()) {
					item.setBranchId(doc.getBranchId());
					item.setBukrs(doc.getBukrs());
					if (!Validation.isEmptyString(error)) {
						break;
					}

					if (Validation.isEmptyLong(item.getPositionId())) {
						error += "Выберите должность" + "\n";
					}

					if (item.getBeginDate() == null) {
						error += "Введите дату начало" + "\n";
					}
				}
			} else if (HrDoc.TYPE_TRANSFER == doc.getTypeId()) {
				for (HrDocItem item : doc.getHrDocItems()) {
					item.setBukrs(doc.getBukrs());
					if (!Validation.isEmptyString(error)) {
						break;
					}

					if (Validation.isEmptyLong(item.getBranchId())) {
						error += "Выберите новый филиал" + "\n";
					}

					if (Validation.isEmptyLong(item.getPositionId())) {
						error += "Выберите новую должность" + "\n";
					}

					if (item.getBeginDate() == null) {
						error += "Введите дату начало" + "\n";
					}

					if (Validation.isEmptyLong(item.getDepartmentId())) {
						error += "Выберите департамент" + "\n";
					}
				}
			} else if (HrDoc.TYPE_BYPASS_SHEET == doc.getTypeId()) {
				for (HrDocItem item : doc.getHrDocItems()) {
					item.setBukrs(doc.getBukrs());
					List<HrDocItem> notClosedItems = hrDocItemDao.findAllNotClosedBypassDocByStaffId(item.getStaffId());
					if (notClosedItems.size() > 0) {
						error += "Для сотрудника уже создан \"Обходной лист\" \n";
					}
					if (!Validation.isEmptyString(error)) {
						break;
					}
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
