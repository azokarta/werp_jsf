package general.tables.search;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import general.Validation;
import general.tables.Demonstration;

public class DemoSearch extends Demonstration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 577047588297279515L;

	private Date fromDate;
	private Date toDate;
	private List<String> branchIds;

	public List<String> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<String> branchIds) {
		this.branchIds = branchIds;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getCondition() {
		String cond = "";

		if (!Validation.isEmptyString(getBukrs())) {
			cond += " bukrs = " + getBukrs();
		}

		if (!Validation.isEmptyLong(getBranchId())) {
			cond += " AND branch_id = " + getBranchId();
		}

		if (!Validation.isEmptyLong(getDealerId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " dealer_id = " + getDealerId();
		}

		if (!Validation.isEmptyLong(getDemosecId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " demosec_id = " + getDemosecId();
		}

		// if (getFromDate() != null) {
		// SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY 00:00:00");
		// String st = String.format("to_timestamp('%s','dd-mm-yyyy
		// hh24:mi:ss')",
		// sdf.format(getFromDate()).toString());
		// cond += (cond.length() > 0 ? " AND " : " ") + " date_time >= " + st;
		// }
		//
		// if (getToDate() != null) {
		// SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY 00:00:00");
		// String st = String.format("to_timestamp('%s','dd-mm-yyyy
		// hh24:mi:ss')", sdf.format(getToDate()).toString());
		//
		// cond += (cond.length() > 0 ? " AND " : " ") + " date_time <= " + st;
		// }

		if (getStatusId() != null && getStatusId() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ") + " status_id = " + getStatusId();
		}

		if (!Validation.isEmptyString(getCustomerName())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " customer_name LIKE '%" + getCustomerName() + "%'";
		}

		if (!Validation.isEmptyString(getCustomerMobile())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " customer_mobile LIKE '%" + getCustomerMobile() + "%'";
		}

		return cond;
	}

}
