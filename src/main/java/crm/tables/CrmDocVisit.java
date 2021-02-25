package crm.tables;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import general.Validation;
import general.tables.Staff;

import java.util.Date;

/**
 * The persistent class for the CRM_DOC_VISIT database table.
 * 
 */
@Entity
@Table(name = "CRM_DOC_VISIT")
@NamedQuery(name = "CrmDocVisit.findAll", query = "SELECT c FROM CrmDocVisit c")
public class CrmDocVisit implements Serializable, CrmDoc {
	private static final long serialVersionUID = 1L;

	private String address;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Column(name = "CONTRACT_NUMBER")
	private Long contractNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	@Temporal(TemporalType.DATE)
	@Column(name = "DOC_DATE")
	private Date docDate;

	@Id
	@SequenceGenerator(name = "CRM_DOC_VISIT_ID_GENERATOR", sequenceName = "SEQ_CRM_DOC_VISIT_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_DOC_VISIT_ID_GENERATOR")
	@Column(updatable = false)
	private Long id;

	@Column(name = "client_name")
	private String clientName;

	@Column(name = "LOCATION_ID")
	private Integer locationId;

	private String note;

	@Column(name = "RECO_COUNT")
	private int recoCount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Version
	@Column(name = "\"VERSION\"")
	private Integer version;

	@Column(name = "VISITOR_ID")
	private Long visitorId;

	public CrmDocVisit() {
		setDocDate(new Date());
		setCustomerId(0L);
		setContractNumber(0L);
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Long getContractNumber() {
		return this.contractNumber;
	}

	public void setContractNumber(Long contractNumber) {
		this.contractNumber = contractNumber;
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

	public Long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Date getDocDate() {
		return this.docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getRecoCount() {
		return this.recoCount;
	}

	public void setRecoCount(int recoCount) {
		this.recoCount = recoCount;
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

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(Long visitorId) {
		this.visitorId = visitorId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "VISITOR_ID", insertable = false, updatable = false)
	private Staff visitor;

	public Staff getVisitor() {
		return visitor;
	}

	public void setVisitor(Staff visitor) {
		this.visitor = visitor;
	}

	@Transient
	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}

}