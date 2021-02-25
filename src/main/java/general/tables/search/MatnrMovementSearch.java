package general.tables.search;

import general.Validation;
import general.tables.MatnrMovement;

public class MatnrMovementSearch extends MatnrMovement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3732744769859736808L;

	public String getCondition() {
		String cond = "";
		if (!Validation.isEmptyLong(getTo_werks())) {
			cond = " to_werks = " + this.getTo_werks();
		}

		if (!Validation.isEmptyString(getMm_type())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " mm_type = '"
					+ getMm_type() + "' ";
		}

		if (!Validation.isEmptyString(getStatus())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " status = '"
					+ getStatus() + "' ";
		}

		if (!Validation.isEmptyString(getBukrs())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " bukrs = '"
					+ getBukrs() + "' ";
		}

		return cond;
	}

}
