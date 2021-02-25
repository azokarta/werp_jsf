package general.output.tables;


public class InvoiceListItemFkage {
	public InvoiceListItemFkage() {

	}
	private String bukrs;
	private Long invoice_id;
	private Long invoice_item_id;
	private Long matnr;
	private Long parent_matnr;
	private String matnr_name;
	private double unit_price;
	private double total_price;
	private double menge;
	private String waers;
	private Long customer_id;
	private Long order_id;
	private int matnr_type;
	
	public Long getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
	}
	public Long getInvoice_item_id() {
		return invoice_item_id;
	}
	public void setInvoice_item_id(Long invoice_item_id) {
		this.invoice_item_id = invoice_item_id;
	}
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public String getMatnr_name() {
		return matnr_name;
	}
	public void setMatnr_name(String matnr_name) {
		this.matnr_name = matnr_name;
	}
	public double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}
	public double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}
	public double getMenge() {
		return menge;
	}
	public void setMenge(double menge) {
		this.menge = menge;
	}
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public Long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
	public Long getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public int getMatnr_type() {
		return matnr_type;
	}
	public void setMatnr_type(int matnr_type) {
		this.matnr_type = matnr_type;
	}
	public Long getParent_matnr() {
		return parent_matnr;
	}
	public void setParent_matnr(Long parent_matnr) {
		this.parent_matnr = parent_matnr;
	}
	
}
