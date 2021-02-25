package crm.tables;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import crm.constants.DemoConstants;
import general.Validation;
import general.tables.Staff;

import java.util.Date;

/**
 * The persistent class for the CRM_DOC_DEMO database table.
 * 
 */
@Entity
@Table(name = "CRM_DOC_DEMO")
@NamedQuery(name = "CrmDocDemo.findAll", query = "SELECT c FROM CrmDocDemo c")
public class CrmDocDemo implements Serializable, CrmDoc {
	private static final long serialVersionUID = 1L;

	private String address;

	@Column(name = "APPOINTED_BY")
	private Long appointedBy;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CALL_DATE")
	private Date callDate;

	@Column(name = "CALL_ID")
	private Long callId;

	@Column(name = "CLIENT_NAME")
	private String clientName;

	@Column(name = "CONTRACT_NUMBER")
	private Long contractNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT", updatable = false)
	private Date createdAt;

	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_TIME")
	private Date dateTime;

	@Column(name = "DEALER_ID")
	private Long dealerId;

	@Id
	@SequenceGenerator(name = "CRM_DOC_DEMO_ID_GENERATOR", sequenceName = "SEQ_CRM_DOC_DEMO_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_DOC_DEMO_ID_GENERATOR")
	@Column(updatable = false)
	private Long id;

	@Column(name = "LOCATION_ID")
	private Integer locationId;

	private String note;

	@Column(name = "PARENT_ID")
	private Long parentId;

	@Column(name = "RECO_COUNT")
	private int recoCount;

	@Column(name = "RECO_ID")
	private Long recoId;

	@Column(name = "RESULT_ID")
	private Integer resultId;

	@Column(name = "REASON_ID")
	private Long reasonId;

	@Temporal(TemporalType.DATE)
	@Column(name = "SALE_DATE")
	private Date saleDate;

	@Column(name = "TREE_ID")
	private Long treeId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Version
	@Column(name = "\"VERSION\"")
	private Integer version;

	private String context;
	@Column(name = "CONTEXT_ID")
	private Long contextId;

	@Column(name = "VISIT_ID")
	private Long visitId;

	public CrmDocDemo() {
		setRecoId(0L);
		setVisitId(0L);
		setCallId(0L);
		setResultId(DemoConstants.RESULT_UNKNOWN);
		setLocationId(0);
		setParentId(0L);
		setTreeId(0L);
		setCustomerId(0L);
		setContractNumber(0L);
		setReasonId(0L);
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getAppointedBy() {
		return appointedBy;
	}

	public void setAppointedBy(Long appointedBy) {
		this.appointedBy = appointedBy;
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

	public Date getCallDate() {
		return this.callDate;
	}

	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}

	public Long getCallId() {
		return this.callId;
	}

	public void setCallId(Long callId) {
		this.callId = callId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
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

	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Long getDealerId() {
		return this.dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
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

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public int getRecoCount() {
		return this.recoCount;
	}

	public void setRecoCount(int recoCount) {
		this.recoCount = recoCount;
	}

	public Long getRecoId() {
		return this.recoId;
	}

	public void setRecoId(Long recoId) {
		this.recoId = recoId;
	}

	public Integer getResultId() {
		return this.resultId;
	}

	public void setResultId(Integer resultId) {
		this.resultId = resultId;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public Date getSaleDate() {
		return this.saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public Long getTreeId() {
		return this.treeId;
	}

	public void setTreeId(Long treeId) {
		this.treeId = treeId;
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

	public Long getVisitId() {
		return this.visitId;
	}

	public void setVisitId(Long visitId) {
		this.visitId = visitId;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "DEALER_ID", insertable = false, updatable = false)
	private Staff dealer;

	public Staff getDealer() {
		return dealer;
	}

	public void setDealer(Staff dealer) {
		this.dealer = dealer;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "APPOINTED_BY", insertable = false, updatable = false)
	private Staff appointer;

	public Staff getAppointer() {
		return appointer;
	}

	public void setAppointer(Staff appointer) {
		this.appointer = appointer;
	}

//	@OneToOne(fetch = FetchType.LAZY)
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "RECO_ID", insertable = false, updatable = false)
	@Transient
	private CrmDocReco parentReco;

	public CrmDocReco getParentReco() {
		return parentReco;
	}

	public void setParentReco(CrmDocReco parentReco) {
		this.parentReco = parentReco;
	}

//	@OneToOne(fetch = FetchType.LAZY)
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JoinColumn(name = "PARENT_ID", insertable = false, updatable = false)
//	private CrmDocDemo parentDemo;
//
//	public CrmDocDemo getParentDemo() {
//		return parentDemo;
//	}
//
//	public void setParentDemo(CrmDocDemo parentDemo) {
//		this.parentDemo = parentDemo;
//	}

	@Transient
	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}

	// @Transient
	// private CrmDocReco crmDocReco;
	//
	// public CrmDocReco getCrmDocReco() {
	// return crmDocReco;
	// }
	//
	// public void setCrmDocReco(CrmDocReco crmDocReco) {
	// this.crmDocReco = crmDocReco;
	// }

	@Transient
	private CrmDocVisit crmDocVisit;

	public CrmDocVisit getCrmDocVisit() {
		return crmDocVisit;
	}

	public void setCrmDocVisit(CrmDocVisit crmDocVisit) {
		this.crmDocVisit = crmDocVisit;
	}

	@Transient
	public String getResultName() {
		return DemoConstants.getAllResults().get(getResultId());
	}
}