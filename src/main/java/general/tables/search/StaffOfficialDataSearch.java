package general.tables.search;

import general.tables.StaffOfficialData;

public class StaffOfficialDataSearch extends StaffOfficialData{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3479446941666473174L;

	public String getCondition() {
		String condition = "";
		
		if(this.getStaff_id() > 0L){
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" staff_id = %d", this.getStaff_id());
		}

		return condition;
	}
}
