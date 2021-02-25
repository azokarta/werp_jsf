package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * The persistent class for the HR_DOCUMENT_ROUTE database table.
 * 
 */
@Entity
@Table(name = "HR_DOCUMENT_ROUTE")
public class HrDocumentRoute implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQ_HR_DOCUMENT_ROUTE_ID", sequenceName = "SEQ_HR_DOCUMENT_ROUTE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HR_DOCUMENT_ROUTE_ID")
	private Long id;

	@Column(name = "IS_CURRENT")
	private int isCurrent;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "STAFF_ID")
	private Long staffId;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	private String title;

	public HrDocumentRoute() {
		this.statusId = 0;
		setCreatedBy(0L);
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

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public static final Integer STATUS_NO_ACTION = 0; // Не делал действии
	public static final Integer STATUS_VIEW = 1; // Просмотр
	public static final Integer STATUS_AGREE = 2; // Согласовал
	public static final Integer STATUS_REFUSE = 3; // Отказал

	@ManyToOne
	@Cascade(value = { CascadeType.PERSIST })
	@JoinColumn(name = "hr_document_id")
	private HrDocument hrDocument;

	public HrDocument getHrDocument() {
		return hrDocument;
	}

	public void setHrDocument(HrDocument hrDocument) {
		this.hrDocument = hrDocument;
	}

	public String getStatusName() {
		if (getStatusId().equals(HrDocumentRoute.STATUS_AGREE)) {
			return "Согласовал";
		}
		return null;
	}

	@Transient
	private String lf;

	public String getLf() {
		return lf;
	}

	public void setLf(String lf) {
		this.lf = lf;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Transient
	private Staff staff;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hrDocument == null) ? 0 : hrDocument.hashCode());
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
		HrDocumentRoute other = (HrDocumentRoute) obj;
		if (hrDocument == null) {
			if (other.hrDocument != null)
				return false;
		} else if (!hrDocument.equals(other.hrDocument))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}