package general.tables; 
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "country")
@javax.persistence.SequenceGenerator(name="seq_country_id",sequenceName="seq_country_id", allocationSize = 1)
public class Country implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2687219312789869258L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_country_id")
    private Long country_id;
    
	@Column(name = "country")
	private String country;
	
	@Column(name = "code")
    private String code;    
    
	@Column(name = "currency_id")
    private Long currency_id;    
    
	@Column(name = "phonecode")
    private String phonecode;
    
	@Column(name = "currency")
    private String currency;
	
	@Column(name = "tel_pattern")
    private String telPattern;	 
	public String getTelPattern() {
		return telPattern;
	}
	public void setTelPattern(String telPattern) {
		this.telPattern = telPattern;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPhonecode() {
		return phonecode;
	}

	public void setPhonecode(String phonecode) {
		this.phonecode = phonecode;
	}

	public Long getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(Long currency_id) {
		this.currency_id = currency_id;
	}

	public void setCountry_id(Long country_id) {
    	this.country_id = country_id;
    }

    public Long getCountry_id() {
        return country_id;
    }

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((country_id == null) ? 0 : country_id.hashCode());
		result = prime * result
				+ ((currency_id == null) ? 0 : currency_id.hashCode());
		result = prime * result
				+ ((phonecode == null) ? 0 : phonecode.hashCode());
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
		Country other = (Country) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (country_id == null) {
			if (other.country_id != null)
				return false;
		} else if (!country_id.equals(other.country_id))
			return false;
		if (currency_id == null) {
			if (other.currency_id != null)
				return false;
		} else if (!currency_id.equals(other.currency_id))
			return false;
		if (phonecode == null) {
			if (other.phonecode != null)
				return false;
		} else if (!phonecode.equals(other.phonecode))
			return false;
		return true;
	}
	
}
