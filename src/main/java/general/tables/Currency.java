package general.tables; 

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@Entity
@Table(name = "currency")
@javax.persistence.SequenceGenerator(name="seq_currency_id",sequenceName="seq_currency_id", allocationSize = 1)
public class Currency implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_currency_id")
    private Long currency_id;
	
	@Column(name = "currency")
	private String currency;
	
	@Column(name = "spras")
	private String spras;
	
	@Column(name = "text20")
	private String text20;
	
	@ManyToOne
	@JoinColumn(name = "currency_id", referencedColumnName ="currency_id", insertable = false, updatable = false)
	private Country p_country;	
	
	public Country getP_country() {
		return p_country;
	}
	public void setP_country(Country p_country) {
		this.p_country = p_country;
	}
	public Long getCurrency_id() {
		return currency_id;
	}
	public void setCurrency_id(Long currency_id) {
		this.currency_id = currency_id;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getSpras() {
		return spras;
	}
	public void setSpras(String spras) {
		this.spras = spras;
	}
	public String getText20() {
		return text20;
	}
	public void setText20(String text20) {
		this.text20 = text20;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currency == null) ? 0 : currency.hashCode());
		result = prime * result
				+ ((currency_id == null) ? 0 : currency_id.hashCode());
		result = prime * result
				+ ((p_country == null) ? 0 : p_country.hashCode());
		result = prime * result + ((spras == null) ? 0 : spras.hashCode());
		result = prime * result + ((text20 == null) ? 0 : text20.hashCode());
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
		Currency other = (Currency) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (currency_id == null) {
			if (other.currency_id != null)
				return false;
		} else if (!currency_id.equals(other.currency_id))
			return false;
		if (p_country == null) {
			if (other.p_country != null)
				return false;
		} else if (!p_country.equals(other.p_country))
			return false;
		if (spras == null) {
			if (other.spras != null)
				return false;
		} else if (!spras.equals(other.spras))
			return false;
		if (text20 == null) {
			if (other.text20 != null)
				return false;
		} else if (!text20.equals(other.text20))
			return false;
		return true;
	} 
	
}
