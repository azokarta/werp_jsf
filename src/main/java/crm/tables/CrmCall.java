package crm.tables;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import crm.constants.CallConstants;
import general.Validation;
import general.tables.Staff;

import java.util.Date;

/**
 * The persistent class for the CRM_CALL database table.
 * 
 */
@Entity
@Table(name = "CRM_CALL")
@NamedQuery(name = "CrmCall.findAll", query = "SELECT c FROM CrmCall c")
public class CrmCall implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "CALLER_ID")
	private Long callerId;

	@Column(name = "TREE_ID")
	private Long treeId;

	private String context;
	private String bukrs;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	@Column(name = "CONTEXT_ID")
	private Long contextId;

	@Column(name = "REASON_ID")
	private Long reasonId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT", updatable = false)
	private Date createdAt;

	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_TIME")
	private Date dateTime;

	@Id
	@SequenceGenerator(name = "CRM_CALL_ID_GENERATOR", sequenceName = "SEQ_CRM_CALL_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_CALL_ID_GENERATOR")
	@Column(updatable = false)
	private Long id;

	private String note;

	@Column(name = "RESULT_ID")
	private Integer resultId;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RECALL_DATE_TIME")
	private Date recallDateTime;

	@Version
	@Column(name = "\"VERSION\"")
	private Integer version;

	public CrmCall() {
		setStatusId(0);
		setReasonId(0L);
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getCallerId() {
		return this.callerId;
	}

	public void setCallerId(Long callerId) {
		this.callerId = callerId;
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Long getContextId() {
		return this.contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
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

	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getResultId() {
		return resultId;
	}

	public void setResultId(Integer resultId) {
		this.resultId = resultId;
	}

	public Date getRecallDateTime() {
		return recallDateTime;
	}

	public void setRecallDateTime(Date recallDateTime) {
		this.recallDateTime = recallDateTime;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public Long getTreeId() {
		return treeId;
	}

	public void setTreeId(Long treeId) {
		this.treeId = treeId;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "CALLER_ID", insertable = false, updatable = false)
	private Staff caller;

	public Staff getCaller() {
		return caller;
	}

	public void setCaller(Staff caller) {
		this.caller = caller;
	}

	@Transient
	private CrmDocReco crmDocReco;

	public CrmDocReco getCrmDocReco() {
		return crmDocReco;
	}

	public void setCrmDocReco(CrmDocReco crmDocReco) {
		this.crmDocReco = crmDocReco;
	}

	@Transient
	private Staff manager;

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}

	@Transient
	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}

	@Transient
	public String getResultName() {
		return CallConstants.getResults().get(getResultId());
	}
}