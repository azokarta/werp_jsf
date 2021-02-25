package general.tables;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author onlasyn 
 * @description Таблица оприходовании
 */

@Entity
@Table(name = "posting")
@javax.persistence.SequenceGenerator(name = "seq_posting_id", sequenceName = "seq_posting_id", allocationSize = 1)
public class Posting {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_posting_id")
	private Long id;

	private Long customer_id;
	private String bukrs;
	private Date created_date;
	private Date updated_date;
	private Long created_by;
	private Long updated_by;
	private Long werks;
	private Double total_amount;
	private Long awkey;
	private Date posting_date;
	
	public Date getPosting_date() {
		return posting_date;
	}

	public void setPosting_date(Date posting_date) {
		this.posting_date = posting_date;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public Long getWerks() {
		return werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}

	public Double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(Double total_amount) {
		this.total_amount = total_amount;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public Long getId() {
		return id;
	}
	
	

	@Transient
	private String werksName;

	@Transient
	private String bukrsName;

	@Transient
	private String customerName;

	public String getWerksName() {
		return werksName;
	}

	public void setWerksName(String werksName) {
		this.werksName = werksName;
	}

	public String getBukrsName() {
		return bukrsName;
	}

	public void setBukrsName(String bukrsName) {
		this.bukrsName = bukrsName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
