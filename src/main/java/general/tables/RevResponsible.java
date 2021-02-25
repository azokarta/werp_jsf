package general.tables;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the REV_RESPONSIBLE database table.
 * 
 */
@Entity
@Table(name = "REV_RESPONSIBLE")
@NamedQuery(name = "RevResponsible.findAll", query = "SELECT r FROM RevResponsible r")
public class RevResponsible implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "REV_RESPONSIBLE_ID_GENERATOR", sequenceName = "SEQ_REV_RESPONSIBLE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REV_RESPONSIBLE_ID_GENERATOR")
	private Long id;

	@Column(name = "POSITION_NAME")
	private String positionName;

	@Column(name = "STAFF_ID")
	private Long staffId;

	@Column(name = "POSITION_ID")
	private Long positionId;

	// bi-directional many-to-one association to Revision
	@ManyToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "REV_ID", referencedColumnName = "ID")
	private Revision revision;

	public RevResponsible() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPositionName() {
		return this.positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public Long getStaffId() {
		return this.staffId;
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

	public Revision getRevision() {
		return this.revision;
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "staff_id", referencedColumnName = "staff_id", insertable = false, updatable = false)
	private Staff staff;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

}