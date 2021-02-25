package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;

/**
 * The persistent class for the HR_DOCUMENT_ACTION_LOG database table.
 * 
 */
@Entity
@Table(name = "HR_DOCUMENT_ACTION_LOG")
public class HrDocumentActionLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQ_HR_DOCUMENT_ACTION_LOG_ID", sequenceName = "SEQ_HR_DOCUMENT_ACTION_LOG_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HR_DOCUMENT_ACTION_LOG_ID")
	private Long id;

	@Column(name = "ACTION_ID")
	private Integer actionId;

	@Column(name = "CREATED_AT")
	private Date createdAt;

	public HrDocumentActionLog() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getActionId() {
		return this.actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@ManyToOne
	@JoinColumn(name = "hr_document_id")
	@OrderBy(" created_at DESC ")
	private HrDocument hrDocument;

	public HrDocument getHrDocument() {
		return hrDocument;
	}

	public void setHrDocument(HrDocument hrDocument) {
		this.hrDocument = hrDocument;
	}

	@OneToOne
	@JoinColumn(name = "created_by")
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getActionName() {
		return HrDocument.getActionMap().get(getActionId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((actionId == null) ? 0 : actionId.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		HrDocumentActionLog other = (HrDocumentActionLog) obj;
		if (actionId == null) {
			if (other.getActionId() != null)
				return false;
		} else if (!actionId.equals(other.getActionId()))
			return false;

		if (user == null) {
			if (other.user != null)
				return false;
		} else if (other.getUser() == null) {
			if (user == null) {
				return false;
			}
		}

		if (!user.getUser_id().equals(other.getUser().getUser_id())) {
			return false;
		}
		return true;
	}

}