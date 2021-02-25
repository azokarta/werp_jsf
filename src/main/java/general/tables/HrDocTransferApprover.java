package general.tables;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the HR_DOC_TRANSFER_APPROVER database table.
 * 
 */
@Entity
@Table(name = "HR_DOC_TRANSFER_APPROVER")
@NamedQuery(name = "HrDocTransferApprover.findAll", query = "SELECT h FROM HrDocTransferApprover h")
public class HrDocTransferApprover implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "HR_DOC_TRANSFER_APPROVER_ID_GENERATOR", sequenceName = "SEQ_HR_DOC_TRANS_APPROVER_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOC_TRANSFER_APPROVER_ID_GENERATOR")
	private Long id;

	@Column(name = "IS_CURRENT")
	private int isCurrent;

	@Column(name = "SORT_ORDER")
	private int sortOrder;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	public HrDocTransferApprover() {
		setPositionId(0L);
		setCreatedBy(0L);
	}

	// bi-directional many-to-one association to HrDocTransfer
	@ManyToOne
	@JoinColumn(name = "DOC_ID", referencedColumnName = "ID")
	private HrDocTransfer hrDocTransfer;

	public HrDocTransfer getHrDocTransfer() {
		return hrDocTransfer;
	}

	public void setHrDocTransfer(HrDocTransfer hrDocTransfer) {
		this.hrDocTransfer = hrDocTransfer;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getIsCurrent() {
		return this.isCurrent;
	}

	public void setIsCurrent(int isCurrent) {
		this.isCurrent = isCurrent;
	}

	public int getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public static final Integer STATUS_NONE = 0;
	public static final Integer STATUS_APPROVED = 1;// Согласовал
	public static final Integer STATUS_REFUSED = 2; // Отказал

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HrDocTransferApprover other = (HrDocTransferApprover) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}