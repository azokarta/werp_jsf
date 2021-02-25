package general.tables2;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;

import general.tables.Matnr;

/**
 * The persistent class for the MATNR_LIMIT_ITEM database table.
 * 
 */
@Entity
@Table(name = "MATNR_LIMIT_ITEM")
@NamedQuery(name = "MatnrLimitItem.findAll", query = "SELECT m FROM MatnrLimitItem m")
public class MatnrLimitItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "ACCOUNT_LIMIT")
	private Double accountLimit;

	@Id
	@SequenceGenerator(name = "MATNR_LIMIT_ITEM_ID_GENERATOR", sequenceName = "SEQ_MATNR_LIMIT_ITEM_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATNR_LIMIT_ITEM_ID_GENERATOR")
	private Long id;

	private Long matnr;

	// bi-directional many-to-one association to MatnrLimit
	@ManyToOne
	@JoinColumn(name = "ML_ID", referencedColumnName = "ID")
	private MatnrLimit matnrLimit;

	public MatnrLimitItem() {
	}

	public Double getAccountLimit() {
		return this.accountLimit;
	}

	public void setAccountLimit(Double accountLimit) {
		this.accountLimit = accountLimit;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMatnr() {
		return this.matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public MatnrLimit getMatnrLimit() {
		return this.matnrLimit;
	}

	public void setMatnrLimit(MatnrLimit matnrLimit) {
		this.matnrLimit = matnrLimit;
	}

	@OneToOne
	@JoinColumn(name = "matnr", insertable = false, updatable = false)
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
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
		MatnrLimitItem other = (MatnrLimitItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		return true;
	}

}