package general.tables.search;

import general.Validation;
import general.tables.State;

public class StateSearch extends State {

	public String getCondition() {
		String cond = "";
		if (!Validation.isEmptyString(getStatename())) {
			cond += " statename LIKE '%" + getStatename() + "%' ";
		}

		if (!Validation.isEmptyLong(getCountryid())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " countryid = "
					+ getCountryid();
		}

		return cond;
	}
}
