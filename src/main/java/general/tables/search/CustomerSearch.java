package general.tables.search;

import general.tables.Customer;

public class CustomerSearch extends Customer {

	public String getCondition() {
		String condition = "";
		if (this.getFiz_yur() > 0) {
			condition += " fiz_yur = " + this.getFiz_yur();
		}

		if (this.getIin_bin() != null && this.getIin_bin().length() > 0) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" iin_bin LIKE '%s'",
							"%" + this.getIin_bin() + "%");
		}

		if (this.getName() != null && this.getName().length() > 0) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" name LIKE '%s'", "%" + this.getName()
							+ "%");
		}

		if (this.getFirstname() != null && this.getFirstname().length() > 0) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" firstname LIKE '%s'",
							"%" + this.getFirstname() + "%");
		}

		if (this.getLastname() != null && this.getLastname().length() > 0) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" lastname LIKE '%s'",
							"%" + this.getLastname() + "%");
		}

		return condition;
	}
}
