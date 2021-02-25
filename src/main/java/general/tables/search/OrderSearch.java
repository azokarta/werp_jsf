package general.tables.search;

import general.Validation;
import general.tables.Order;

public class OrderSearch extends Order {

	private String[] werksFromIds;
	private String[] werksToIds;

	public String[] getWerksFromIds() {
		return werksFromIds;
	}

	public void setWerksFromIds(String[] werksFromIds) {
		this.werksFromIds = werksFromIds;
	}

	public String[] getWerksToIds() {
		return werksToIds;
	}

	public void setWerksToIds(String[] werksToIds) {
		this.werksToIds = werksToIds;
	}

	public String getCondition() {
		String condition = "";
		if (!Validation.isEmptyString(getBukrs())) {
			condition = " bukrs = '" + getBukrs() + "' ";
		}

		if (!Validation.isEmptyLong(getCustomer_id())) {
			condition += (condition.length() > 0 ? " AND  " : " ") + " customer_id = " + getCustomer_id();
		}

		if (!Validation.isEmptyLong(getCountry_id())) {
			condition += (condition.length() > 0 ? " AND  " : " ") + " country_id = " + getCountry_id();
		}
		return condition;
	}
}