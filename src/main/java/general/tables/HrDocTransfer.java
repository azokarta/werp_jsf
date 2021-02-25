package general.tables;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The persistent class for the HR_DOC_TRANSFER database table.
 * 
 */
@Entity
@Table(name = "HR_DOC_TRANSFER")
@NamedQuery(name = "HrDocTransfer.findAll", query = "SELECT h FROM HrDocTransfer h")
@javax.persistence.SequenceGenerator(name="SEQ_HR_DOC_TRANSFER_ID",sequenceName="SEQ_HR_DOC_TRANSFER_ID", allocationSize = 1)
public class HrDocTransfer implements Serializable, IMdTable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HR_DOC_TRANSFER_ID")
	private Long id;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "CURRENT_RESP_ID")
	private Long currentRespId;

	@Column(name = "RESPONSIBLE_ID")
	private Long responsibleId;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	// bi-directional many-to-one association to HrDocTransferActionLog
	@OneToMany(mappedBy = "hrDocTransfer")
	private Set<HrDocTransferActionLog> hrDocTransferActionLogs;

	// bi-directional many-to-one association to HrDocTransferItem
	@OneToMany(mappedBy = "hrDocTransfer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<HrDocTransferItem> hrDocTransferItems;

	@OneToMany(mappedBy = "hrDocTransfer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<HrDocTransferApprover> hrDocTransferApprovers = new ArrayList<>();

	public List<HrDocTransferApprover> getHrDocTransferApprovers() {
		return hrDocTransferApprovers;
	}

	public void setHrDocTransferApprovers(List<HrDocTransferApprover> hrDocTransferApprovers) {
		this.hrDocTransferApprovers = hrDocTransferApprovers;
	}

	public HrDocTransfer() {
		this.hrDocTransferItems = new HashSet<>();
		setResponsibleId(47L);// Bahtiyar HR костыль
		setCurrentRespId(0L);
		// this.hrDocTransferActionLogs = new Tree
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

	public Long getCurrentRespId() {
		return this.currentRespId;
	}

	public void setCurrentRespId(Long currentRespId) {
		this.currentRespId = currentRespId;
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

	public Set<HrDocTransferActionLog> getHrDocTransferActionLogs() {
		return this.hrDocTransferActionLogs;
	}

	public void setHrDocTransferActionLogs(Set<HrDocTransferActionLog> hrDocTransferActionLogs) {
		this.hrDocTransferActionLogs = hrDocTransferActionLogs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HrDocTransferActionLog addHrDocTransferActionLog(HrDocTransferActionLog hrDocTransferActionLog) {
		getHrDocTransferActionLogs().add(hrDocTransferActionLog);
		hrDocTransferActionLog.setHrDocTransfer(this);

		return hrDocTransferActionLog;
	}

	public HrDocTransferActionLog removeHrDocTransferActionLog(HrDocTransferActionLog hrDocTransferActionLog) {
		getHrDocTransferActionLogs().remove(hrDocTransferActionLog);
		hrDocTransferActionLog.setHrDocTransfer(null);

		return hrDocTransferActionLog;
	}

	public HrDocTransferApprover addHrDocTransferApprover(HrDocTransferApprover hrDocTransferApprover) {
		if (hrDocTransferApprover != null) {
			if (hrDocTransferApprovers == null) {
				setHrDocTransferApprovers(new ArrayList<>());
			}
			if (!hrDocTransferApprovers.contains(hrDocTransferApprover)) {
				getHrDocTransferApprovers().add(hrDocTransferApprover);
				hrDocTransferApprover.setHrDocTransfer(this);
			}
		}

		return hrDocTransferApprover;
	}

	public HrDocTransferApprover removeHrDocTransferApprover(HrDocTransferApprover hrDocTransferApprover) {
		getHrDocTransferApprovers().remove(hrDocTransferApprover);
		hrDocTransferApprover.setHrDocTransfer(null);

		return hrDocTransferApprover;
	}

	public Set<HrDocTransferItem> getHrDocTransferItems() {
		return this.hrDocTransferItems;
	}

	public void setHrDocTransferItems(Set<HrDocTransferItem> hrDocTransferItems) {
		this.hrDocTransferItems = hrDocTransferItems;
	}

	public HrDocTransferItem addHrDocTransferItem(HrDocTransferItem hrDocTransferItem) {
		if (hrDocTransferItem != null) {
			if (hrDocTransferItems == null) {
				setHrDocTransferItems(new HashSet<>());
			}
			getHrDocTransferItems().add(hrDocTransferItem);
			hrDocTransferItem.setHrDocTransfer(this);
		}

		return hrDocTransferItem;
	}

	public HrDocTransferItem removeHrDocTransferItem(HrDocTransferItem hrDocTransferItem) {
		getHrDocTransferItems().remove(hrDocTransferItem);
		hrDocTransferItem.setHrDocTransfer(null);

		return hrDocTransferItem;
	}

	public static final Integer STATUS_ON_CREATE = 1; // На создании
	public static final Integer STATUS_ON_VIEW = 2; // На просмотре
	public static final Integer STATUS_ON_APPROVEMENT = 3; // На согласовании
	public static final Integer STATUS_ON_EXECUTION = 4; // На исполнении
	public static final Integer STATUS_CLOSED = 5;// Закрытый
	public static final Integer STATUS_REFUSED = 6;// Отказан

	public String getStatusName() {
		if (STATUS_CLOSED.equals(getStatusId())) {
			return "ЗАКРЫТ";
		} else if (STATUS_ON_APPROVEMENT.equals(getStatusId())) {
			return "НА СОГЛАСОВАНИИ";
		} else if (STATUS_ON_CREATE.equals(getStatusId())) {
			return "НА СОЗДАНИИ";
		} else if (STATUS_ON_EXECUTION.equals(getStatusId())) {
			return "НА ИСПОЛНЕНИИ";
		} else if (STATUS_REFUSED.equals(getStatusId())) {
			return "ОТКАЗАНО";
		}

		return "";
	}

	@Override
	public String getContext() {
		return "hr_document_trans";
	}

	@Override
	public Long getContextId() {
		return getId();
	}

	@Transient
	private Staff creator;

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}

	@Transient
	private Staff responsible;

	public Staff getResponsible() {
		return responsible;
	}

	public void setResponsible(Staff responsible) {
		this.responsible = responsible;
	}

	@Transient
	private Staff currentResponsible;

	public Staff getCurrentResponsible() {
		return currentResponsible;
	}

	public void setCurrentResponsible(Staff currentResponsible) {
		this.currentResponsible = currentResponsible;
	}

}