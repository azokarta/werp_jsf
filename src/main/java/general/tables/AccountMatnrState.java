package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the ACCOUNT_MATNR_STATE database table.
 * 
 */
@Entity
@Table(name = "ACCOUNT_MATNR_STATE")
@NamedQuery(name = "AccountMatnrState.findAll", query = "SELECT a FROM AccountMatnrState a")
public class AccountMatnrState implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Id
	@SequenceGenerator(name = "ACCOUNT_MATNR_STATE_ID_GENERATOR", sequenceName = "SEQ_ACCOUNT_MATNR_STATE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_MATNR_STATE_ID_GENERATOR")
	private Long id;

	@Column(name = "INVOICE_ID")
	private Long invoiceId;

	private Long matnr;

	@Column(name = "MLS_MATNR_ID")
	private Long mlsMatnrId;

	private String note;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	private String barcode;

	public AccountMatnrState() {
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInvoiceId() {
		return this.invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Long getMatnr() {
		return this.matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Long getMlsMatnrId() {
		return this.mlsMatnrId;
	}

	public void setMlsMatnrId(Long mlsMatnrId) {
		this.mlsMatnrId = mlsMatnrId;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
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