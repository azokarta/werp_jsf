package general.output.tables;

public class CashBankAccountStatement {
	public CashBankAccountStatement()
	{
		
	}
	private int index = 0;
	private String hkont = "";
	private String hkont_name = "";
	private String waers = "";
	private double amount = 0; 
	private Long customer_id;
	
	public String getHkont() {
		return hkont;
	}
	public void setHkont(String hkont) {
		this.hkont = hkont;
	}
	public String getHkont_name() {
		return hkont_name;
	}
	public void setHkont_name(String hkont_name) {
		this.hkont_name = hkont_name;
	}
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
