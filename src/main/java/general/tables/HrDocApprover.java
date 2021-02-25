package general.tables;

import java.io.Serializable;
import javax.persistence.*;

import general.Validation;

/**
 * The persistent class for the HR_DOC_APPROVER database table.
 * 
 */
@Entity
@Table(name = "HR_DOC_APPROVER")
@NamedQuery(name = "HrDocApprover.findAll", query = "SELECT h FROM HrDocApprover h")
public class HrDocApprover implements Serializable, Comparable<HrDocApprover> {
	private static final long serialVersionUID = 1L;
	public static final int TYPE_RECRUITMENT = 1;
	public static final int TYPE_TRANSFER = 2;
	public static final int TYPE_DISMISS = 3;

	public static final int STATUS_NONE = 0;
	public static final int STATUS_VIEWED = 1;// Просмотр
	public static final int STATUS_APPROVED = 2;// Согласовал
	public static final int STATUS_REFUSED = 3; // Отказал

	@Id
	@SequenceGenerator(name = "HR_DOC_APPROVER_ID_GENERATOR", sequenceName = "SEQ_HR_DOC_APPROVER_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOC_APPROVER_ID_GENERATOR")
	private Long id;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Column(name = "STATUS_ID")
	private int statusId;

	private String title;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "STAFF_ID")
	private Long staffId;

	// bi-directional many-to-one association to HrDoc
	@ManyToOne
	@JoinColumn(name = "DOC_ID", referencedColumnName = "ID")
	private HrDoc hrDoc;

	public HrDocApprover() {
		setCreatedBy(0L);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPositionId() {
		return this.positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public int getStatusId() {
		return this.statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public HrDoc getHrDoc() {
		return this.hrDoc;
	}

	public void setHrDoc(HrDoc hrDoc) {
		this.hrDoc = hrDoc;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hrDoc == null) ? 0 : hrDoc.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		HrDocApprover other = (HrDocApprover) obj;
		if (hrDoc == null) {
			if (other.hrDoc != null)
				return false;
		} else if (!hrDoc.equals(other.hrDoc))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Transient
	public String getStatusName() {
		switch (getStatusId()) {
		case STATUS_NONE:
			return "Не делал действии";

		case STATUS_VIEWED:
			return "Просмотр";

		case STATUS_APPROVED:
			return "Согласовал";

		case STATUS_REFUSED:
			return "Отказал";
		}

		return null;
	}

	@Override
	public int compareTo(HrDocApprover o) {
		if (Validation.isEmptyLong(this.id) && Validation.isEmptyLong(o.getId())) {
			return 0;
		}
		if (Validation.isEmptyLong(this.id)) {
			return -1;
		}

		if (Validation.isEmptyLong(o.getId())) {
			return 1;
		}

		return this.id.compareTo(o.getId());
	}
}