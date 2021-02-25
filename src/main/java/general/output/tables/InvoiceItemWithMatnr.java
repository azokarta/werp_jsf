package general.output.tables;

public class InvoiceItemWithMatnr {

	private Long invoice_item_id;
	private Long matnr;
	private Long parent_matnr;
	private String matnr_name;
	private double menge = 0;
	private int matnr_type;
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
	public double getMenge() {
		return menge;
	}
	public void setMenge(double menge) {
		this.menge = menge;
	}
	public int getMatnr_type() {
		return matnr_type;
	}
	public void setMatnr_type(int matnr_type) {
		this.matnr_type = matnr_type;
	}
	public String getMatnr_name() {
		return matnr_name;
	}
	public void setMatnr_name(String matnr_name) {
		this.matnr_name = matnr_name;
	}
	public Long getParent_matnr() {
		return parent_matnr;
	}
	public void setParent_matnr(Long parent_matnr) {
		this.parent_matnr = parent_matnr;
	}
	
}
