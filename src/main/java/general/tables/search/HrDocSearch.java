package general.tables.search;

import general.Validation;
import general.tables.HrDoc;

public class HrDocSearch extends HrDoc implements ISearchTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6272874235304583659L;

	@Override
	public String getCondition() {
		String cond = "";
		if (!Validation.isEmptyString(getBukrs())) {
			cond = " bukrs = '" + getBukrs() + "' ";
		}

		if (getTypeId() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ") + " typeId = " + getTypeId();
		}

		if (!Validation.isEmptyLong(getBranchId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " branchId = " + getBranchId();
		}

		if (getStatusId() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ") + " statusId = " + getStatusId();
		}

		return cond;
	}

}
