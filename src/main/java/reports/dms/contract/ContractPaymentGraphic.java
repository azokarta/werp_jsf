package reports.dms.contract;

import general.GeneralUtil;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.PaymentSchedule;
import general.tables.Staff;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContractPaymentGraphic implements Comparable<ContractPaymentGraphic> {

	public ContractPaymentGraphic() {
		// TODO Auto-generated constructor stub
		this.contract = new Contract();
		this.branch = new Branch();
		this.customer = new Customer();
		this.dealer = new Staff();
		this.psL = new ArrayList<PaymentSchedule>();
		this.pdate = new ArrayList<String>();
	}

	public ContractPaymentGraphic(Contract a_con) {
		// TODO Auto-generated constructor stub
		this.contract = a_con;
		this.branch = new Branch();
		this.customer = new Customer();
		this.dealer = new Staff();
		this.psL = new ArrayList<PaymentSchedule>();
		this.pdate = new ArrayList<String>();
	}
	
	private Contract contract;
	private Branch branch;
	private Customer customer;
	private String bday;
	private Staff dealer;
	private String currency;
	public List<PaymentSchedule> psL;
	public String address;
	public String tel;
	public String rab;
	public String data_vyd;
	public List<String> pdate;

	public String getBday() {
		return bday;
	}

	public void setBday(String bday) {
		this.bday = bday;
	}
	
	public String getRab() {
		return rab;
	}

	public void setRab(String rab) {
		this.rab = rab;
	}
	
	public List<String> getPdate() {
		return pdate;
	}

	public void setPdate(List<String> pdate) {
		this.pdate = pdate;
	}

	public String getData_vyd() {
		return data_vyd;
	}

	public void setData_vyd(String data_vyd) {
		this.data_vyd = data_vyd;
	}

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

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<PaymentSchedule> getPsL() {
		return psL;
	}

	public void setPsL(List<PaymentSchedule> psL) {
		this.psL = psL;
	}

	@Override
	public int compareTo(ContractPaymentGraphic o) {
		Calendar fd = Calendar.getInstance();
		Calendar ld = Calendar.getInstance();
		fd.setTime(GeneralUtil.getFirstDateOfMonth(fd.getTime()));
		ld.setTime(GeneralUtil.getFirstDateOfMonth(ld.getTime()));
		
		int i=0;
		while (psL.get(i).getPayment_date().getTime() < fd.getTime().getTime()) i++;
		
		int j=0;
		while (o.getPsL().get(j).getPayment_date().getTime() < fd.getTime().getTime()) j++;
		
		if (i == psL.size()) i--; 
		if (j == o.getPsL().size()) j--;
		return psL.get(i).getPayment_date().compareTo(o.getPsL().get(j).getPayment_date());
	}	
		
}
