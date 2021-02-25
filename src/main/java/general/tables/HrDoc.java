package general.tables;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import general.MessageProvider;
import general.comparators.HrDocApproverComparator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the HR_DOC database table.
 * 
 */
@Entity
@Table(name = "HR_DOC")
@NamedQuery(name = "HrDoc.findAll", query = "SELECT h FROM HrDoc h")
public class HrDoc implements Serializable, IMdTable {
	private static final long serialVersionUID = 1L;

	public static final int TYPE_RECRUITMENT = 1;
	public static final int TYPE_TRANSFER = 2;
	public static final int TYPE_DISMISS = 3;
	// Изменение оклада
	public static final int TYPE_CHANGE_SALARY = 4;
	public static final int TYPE_BYPASS_SHEET = 5;

	@Id
	@SequenceGenerator(name = "HR_DOC_ID_GENERATOR", sequenceName = "SEQ_HR_DOC_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOC_ID_GENERATOR")
	private Long id;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "RESPONSIBLE_ID")
	private Long responsibleId;

	@Column(name = "REG_NUMBER")
	private Long regNumber;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	@Column(name = "STATUS_ID")
	private int statusId;

	@Column(name = "TYPE_ID")
	private int typeId;

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	// bi-directional many-to-one association to HrDocApprover
	@OneToMany(mappedBy = "hrDoc", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Sort(type = SortType.COMPARATOR, comparator = HrDocApproverComparator.class)
	private List<HrDocApprover> hrDocApprovers;

	// bi-directional many-to-one association to HrDocItem
	@OneToMany(mappedBy = "hrDoc", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<HrDocItem> hrDocItems;

	public HrDoc() {
		setStatusId(STATUS_ON_CREATE);
		setHrDocItems(new ArrayList<>());
		setHrDocApprovers(new ArrayList<>());
		setResponsibleId(47L);// Bahtiyar HR костыль
		setBranchId(0L);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getResponsibleId() {
		return this.responsibleId;
	}

	public void setResponsibleId(Long responsibleId) {
		this.responsibleId = responsibleId;
	}

	public int getStatusId() {
		return this.statusId;
	}

	public void setStatusId(int statusId) {
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

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(Long regNumber) {
		this.regNumber = regNumber;
	}

	public List<HrDocApprover> getHrDocApprovers() {
		return this.hrDocApprovers;
	}

	public void setHrDocApprovers(List<HrDocApprover> hrDocApprovers) {
		this.hrDocApprovers = hrDocApprovers;
	}

	public HrDocApprover addHrDocApprover(HrDocApprover hrDocApprover) {
		if (hrDocApprover != null) {
			hrDocApprover.setHrDoc(this);
			if (!hrDocApprovers.contains(hrDocApprover)) {
				getHrDocApprovers().add(hrDocApprover);
			}
		}

		return hrDocApprover;
	}

	public HrDocApprover removeHrDocApprover(HrDocApprover hrDocApprover) {
		getHrDocApprovers().remove(hrDocApprover);
		hrDocApprover.setHrDoc(null);

		return hrDocApprover;
	}

	public List<HrDocItem> getHrDocItems() {
		return this.hrDocItems;
	}

	public void setHrDocItems(List<HrDocItem> hrDocItems) {
		this.hrDocItems = hrDocItems;
	}

	public HrDocItem addHrDocItem(HrDocItem hrDocItem) {
		getHrDocItems().add(hrDocItem);
		hrDocItem.setHrDoc(this);

		return hrDocItem;
	}

	public HrDocItem removeHrDocItem(HrDocItem hrDocItem) {
		getHrDocItems().remove(hrDocItem);
		hrDocItem.setHrDoc(null);

		return hrDocItem;
	}

	public String getDocTypeName() {
		MessageProvider mp = new MessageProvider();
		if (getTypeId() == TYPE_RECRUITMENT) {
			return mp.getValue("hr.doc.recruitment");
		} else if (getTypeId() == TYPE_TRANSFER) {
			return mp.getValue("hr.doc.transfer");
		} else if (getTypeId() == TYPE_DISMISS) {
			return mp.getValue("hr.doc.dismiss");
		} else if (getTypeId() == TYPE_CHANGE_SALARY) {
			return mp.getValue("hr.doc.change_salary");
		}else if(getTypeId() == TYPE_BYPASS_SHEET){
			return "Обходной лист";
		}
		return null;
	}

	public static final int STATUS_NONE = 0;
	public static final int STATUS_ON_CREATE = 1; // На создании
	public static final int STATUS_ON_VIEW = 2; // На просмотре
	public static final int STATUS_ON_APPROVEMENT = 3; // На согласовании
	public static final int STATUS_ON_EXECUTION = 4; // На исполнении
	public static final int STATUS_CLOSED = 5;// Закрытый
	public static final int STATUS_REFUSED = 6;// Отказан
	public static final int STATUS_CANCELLED = 7;// Отменен

	public String getStatusName() {
		MessageProvider mp = new MessageProvider();
		if (STATUS_ON_APPROVEMENT == getStatusId()) {
			//return "НА СОГЛАСОВАНИИ";
			return mp.getValue("hr.doc_status_3");
		} else if (STATUS_ON_CREATE == getStatusId()) {
			//return "НА СОЗДАНИИ";
			return mp.getValue("hr.doc_status_1");
		} else if (STATUS_ON_EXECUTION == getStatusId()) {
			//return "НА ИСПОЛНЕНИИ";
			return mp.getValue("hr.doc_status_4");
		} else if (STATUS_REFUSED == getStatusId()) {
			//return "ОТКАЗАН";
			return mp.getValue("hr.doc_status_6");
		} else if (STATUS_CLOSED == getStatusId()) {
			//return "ЗАКРЫТ";
			return mp.getValue("hr.doc_status_5");
		} else if (STATUS_CANCELLED == getStatusId()) {
			return mp.getValue("hr.doc_status_7");
			//return "ОТМЕНЕН";
		}
		return null;
	}

	@Override
	public String getContext() {
		String s = "hr_document";
		if (getTypeId() == TYPE_RECRUITMENT) {
			s += "_rec";
		} else if (getTypeId() == TYPE_TRANSFER) {
			s += "_trans";
		} else if (getTypeId() == TYPE_DISMISS) {
			s += "_dismiss";
		} else if (getTypeId() == TYPE_CHANGE_SALARY) {
			s += "_change_salary";
		}
		return s;
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
	public String getFormattedRegNumber() {
		return String.format("%07d", getRegNumber());
	}

	@Transient
	private List<HrDocActionLog> logs;

	public List<HrDocActionLog> getLogs() {
		return logs;
	}

	public void setLogs(List<HrDocActionLog> logs) {
		this.logs = logs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HrDoc other = (HrDoc) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}