package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the WRITEOFF_REPAIR database table.
 * 
 */
@Entity
@Table(name = "WRITEOFF_REPAIR")
@NamedQuery(name = "WriteoffRepair.findAll", query = "SELECT w FROM WriteoffRepair w")
public class WriteoffRepair implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "WRITEOFF_REPAIR_ID_GENERATOR", sequenceName = "SEQ_WRITEOFF_REPAIR_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WRITEOFF_REPAIR_ID_GENERATOR")
	private Long id;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "\"DATE\"")
	private Date date;

	@Column(name = "FROM_WERKS")
	private Long fromWerks;

	@Column(name = "NOTE2")
	private String note;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "STATUS_ID")
	private int statusId;

	@Column(name = "MATNR_LIST_ID")
	private Long matnrListId;

	@Column(name = "MASTER_ID")
	private Long masterId;

	private String barcode;

	// bi-directional many-to-one association to WriteoffRepairItem
	@OneToMany(mappedBy = "writeoffRepair", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<WriteoffRepairItem> writeoffRepairItems = new HashSet<WriteoffRepairItem>();

	public WriteoffRepair() {
		this.statusId = 0;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBukrs() {
		return this.bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
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

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getFromWerks() {
		return this.fromWerks;
	}

	public void setFromWerks(Long fromWerks) {
		this.fromWerks = fromWerks;
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

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public Set<WriteoffRepairItem> getWriteoffRepairItems() {
		return this.writeoffRepairItems;
	}

	public void setWriteoffRepairItems(Set<WriteoffRepairItem> writeoffRepairItems) {
		this.writeoffRepairItems = writeoffRepairItems;
	}

	public WriteoffRepairItem addWriteoffRepairItem(WriteoffRepairItem writeoffRepairItem) {
		if (writeoffRepairItem != null && !getWriteoffRepairItems().contains(writeoffRepairItem)) {
			getWriteoffRepairItems().add(writeoffRepairItem);
			writeoffRepairItem.setWriteoffRepair(this);
		}
		return writeoffRepairItem;
	}

	public WriteoffRepairItem removeWriteoffRepairItem(WriteoffRepairItem writeoffRepairItem) {
		getWriteoffRepairItems().remove(writeoffRepairItem);
		writeoffRepairItem.setWriteoffRepair(null);

		return writeoffRepairItem;
	}

	public Long getMatnrListId() {
		return matnrListId;
	}

	public void setMatnrListId(Long matnrListId) {
		this.matnrListId = matnrListId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "MATNR")
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "MASTER_ID",referencedColumnName="STAFF_ID",insertable=false,updatable=false)
	private Staff master;

	public Staff getMaster() {
		return master;
	}

	public void setMaster(Staff master) {
		this.master = master;
	}

	public static final Integer STATUS_NEW = 1;
	public static final Integer STATUS_DONE = 2;

	public static final String CONTEXT = "log_wr";

	@Transient
	private Staff creator = new Staff();

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}

	@Transient
	private String matnrName;

	public String getMatnrName() {
		return matnrName;
	}

	public void setMatnrName(String matnrName) {
		this.matnrName = matnrName;
	}

}