package crm.tables;

import java.io.Serializable;
import javax.persistence.*;

import crm.constants.RecoConstants;
import general.Validation;
import general.tables.Staff;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the CRM_DOC_RECO database table.
 * 
 */
@Entity
@Table(name = "CRM_DOC_RECO")
@NamedQuery(name = "CrmDocReco.findAll", query = "SELECT c FROM CrmDocReco c")
public class CrmDocReco implements Serializable, CrmDoc {
	private static final long serialVersionUID = 1L;

	public final static Integer STATUS_NEW = 0;
	public final static String CONTEXT = "crm_doc_reco";

	private String address;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CALL_DATE")
	private Date callDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "CALL_TIME_FROM")
	private Date callTimeFrom;

	@Temporal(TemporalType.DATE)
	@Column(name = "CALL_TIME_TO")
	private Date callTimeTo;

	@Column(name = "CALLER_IS_DEALER")
	private int callerIsDealer;

	@Column(name = "CONTRACT_NUMBER")
	private Long contractNumber;

	@Temporal(TemporalType.DATE)
	@Column(name = "CREATED_AT", updatable = false)
	private Date createdAt;

	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	@Column(name = "DEMO_ID")
	private Long demoId;

	@Temporal(TemporalType.DATE)
	@Column(name = "DOC_DATE")
	private Date docDate;

	@Column(name = "CLIENT_NAME")
	private String clientName;

	@Column(name = "HAS_ANIMAL")
	private int hasAnimal;

	@Column(name = "HAS_CHILD")
	private int hasChild;

	private String profession;

	@Column(name = "RELATIVE_ID")
	private Long relativeId;

	@Column(name = "TEMP_RECOMMENDER")
	private String tempRecommender;

	@Column(name = "HAS_ALLERGY")
	private int hasAllergy;

	@Column(name = "HAS_ASTHMA")
	private int hasAsthma;

	private String district;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Id
	@SequenceGenerator(name = "CRM_DOC_RECO_ID_GENERATOR", sequenceName = "SEQ_CRM_DOC_RECO_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_DOC_RECO_ID_GENERATOR")
	@Column(updatable = false)
	private Long id;

	@Column(name = "LOCATION_ID")
	private Integer locationId;

	private String note;

	@Column(name = "OWNER_BRANCH_ID")
	private Long ownerBranchId;

	@Column(name = "OWNER_ID")
	private Long ownerId;

	@Column(name = "PARENT_ID")
	private Long parentId;

	@Column(name = "RESPONSIBLE_ID")
	private Long responsibleId;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Column(name = "TREE_ID")
	private Long treeId;

	@Temporal(TemporalType.DATE)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Version
	@Column(name = "\"VERSION\"")
	private Integer version;

	@Column(name = "VISIT_ID")
	private Long visitId;

	public CrmDocReco() {
		setHasAnimal(0);
		setHasChild(0);
		setParentId(0L);
		setTreeId(0L);
		setVisitId(0L);
		setDemoId(0L);
		setVisitId(0L);
		setCustomerId(0L);
		setContractNumber(0L);
		setStatusId(STATUS_NEW);
		setLocationId(0);
		setCallerIsDealer(0);
		setDocDate(Calendar.getInstance().getTime());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		setCallDate(cal.getTime());
		this.addPhone(new CrmPhone());
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

	public Date getCallDate() {
		return this.callDate;
	}

	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}

	public Date getCallTimeFrom() {
		return this.callTimeFrom;
	}

	public void setCallTimeFrom(Date callTimeFrom) {
		this.callTimeFrom = callTimeFrom;
	}

	public Date getCallTimeTo() {
		return this.callTimeTo;
	}

	public void setCallTimeTo(Date callTimeTo) {
		this.callTimeTo = callTimeTo;
	}

	public int getCallerIsDealer() {
		return this.callerIsDealer;
	}

	public void setCallerIsDealer(int callerIsDealer) {
		this.callerIsDealer = callerIsDealer;
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

	public Long getDemoId() {
		return this.demoId;
	}

	public void setDemoId(Long demoId) {
		this.demoId = demoId;
	}

	public Date getDocDate() {
		return this.docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public int getHasAnimal() {
		return this.hasAnimal;
	}

	public void setHasAnimal(int hasAnimal) {
		this.hasAnimal = hasAnimal;
	}

	public int getHasChild() {
		return this.hasChild;
	}

	public void setHasChild(int hasChild) {
		this.hasChild = hasChild;
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

	public Long getOwnerBranchId() {
		return this.ownerBranchId;
	}

	public void setOwnerBranchId(Long ownerBranchId) {
		this.ownerBranchId = ownerBranchId;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getResponsibleId() {
		return this.responsibleId;
	}

	public void setResponsibleId(Long responsibleId) {
		this.responsibleId = responsibleId;
	}

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
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

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Long getRelativeId() {
		return relativeId;
	}

	public void setRelativeId(Long relativeId) {
		this.relativeId = relativeId;
	}

	public String getTempRecommender() {
		return tempRecommender;
	}

	public void setTempRecommender(String tempRecommender) {
		this.tempRecommender = tempRecommender;
	}

	public int getHasAllergy() {
		return hasAllergy;
	}

	public void setHasAllergy(int hasAllergy) {
		this.hasAllergy = hasAllergy;
	}

	public int getHasAsthma() {
		return hasAsthma;
	}

	public void setHasAsthma(int hasAsthma) {
		this.hasAsthma = hasAsthma;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsible_id", insertable = false, updatable = false)
	private Staff responsible;

	public Staff getResponsible() {
		return responsible;
	}

	public void setResponsible(Staff responsible) {
		this.responsible = responsible;
	}

	@OneToMany(mappedBy = "reco", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CrmPhone> phones = new ArrayList<>();

	public List<CrmPhone> getPhones() {
		return phones;
	}

	public void setPhones(List<CrmPhone> phones) {
		this.phones = phones;
	}

	public void addPhone(CrmPhone phone) {
		if (phone != null) {
			if (this.phones == null) {
				this.phones = new ArrayList<>();
			}

			if (!this.phones.contains(phone)) {
				this.phones.add(phone);
				if (phone.getReco() != this) {
					phone.setReco(this);
				}
			}
		}
	}

	@Transient
	public Long getContextId() {
		return getId();
	}

	@Transient
	private Staff owner;

	public Staff getOwner() {
		return owner;
	}

	public void setOwner(Staff owner) {
		this.owner = owner;
	}

	@Transient
	private List<CrmCall> calls;

	public List<CrmCall> getCalls() {
		return calls;
	}

	public void setCalls(List<CrmCall> calls) {
		this.calls = calls;
	}

	@Transient
	private List<CrmDocDemo> demos;

	public List<CrmDocDemo> getDemos() {
		return demos;
	}

	public void setDemos(List<CrmDocDemo> demos) {
		this.demos = demos;
	}

	@Transient
	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}

	@Transient
	public String getStatusName() {
		return RecoConstants.getStatuses().get(getStatusId());
	}
}