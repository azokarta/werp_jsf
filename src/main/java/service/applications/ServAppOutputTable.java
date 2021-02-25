package service.applications;

import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.ServiceApplication;

public class ServAppOutputTable {

	public ServAppOutputTable() {
		this.servApp = new ServiceApplication();
		this.company = new Bukrs();
		this.branch = new Branch();
		this.contract = new Contract();
		this.customer = new Customer();
		this.customer_fio = "";
		this.user_fio  = "";
		this.contr_num = "";
	}
	
	public ServAppOutputTable(int new_index) {
		this.index = new_index;
		this.servApp = new ServiceApplication();
		this.company = new Bukrs();
		this.branch = new Branch();
		this.contract = new Contract();
		this.customer = new Customer();
		this.customer_fio = "";
		this.user_fio = "";
		this.contr_num = "";
	}

	int index;
	ServiceApplication servApp;
	Bukrs company;
	Branch branch;
	Contract contract;
	Customer customer;
	String customer_fio;
	String user_fio;
	String contr_num;
	
	public String getContr_num() {
		return contr_num;
	}

	public void setContr_num(String contr_num) {
		this.contr_num = contr_num;
	}

	public String getUser_fio() {
		return user_fio;
	}

	public void setUser_fio(String user_fio) {
		this.user_fio = user_fio;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustomer_fio() {
		return customer_fio;
	}

	public void setCustomer_fio(String customer_fio) {
		this.customer_fio = customer_fio;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ServiceApplication getServApp() {
		return servApp;
	}

	public void setServApp(ServiceApplication servApp) {
		this.servApp = servApp;
	}

	public Bukrs getCompany() {
		return company;
	}

	public void setCompany(Bukrs company) {
		this.company = company;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
		
	
}
