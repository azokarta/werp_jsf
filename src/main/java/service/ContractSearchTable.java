package service;

import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.Staff;

import java.util.Date;

public class ContractSearchTable {
	
	public ContractSearchTable() {
		// TODO Auto-generated constructor stub
		this.contract = new Contract();
		this.customer = new Customer();
		this.dealer = new Staff();
		this.collector = new Staff();
		this.company = new Bukrs();
		this.branch = new Branch();
	}

	public ContractSearchTable(int new_index) {
		// TODO Auto-generated constructor stub
		this.contract = new Contract();
		this.customer = new Customer();
		this.dealer = new Staff();
		this.collector = new Staff();
		this.company = new Bukrs();
		this.branch = new Branch();
		this.index = new_index;
	}
	
	public int index;
	private Contract contract;
	private Customer customer;
	private Staff dealer;
	private Staff collector;
	private Bukrs company;
	private Branch branch;
	
	private String clientFio;
	private String dealerFio;
	private String collectorFio;
	private String status;
	private String type;
	private double payment_due;
	private double price;
	private double paid;
	private double remain;
	private double dmbtr;
	private double wrbtr;
	private String address;
	private String tel;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public Staff getDealer() {
		return dealer;
	}
	public void setDealer(Staff dealer) {
		this.dealer = dealer;
	}
	public Staff getCollector() {
		return collector;
	}
	public void setCollector(Staff collector) {
		this.collector = collector;
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
	public String getClientFio() {
		return clientFio;
	}
	public void setClientFio(String clientFio) {
		this.clientFio = clientFio;
	}
	public String getDealerFio() {
		return dealerFio;
	}
	public void setDealerFio(String dealerFio) {
		this.dealerFio = dealerFio;
	}
	public String getCollectorFio() {
		return collectorFio;
	}
	public void setCollectorFio(String collectorFio) {
		this.collectorFio = collectorFio;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getPayment_due() {
		return payment_due;
	}
	public void setPayment_due(double payment_due) {
		this.payment_due = payment_due;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}
	public double getRemain() {
		return remain;
	}
	public void setRemain(double remain) {
		this.remain = remain;
	}
	public double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}
	public double getWrbtr() {
		return wrbtr;
	}
	public void setWrbtr(double wrbtr) {
		this.wrbtr = wrbtr;
	}


}
