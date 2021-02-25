package service;

import general.tables.ServiceTable;
import general.tables.Staff;

public class ServiceTableOutput {
	
	public ServiceTableOutput() {
		// TODO Auto-generated constructor stub
		this.service = new ServiceTable();
		this.master = new Staff();
		this.oper = new Staff();
	}
	
	public ServiceTableOutput(int ind) {
		// TODO Auto-generated constructor stub
		this.index = ind;
		this.service = new ServiceTable();
		this.master = new Staff();
		this.oper = new Staff();
	}
	
	public int index;
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	private ServiceTable service;
	private Staff master;
	private Staff oper;
	public ServiceTable getService() {
		return service;
	}
	public void setService(ServiceTable service) {
		this.service = service;
	}
	public Staff getMaster() {
		return master;
	}
	public void setMaster(Staff master) {
		this.master = master;
	}
	public Staff getOper() {
		return oper;
	}
	public void setOper(Staff oper) {
		this.oper = oper;
	}
	
}
