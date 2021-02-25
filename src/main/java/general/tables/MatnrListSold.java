package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity 
@Table(name = "matnr_list_sold")
@javax.persistence.SequenceGenerator(name="seq_matnr_list_sold_id",sequenceName="seq_matnr_list_sold_id",allocationSize=1)
public class MatnrListSold  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_matnr_list_sold_id")
	@Column(name = "mls_id")
	private Long mls_id;
	
	@Column(name = "matnr_list_id")
	private Long matnr_list_id;
	 
	@Column(name = "bukrs")
	private String bukrs; 
	 
	@Column(name = "gjahr")
	private int gjahr;

	@Column(name = "matnr")
	private Long matnr;
	
	@Column(name = "werks")
	private Long werks;
	
	@Column(name = "barcode")
	private String barcode;
	
	@Column(name = "dmbtr")
	private double dmbtr;
	
	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "invoice")
	private Long invoice;
	
	@Column(name = "awkey")
	private Long awkey;
	
	@Column(name = "menge")
	private double menge;
	
	@Column(name = "status")
	private String status;
	
	private Long customer_id;
	private Date created_date;
	private Long created_by;
	private Long writeoff_doc;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getMenge() {
		return menge;
	}
	public void setMenge(double menge) {
		this.menge = menge;
	}
	public Long getMatnr_list_id() {
		return matnr_list_id;
	}
	public void setMatnr_list_id(Long matnr_list_id) {
		this.matnr_list_id = matnr_list_id;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public int getGjahr() {
		return gjahr;
	}
	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public Long getWerks() {
		return werks;
	}
	public void setWerks(Long werks) {
		this.werks = werks;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	
	public Long getInvoice() {
		return invoice;
	}
	public void setInvoice(Long invoice) {
		this.invoice = invoice;
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
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Long getCreated_by() {
		return created_by;
	}
	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}
	public Long getWriteoff_doc() {
		return writeoff_doc;
	}
	public void setWriteoff_doc(Long writeoff_doc) {
		this.writeoff_doc = writeoff_doc;
	}
	public Long getMls_id() {
		return mls_id;
	}
	
	public MatnrListSold(){
		super();
	}
	
	public MatnrListSold(Long matnr_list_id, String bukrs, int gjahr,
			Long matnr, Long werks, String barcode, double dmbtr,
			Long staff_id, Long invoice, Long awkey, double menge,
			String status, Long customer_id, Date created_date,
			Long created_by, Long writeoff_doc) {
		super();
		this.matnr_list_id = matnr_list_id;
		this.bukrs = bukrs;
		this.gjahr = gjahr;
		this.matnr = matnr;
		this.werks = werks;
		this.barcode = barcode;
		this.dmbtr = dmbtr;
		this.staff_id = staff_id;
		this.invoice = invoice;
		this.awkey = awkey;
		this.menge = menge;
		this.status = status;
		this.customer_id = customer_id;
		this.created_date = created_date;
		this.created_by = created_by;
		this.writeoff_doc = writeoff_doc;
	}



	@Transient
	private Matnr matnrObject;
	public Matnr getMatnrObject() {
		return matnrObject;
	}
	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}
	
	
}
