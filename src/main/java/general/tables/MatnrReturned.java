package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: MatnrReturn
 *
 */
@Entity
@Table(name="matnr_returned")
@javax.persistence.SequenceGenerator(name="seq_matnr_returned_id",sequenceName="seq_matnr_returned_id",allocationSize=1)

public class MatnrReturned implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_matnr_returned_id")
	private Long mr_id;
	private Long awkey;
	private Date created_date;
	private Date updated_date;
	private Long created_by;
	private Long updated_by;
	private Long branch_id;
	private String bukrs;
	private Long to_werks;
	private Long contract_number;
	private Long customer_id;
	@Column(name="comment2")
	private String comment;
	private static final long serialVersionUID = 1L;

	public MatnrReturned() {
		super();
	}   
	public Long getMr_id() {
		return this.mr_id;
	}

	public void setMr_id(Long mr_id) {
		this.mr_id = mr_id;
	}   
	  
	public Long getAwkey() {
		return this.awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
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
	
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	
	public Long getTo_werks() {
		return to_werks;
	}
	public void setTo_werks(Long to_werks) {
		this.to_werks = to_werks;
	}
	public Long getContract_number() {
		return contract_number;
	}
	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}
	public Long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awkey == null) ? 0 : awkey.hashCode());
		result = prime * result
				+ ((branch_id == null) ? 0 : branch_id.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((contract_number == null) ? 0 : contract_number.hashCode());
		result = prime * result
				+ ((created_by == null) ? 0 : created_by.hashCode());
		result = prime * result
				+ ((created_date == null) ? 0 : created_date.hashCode());
		result = prime * result
				+ ((customer_id == null) ? 0 : customer_id.hashCode());
		result = prime * result + ((mr_id == null) ? 0 : mr_id.hashCode());
		result = prime * result
				+ ((to_werks == null) ? 0 : to_werks.hashCode());
		result = prime * result
				+ ((updated_by == null) ? 0 : updated_by.hashCode());
		result = prime * result
				+ ((updated_date == null) ? 0 : updated_date.hashCode());
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
		MatnrReturned other = (MatnrReturned) obj;
		if (awkey == null) {
			if (other.awkey != null)
				return false;
		} else if (!awkey.equals(other.awkey))
			return false;
		if (branch_id == null) {
			if (other.branch_id != null)
				return false;
		} else if (!branch_id.equals(other.branch_id))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (contract_number == null) {
			if (other.contract_number != null)
				return false;
		} else if (!contract_number.equals(other.contract_number))
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
		if (mr_id == null) {
			if (other.mr_id != null)
				return false;
		} else if (!mr_id.equals(other.mr_id))
			return false;
		if (to_werks == null) {
			if (other.to_werks != null)
				return false;
		} else if (!to_werks.equals(other.to_werks))
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
		return true;
	}
	
	
   
}
