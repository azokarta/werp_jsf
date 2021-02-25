package general.tables.search;

import java.util.List;

import general.Validation;
import general.tables.Salary;

public class SalarySearch extends Salary implements ISearchTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 769602920598821580L;

	public String getCondition() {
		String cond = " 1 = 1 ";
		if (!Validation.isEmptyLong(getPosition_id())) {
			cond += " AND position_id = " + getPosition_id();
		}
		return cond;
	}

	private String firstname;
	private String lastname;
	private List<String> branchIds;
	private String[] branchIdsArray;

	public String[] getBranchIdsArray() {
		return branchIdsArray;
	}

	public void setBranchIdsArray(String[] branchIdsArray) {
		this.branchIdsArray = branchIdsArray;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public List<String> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<String> branchIds) {
		this.branchIds = branchIds;
	}

	public String getSpecialCondition() {

		String cond = " s1.staff_id IS NOT NULL AND s1.staff_id != 0 ";
		// System.out.println("TEMP: " + bukrs + " => " + positionId);
		if (!Validation.isEmptyString(getBukrs())) {
			cond += " AND s1.bukrs = '" + getBukrs() + "' ";
		}

		if (!Validation.isEmptyLong(getBranch_id())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + getBranch_id() + " ";
		}

		if (!Validation.isEmptyLong(getPosition_id())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.position_id = " + getPosition_id() + " ";
		}

		if (!Validation.isEmptyString(firstname)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " s2.firstname LIKE '%" + firstname + "%' ";
		}

		if (!Validation.isEmptyString(lastname)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " s2.lastname LIKE '%" + lastname + "%' ";
		}

		// if(isCurrent){
		// cond += " AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "'
		// ";
		// }

		System.out.println("CONDDD: " + cond);
		return cond.length() > 0 ? cond : " 1 = 1 ";
	}

}