package general.tables;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "bank_account")
@javax.persistence.SequenceGenerator(name="seq_bank_account_id",sequenceName="seq_bank_account_id", allocationSize = 1)
public class BankAccount {


	@Id
	@Column(name="ba_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_bank_account_id")
	private Long ba_id;
	
	@Column(name = "iban")
	private String iban;
	
	@Column(name = "currency")
	private String currency;	
	
	@Column(name = "customer_id")
	private Long customer_id;
	
	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	@Column(name = "bank_id")
	private Long bank_id;

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getBank_id() {
		return bank_id;
	}

	public void setBank_id(Long bank_id) {
		this.bank_id = bank_id;
	}

	public Long getBa_id() {
		return ba_id;
	}
	
	@Column(name = "created_by", nullable = true)
	private Long created_by;
	
	@Column(name = "created_date", nullable = true)
	private Date created_date;
	
	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
}
