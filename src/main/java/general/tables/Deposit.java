package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="deposit")
@javax.persistence.SequenceGenerator(name="seq_deposit_id",sequenceName="seq_deposit_id", allocationSize = 1)
public class Deposit {
	@Id
	@Column(name = "deposit_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_deposit_id")
	private Long deposit_id;
	
	@Column(name="staff_id")
	private Long staff_id;
	
	@Column(name="customer_id")
	private Long customer_id;
	
	@Column(name="waers")
	private String waers;
	
	@Column(name="amount")
	private double amount;

	public Long getDeposit_id() {
		return deposit_id;
	}

	public void setDeposit_id(Long deposit_id) {
		this.deposit_id = deposit_id;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
}
