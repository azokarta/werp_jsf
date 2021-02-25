package crm.tables;

import java.io.Serializable;
import javax.persistence.*;

import crm.constants.ReasonConstants;
import general.Validation;

/**
 * The persistent class for the CRM_REASON database table.
 * 
 */
@Entity
@Table(name = "CRM_REASON")
@NamedQuery(name = "CrmReason.findAll", query = "SELECT c FROM CrmReason c")
public class CrmReason implements Serializable {
	private static final long serialVersionUID = 1L;

	// private String context;

	@Id
	@SequenceGenerator(name = "CRM_REASON_ID_GENERATOR", sequenceName = "SEQ_CRM_REASON_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_REASON_ID_GENERATOR")
	@Column(updatable = false)
	private Long id;

	private String name;

	@Column(name = "NAME_EN")
	private String nameEn;

	@Column(name = "NAME_TR")
	private String nameTr;

	@Column(name = "TYPE_ID")
	private Integer typeId;

	public CrmReason() {
	}

	// public String getContext() {
	// return this.context;
	// }
	//
	// public void setContext(String context) {
	// this.context = context;
	// }

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameEn() {
		return this.nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameTr() {
		return this.nameTr;
	}

	public void setNameTr(String nameTr) {
		this.nameTr = nameTr;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	@Transient
	public String getTypeName() {
		return ReasonConstants.getReasonTypes().get(getTypeId());
	}

	@Transient
	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}
}