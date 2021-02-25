package general.tables.report;

import java.util.List;

import general.tables.Role;
import general.tables.User;

public class HrReport1 extends User {

	private String staffName;
	private List<Role> roles;

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
