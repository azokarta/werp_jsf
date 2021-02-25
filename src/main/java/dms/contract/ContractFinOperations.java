package dms.contract;

public class ContractFinOperations {
	private String operationInfo;
	private int oper_type;
	// 1 - Скидка рекомендателю
	// 2 - Вознаграждение рекомендателю
	// 3 - Скидка от рекомендателя новому клиенту
	// 4 - Скидка от дилера клиенту
	// 5 - Удержание от дилера за акционный товар
	private double wrbtr;
	private double dmbtr;
	private double rate;
	private String waers;
	private Long customer_id;
	private Long awkey;
	private Long contractNumber;
	private Long staff_id;
	private Long matnr;
	
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public String getOperationInfo() {
		return operationInfo;
	}
	public void setOperationInfo(String operationInfo) {
		this.operationInfo = operationInfo;
	}
	
	public Long getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(Long contractNumber) {
		this.contractNumber = contractNumber;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	public Long getAwkey() {
		return awkey;
	}
	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}
	public Long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
	
	public int getOper_type() {
		return oper_type;
	}
	public void setOper_type(int oper_type) {
		this.oper_type = oper_type;
	}
	public double getWrbtr() {
		return wrbtr;
	}
	public void setWrbtr(double wrbtr) {
		this.wrbtr = wrbtr;
	}
	public double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}	
	
}
