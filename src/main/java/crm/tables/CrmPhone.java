package crm.tables;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.BatchSize;

import general.Validation;

/**
 * The persistent class for the CRM_PHONE database table.
 * 
 */
@Entity
@Table(name = "CRM_PHONE")
@NamedQuery(name = "CrmPhone.findAll", query = "SELECT c FROM CrmPhone c")
public class CrmPhone implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CRM_PHONE_ID_GENERATOR", sequenceName = "SEQ_CRM_PHONE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_PHONE_ID_GENERATOR")
	private Long id;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "PHONE_TYPE")
	private String phoneType;

	// @Column(name = "RECO_ID")
	// private Long recoId;

	public CrmPhone() {
		this.phoneType = "MOB";
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneType() {
		return this.phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	// public Long getRecoId() {
	// return this.recoId;
	// }
	//
	// public void setRecoId(Long recoId) {
	// this.recoId = recoId;
	// }

	@ManyToOne
	@BatchSize(size = 10)
	@JoinColumn(name = "reco_id")
	private CrmDocReco reco;

	public CrmDocReco getReco() {
		return reco;
	}

	public void setReco(CrmDocReco reco) {
		this.reco = reco;
	}

	public String getFormattedNumber() {
		if (Validation.isEmptyString(getPhoneNumber())) {
			return "";
		}

		int l = getPhoneNumber().length();
		return getPhoneNumber().substring(0, l - 3) + "***";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
		CrmPhone other = (CrmPhone) obj;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}

}