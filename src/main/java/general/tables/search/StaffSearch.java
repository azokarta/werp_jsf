package general.tables.search;

import general.AppContext;
import general.Validation;
import general.tables.Staff;

public class StaffSearch extends Staff implements ISearchTable {

	private String bukrs = "";

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	public StaffSearch(AppContext ac) {
		this.appContext = ac;
	}
	public StaffSearch() {
		// TODO Auto-generated constructor stub
	}

	public String getCondition() {
		String condition = "";
		if (this.getIin_bin() != null && this.getIin_bin().length() > 0) {
			condition += String.format(" iin_bin LIKE '%s'",
					"%" + this.getIin_bin() + "%");
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
		if (this.getEmail() != null && this.getEmail().length() > 0) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" email LIKE '%s'", "%" + this.getEmail()
							+ "%");
		}
		if (this.getBranch_id() != null && this.getBranch_id() > 0L) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" branch_id = %d", this.getBranch_id());
		}

		if (this.getDepartment_id() != null && this.getDepartment_id() > 0L) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" department_id = %d",
							this.getDepartment_id());
		}
		
		if(!Validation.isEmptyLong(getPosition_id())){
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" position_id = %d",
							getPosition_id());
		}
		return condition;
	}
	
	private String[] getBranchIds(){
		if(appContext != null){
			
		}
		return null;
	}
}
