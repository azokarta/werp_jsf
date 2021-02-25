package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the REVISION database table.
 * 
 */
@Entity
@NamedQuery(name = "Revision.findAll", query = "SELECT r FROM Revision r")
public class Revision implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "REVISION_ID_GENERATOR", sequenceName = "SEQ_REVISION_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REVISION_ID_GENERATOR")
	private Long id;

	@Temporal(TemporalType.DATE)
	@Column(name = "BEGIN_DATE")
	private Date beginDate;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	private String currency;

	@Temporal(TemporalType.DATE)
	@Column(name = "FINISH_DATE")
	private Date finishDate;

	private String note;

	private Integer status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	private Long werks;

	// bi-directional many-to-one association to RevResponsible
	@OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RevResponsible> revResponsibles;

	public Revision() {
		this.status = STATUS_NEW;
	}

	public Date getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
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

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Long getWerks() {
		return this.werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<RevResponsible> getRevResponsibles() {
		return this.revResponsibles;
	}

	public void setRevResponsibles(List<RevResponsible> revResponsibles) {
		this.revResponsibles = revResponsibles;
	}

	public RevResponsible addRevResponsible(RevResponsible revResponsible) {
		getRevResponsibles().add(revResponsible);
		revResponsible.setRevision(this);

		return revResponsible;
	}

	public RevResponsible removeRevResponsible(RevResponsible revResponsible) {
		getRevResponsibles().remove(revResponsible);
		revResponsible.setRevision(null);

		return revResponsible;
	}

	public static final Integer STATUS_NEW = 1;// Новый
	public static final Integer STATUS_IN_ACTION = 2;// В действии
	public static final Integer STATUS_FINISHED = 3;// Завершен
	public static final Integer STATUS_CLOSED = 4;// ЗАКРЫТ

	public String getStatusName() {
		if (getStatus().equals(STATUS_NEW)) {
			return "НОВЫЙ";
		} else if (getStatus().equals(STATUS_IN_ACTION)) {
			return "В ДЕЙСТВИИ";
		}else if(STATUS_FINISHED.equals(getStatus())){
			return "ЗАВЕРШЕН";
		}else if(STATUS_CLOSED.equals(getStatus())){
			return "ЗАКРЫТ";
		}

		return null;
	}

}