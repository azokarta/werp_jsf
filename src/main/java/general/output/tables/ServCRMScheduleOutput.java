package general.output.tables;

import general.tables.Contract;
import general.tables.Customer;
import general.tables.ServCRMSchedule;
import general.tables.Staff;

public class ServCRMScheduleOutput {
	public ServCRMScheduleOutput(int i) {
		this.index = i;
		this.scs = new ServCRMSchedule();
		this.staff = new Staff();
		this.contract = new Contract();
		this.customer = new Customer();
	}
	
	public int index;
	private ServCRMSchedule scs;
	private Staff staff;
	private Contract contract;
	private Customer customer;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public ServCRMSchedule getScs() {
		return scs;
	}
	public void setScs(ServCRMSchedule scs) {
		this.scs = scs;
	}
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
