package dms.contract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.GeneralUtil;
import general.clone.Clone;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Customer;
import general.tables.PaymentSchedule;
import general.tables.PaymentTemplate;
import general.tables.PriceList;
import general.tables.Staff;

public class ContractDetails {
	
		public ContractDetails() {
			this.bukrs = new Bukrs();
			this.branch = new Branch();
			this.contract = new Contract();
			this.contrType = new ContractType();
			this.customer = new Customer();
			this.dealer = new Staff();
			this.demosec = new Staff();
			this.fitter = new Staff();
			this.collector = new Staff();
			this.priceList = new PriceList();
			this.bkpf = new Bkpf();
			this.psList = new ArrayList<PaymentSchedule>();

			this.price = 0;
			this.skidka = 0;
			this.paid = 0;
			this.remain = 0;
			this.repayment = 0;
			this.firstPayment = 0;
			
			this.setApprove(false);
		}
		
		public ContractDetails(int ind, Contract con) {
			this.index = ind;
			this.bukrs = new Bukrs();
			this.branch = new Branch();
			this.contrType = new ContractType();
			this.customer = new Customer();
			this.dealer = new Staff();
			this.demosec = new Staff();
			this.fitter = new Staff();
			this.collector = new Staff();
			this.priceList = new PriceList();
			this.bkpf = new Bkpf();
			this.psList = new ArrayList<PaymentSchedule>();

			if (con != null) this.price = con.getPrice();
			this.firstPayment = 0;
			this.skidka = 0;
			this.paid = 0;
			this.remain = 0;
			this.repayment = 0;
			
			this.setApprove(false);
			try {
				this.contract = con.clone();
			} catch(Exception ex) {
				GeneralUtil.addErrorMessage(ex.getMessage());
			}			
		}
	
		//*****************************************
		
		private int index;
		private Bukrs bukrs;
		private Branch branch;
		private Contract contract;
		private ContractType contrType;
		private Customer customer;
		private Staff dealer;
		private Staff demosec;
		private Staff collector;
		private Staff fitter;
		private PriceList priceList;
		private Bkpf bkpf;
		private List<PaymentSchedule> psList; 
		private String statusName;
		
		public double price;
		public double skidka;
		public double paid;
		public double remain;
		public double repayment;
		public double firstPayment;
		public String icon;
		private String info;
		private boolean approve;
		private String address;		
		private String tel;
		
		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public List<PaymentTemplate> paymentTempList;
		//*****************************************
		
		public List<PaymentTemplate> getPaymentTempList() {
			return paymentTempList;
		}

		public void setPaymentTempList(List<PaymentTemplate> paymentTempList) {
			this.paymentTempList = paymentTempList;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public double getFirstPayment() {
			return firstPayment;
		}

		public void setFirstPayment(double firstPayment) {
			this.firstPayment = firstPayment;
		}

		public List<PaymentSchedule> getPsList() {
			return psList;
		}

		public void setPsList(List<PaymentSchedule> psList) {
			this.psList = psList;
		}

		public Staff getCollector() {
			return collector;
		}

		public void setCollector(Staff collector) {
			this.collector = collector;
		}

		public double getRepayment() {
			return repayment;
		}

		public void setRepayment(double repayment) {
			this.repayment = repayment;
		}
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Bukrs getBukrs() {
			return bukrs;
		}
		public void setBukrs(Bukrs bukrs) {
			this.bukrs = bukrs;
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
		public ContractType getContrType() {
			return contrType;
		}
		public void setContrType(ContractType contrType) {
			this.contrType = contrType;
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
		public Staff getDemosec() {
			return demosec;
		}
		public void setDemosec(Staff demosec) {
			this.demosec = demosec;
		}
		public Staff getFitter() {
			return fitter;
		}
		public void setFitter(Staff fitter) {
			this.fitter = fitter;
		}
		public PriceList getPriceList() {
			return priceList;
		}
		public void setPriceList(PriceList priceList) {
			this.priceList = priceList;
		}
		public Bkpf getBkpf() {
			return bkpf;
		}
		public void setBkpf(Bkpf bkpf) {
			this.bkpf = bkpf;
		}
		public String getStatusName() {
			return statusName;
		}
		public void setStatusName(String statusName) {
			this.statusName = statusName;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public double getSkidka() {
			return skidka;
		}
		public void setSkidka(double skidka) {
			this.skidka = skidka;
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
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
		public boolean isApprove() {
			return approve;
		}
		public void setApprove(boolean approve) {
			this.approve = approve;
		}
}
