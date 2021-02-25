package general.tables;

import general.Validation;

import java.io.Serializable;
import java.lang.Long;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: InvoiceItem
 *
 */
@Entity
@Table(name = "invoice_table_item1")
@javax.persistence.SequenceGenerator(name = "seq_invoice_item_id", sequenceName = "seq_invoice_item_id", allocationSize = 1)
public class InvoiceItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1485136870565273589L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invoice_item_id")
	private Long id;

	private Long invoice_id;
	private Long matnr;
	private Double quantity;
	private String barcode;
	private Double done_quantity;
	private String note;
	private String ml_ids;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public InvoiceItem() {
		super();
		setQuantity(0D);
		this.done_quantity = 0D;
	}

	public InvoiceItem(Long invoice_id, Long matnr, Double quantity,
			String barcode) {
		super();
		this.invoice_id = invoice_id;
		this.matnr = matnr;
		this.quantity = quantity;
		this.barcode = barcode;
		this.done_quantity = 0D;
	}

	public String[] getParsedBarcodes() {
		if (!Validation.isEmptyString(getBarcode())) {
			return getBarcode().split(",");
		}
		return null;
	}

	public boolean hasBarcode() {
		// return !Validation.isEmptyString(getBarcode());
		return getBarcodesList().size() > 0;
	}

	public List<String> getParsedBarcodesAsList() {
		// System.out.println("BARCODE: " + getBarcode());
		List<String> out = new ArrayList<String>();
		if (!Validation.isEmptyString(getBarcode())) {
			for (String s : getBarcode().split(",")) {
				out.add(s);
			}
		}
		return out;
	}

	public Double getDone_quantity() {
		return done_quantity;
	}

	public void setDone_quantity(Double done_quantity) {
		this.done_quantity = done_quantity;
	}
	
	public String getMl_ids() {
		return ml_ids;
	}

	public void setMl_ids(String ml_ids) {
		this.ml_ids = ml_ids;
	}

	@Transient
	public String[] getMlIdsAsArray(){
		if(Validation.isEmptyString(ml_ids)){
			return null;
		}
		
		return ml_ids.split(",");
	}


	@Transient
	private Invoice invoiceObject;

	public Invoice getInvoiceObject() {
		return invoiceObject;
	}

	public void setInvoiceObject(Invoice invoiceObject) {
		this.invoiceObject = invoiceObject;
	}

	@Transient
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

	@Transient
	List<String> barcodesList = new ArrayList<String>();

	public List<String> getBarcodesList() {
		return barcodesList;
	}

	public void setBarcodesList(List<String> barcodesList) {
		this.barcodesList = barcodesList;
	}

}
