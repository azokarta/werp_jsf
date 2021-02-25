package general.output.tables;

import general.tables.ServCRMHistory;
import general.tables.Staff;

public class ServCRMHistoryOutput {
	public ServCRMHistoryOutput(int i) {
		this.index = i;
		this.sch = new ServCRMHistory();
		this.staff = new Staff();
	}
	
	public int index;
	private ServCRMHistory sch;
	private Staff staff;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public ServCRMHistory getSch() {
		return sch;
	}
	public void setSch(ServCRMHistory sch) {
		this.sch = sch;
	}
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	
	
	
}
