package crm.tables;

import java.io.Serializable;
import javax.persistence.*;

import general.Validation;

/**
 * The persistent class for the CRM_CLIENT_RELATIVE database table.
 * 
 */
@Entity
@Table(name = "CRM_RELATIVE")
public class CrmRelative implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CRM_RELATIVE_ID_GENERATOR", sequenceName = "SEQ_CRM_RELATIVE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_RELATIVE_ID_GENERATOR")
	private Long id;

	private String name;

	@Column(name = "NAME_EN")
	private String nameEn;

	@Column(name = "NAME_TR")
	private String nameTr;

	public CrmRelative() {
	}

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
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameTr() {
		return nameTr;
	}

	public void setNameTr(String nameTr) {
		this.nameTr = nameTr;
	}

	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}
}