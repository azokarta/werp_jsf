package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_schedule_arc")
@javax.persistence.SequenceGenerator(name="seq_payment_schedule_arc_id",sequenceName="seq_payment_schedule_arc_id",allocationSize=1)
public class PaymentScheduleArc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6483592176288092589L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_payment_schedule_arc_id")
    private Long payment_schedule_id;
	
	@Column(name = "bukrs")	
	private String bukrs;
	
	@Column(name = "awkey")	
	private Long awkey;
	
	@Column(name="payment_date")
	private Date payment_date;
	
	@Column(name = "sum2")	
	private double sum;
	
	@Column(name="paid")
	private double paid;

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public Date getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public Long getPayment_schedule_id() {
		return payment_schedule_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awkey == null) ? 0 : awkey.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		long temp;
		temp = Double.doubleToLongBits(paid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((payment_date == null) ? 0 : payment_date.hashCode());
		temp = Double.doubleToLongBits(sum);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		PaymentScheduleArc other = (PaymentScheduleArc) obj;
		if (awkey == null) {
			if (other.awkey != null)
				return false;
		} else if (!awkey.equals(other.awkey))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (Double.doubleToLongBits(paid) != Double
				.doubleToLongBits(other.paid))
			return false;
		if (payment_date == null) {
			if (other.payment_date != null)
				return false;
		} else if (!payment_date.equals(other.payment_date))
			return false;
		if (Double.doubleToLongBits(sum) != Double.doubleToLongBits(other.sum))
			return false;
		return true;
	}
	
	
}
