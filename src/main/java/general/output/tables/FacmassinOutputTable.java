package general.output.tables;

import java.util.Date;

public class FacmassinOutputTable {
		private int index;
		private Long contract_number;	
		private Date contract_date;
		private String clientFio;
		private String collectorFio;
		private String status;
		private String type;		
		private String waers = "";
		private double price;
		private double dmbtr;
		private double dmbtr_wrbtr;
		private double wrbtr_dmbtr;
		private double wrbtr;
		private Long payment_schedule_id;
		private Date payment_date;
		private double paid;
		private double payment_due;
		private boolean first_payment;
		private boolean error = true;
		private String error_message;
		private Long customer_id;
		private Long awkey;
		private Long matnr;
		private Long old_sn;
		private String iptoo_hkont="";
		private String iptoo_waers="";
		private double iptoo_dmbtr=0;
		private double iptoo_wrbtr=0;
		private double iptoo_summa=0;
		
		
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public Date getPayment_date() {
			return payment_date;
		}
		public void setPayment_date(Date payment_date) {
			this.payment_date = payment_date;
		}
		public Long getPayment_schedule_id() {
			return payment_schedule_id;
		}
		public void setPayment_schedule_id(Long payment_schedule_id) {
			this.payment_schedule_id = payment_schedule_id;
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
		public Long getContract_number() {
			return contract_number;
		}
		public void setContract_number(Long contract_number) {
			this.contract_number = contract_number;
		}
		public Date getContract_date() {
			return contract_date;
		}
		public void setContract_date(Date contract_date) {
			this.contract_date = contract_date;
		}
		public String getClientFio() {
			return clientFio;
		}
		public void setClientFio(String clientFio) {
			this.clientFio = clientFio;
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
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public boolean isFirst_payment() {
			return first_payment;
		}
		public void setFirst_payment(boolean first_payment) {
			this.first_payment = first_payment;
		}
		public double getDmbtr_wrbtr() {
			return dmbtr_wrbtr;
		}
		public void setDmbtr_wrbtr(double dmbtr_wrbtr) {
			this.dmbtr_wrbtr = dmbtr_wrbtr;
		}
		public double getWrbtr_dmbtr() {
			return wrbtr_dmbtr;
		}
		public void setWrbtr_dmbtr(double wrbtr_dmbtr) {
			this.wrbtr_dmbtr = wrbtr_dmbtr;
		}
		public Long getAwkey() {
			return awkey;
		}
		public void setAwkey(Long awkey) {
			this.awkey = awkey;
		}
		public Long getMatnr() {
			return matnr;
		}
		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}
		public String getError_message() {
			return error_message;
		}
		public void setError_message(String error_message) {
			this.error_message = error_message;
		}
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public Long getOld_sn() {
			return old_sn;
		}
		public void setOld_sn(Long old_sn) {
			this.old_sn = old_sn;
		}
		public String getIptoo_hkont() {
			return iptoo_hkont;
		}
		public void setIptoo_hkont(String iptoo_hkont) {
			this.iptoo_hkont = iptoo_hkont;
		}
		public String getIptoo_waers() {
			return iptoo_waers;
		}
		public void setIptoo_waers(String iptoo_waers) {
			this.iptoo_waers = iptoo_waers;
		}
		public double getIptoo_dmbtr() {
			return iptoo_dmbtr;
		}
		public void setIptoo_dmbtr(double iptoo_dmbtr) {
			this.iptoo_dmbtr = iptoo_dmbtr;
		}
		public double getIptoo_wrbtr() {
			return iptoo_wrbtr;
		}
		public void setIptoo_wrbtr(double iptoo_wrbtr) {
			this.iptoo_wrbtr = iptoo_wrbtr;
		}
		public double getIptoo_summa() {
			return iptoo_summa;
		}
		public void setIptoo_summa(double iptoo_summa) {
			this.iptoo_summa = iptoo_summa;
		}
		 

	
}
