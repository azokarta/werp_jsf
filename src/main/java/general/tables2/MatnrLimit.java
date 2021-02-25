package general.tables2;

import java.io.Serializable;
import javax.persistence.*;

import general.tables.Staff;
import general.tables.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the MATNR_LIMIT database table.
 * 
 */
@Entity
@Table(name = "MATNR_LIMIT")
@NamedQuery(name = "MatnrLimit.findAll", query = "SELECT m FROM MatnrLimit m")
public class MatnrLimit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "MATNR_LIMIT_ID_GENERATOR", sequenceName = "SEQ_MATNR_LIMIT_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATNR_LIMIT_ID_GENERATOR")
	private Long id;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.DATE)
	@Column(name = "CREATED_AT", updatable = false)
	private Date createdAt;

	@Column(name = "CREATED_BY", updatable = false)
	private Long createdBy;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Temporal(TemporalType.DATE)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	// bi-directional many-to-one association to MatnrLimitItem
	@OneToMany(mappedBy = "matnrLimit", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MatnrLimitItem> matnrLimitItems;

	public MatnrLimit() {
		this.matnrLimitItems = new ArrayList<>();
		this.bukrs = null;
		this.branchId = 0L;
		this.branchId = 0L;
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

	public Long getPositionId() {
		return this.positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
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

	public List<MatnrLimitItem> getMatnrLimitItems() {
		return this.matnrLimitItems;
	}

	public void setMatnrLimitItems(List<MatnrLimitItem> matnrLimitItems) {
		this.matnrLimitItems = matnrLimitItems;
	}

	public MatnrLimitItem addMatnrLimitItem(MatnrLimitItem matnrLimitItem) {
		if (matnrLimitItems == null) {
			matnrLimitItems = new ArrayList<>();
		}
		if (!matnrLimitItems.contains(matnrLimitItem)) {
			matnrLimitItems.add(matnrLimitItem);
			matnrLimitItem.setMatnrLimit(this);
		}
		return matnrLimitItem;
	}

	public MatnrLimitItem removeMatnrLimitItem(MatnrLimitItem matnrLimitItem) {
		getMatnrLimitItems().remove(matnrLimitItem);
		matnrLimitItem.setMatnrLimit(null);

		return matnrLimitItem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	private User creator;

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	@Transient
	public boolean isNew() {
		return this.id == null || this.id == 0L;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result + ((positionId == null) ? 0 : positionId.hashCode());
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
		MatnrLimit other = (MatnrLimit) obj;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (positionId == null) {
			if (other.positionId != null)
				return false;
		} else if (!positionId.equals(other.positionId))
			return false;
		return true;
	}
	
	
}