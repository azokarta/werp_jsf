package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: MatnrPrice
 *
 */
@Entity
@Table(name = "matnr_price")
@javax.persistence.SequenceGenerator(name="seq_matnr_price_id",sequenceName="seq_matnr_price_id",allocationSize=1)
public class MatnrPrice implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_matnr_price_id")
	private Long mp_id;
	private Long matnr;
	private Long customer_id;
	private Long created_by;
	private Long updated_by;
	private Date created_date;
	private Date updated_date;
	private Double price;
	private String bukrs;
	private Long country_id;
	private String waers;
	private static final long serialVersionUID = 1L;

	public MatnrPrice() {
		super();
	}

	public Long getMp_id() {
		return this.mp_id;
	}

	public void setMp_id(Long mp_id) {
		this.mp_id = mp_id;
	}

	public Long getMatnr() {
		return this.matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Long getCustomer_id() {
		return this.customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public Long getCreated_by() {
		return this.created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Long getUpdated_by() {
		return this.updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public Date getCreated_date() {
		return this.created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getUpdated_date() {
		return this.updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result
				+ ((country_id == null) ? 0 : country_id.hashCode());
		result = prime * result
				+ ((created_by == null) ? 0 : created_by.hashCode());
		result = prime * result
				+ ((created_date == null) ? 0 : created_date.hashCode());
		result = prime * result
				+ ((customer_id == null) ? 0 : customer_id.hashCode());
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		result = prime * result + ((mp_id == null) ? 0 : mp_id.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result
				+ ((updated_by == null) ? 0 : updated_by.hashCode());
		result = prime * result
				+ ((updated_date == null) ? 0 : updated_date.hashCode());
		result = prime * result + ((waers == null) ? 0 : waers.hashCode());
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
		MatnrPrice other = (MatnrPrice) obj;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (country_id == null) {
			if (other.country_id != null)
				return false;
		} else if (!country_id.equals(other.country_id))
			return false;
		if (created_by == null) {
			if (other.created_by != null)
				return false;
		} else if (!created_by.equals(other.created_by))
			return false;
		if (created_date == null) {
			if (other.created_date != null)
				return false;
		} else if (!created_date.equals(other.created_date))
			return false;
		if (customer_id == null) {
			if (other.customer_id != null)
				return false;
		} else if (!customer_id.equals(other.customer_id))
			return false;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		if (mp_id == null) {
			if (other.mp_id != null)
				return false;
		} else if (!mp_id.equals(other.mp_id))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (updated_by == null) {
			if (other.updated_by != null)
				return false;
		} else if (!updated_by.equals(other.updated_by))
			return false;
		if (updated_date == null) {
			if (other.updated_date != null)
				return false;
		} else if (!updated_date.equals(other.updated_date))
			return false;
		if (waers == null) {
			if (other.waers != null)
				return false;
		} else if (!waers.equals(other.waers))
			return false;
		return true;
	}

	public boolean isSameMatnr(Object obj) {
		MatnrPrice other = (MatnrPrice) obj;
		if (bukrs.equals(other.getBukrs())
				&& customer_id.equals(other.getCustomer_id())
				&& matnr.equals(other.getMatnr())
				&& country_id.equals(other.getCountry_id())
				&& waers.equals(other.getWaers())) {
			return true;
		}
		return false;
	}
}
