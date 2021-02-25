package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payment_schedule")
@javax.persistence.SequenceGenerator(name="seq_payment_schedule_id",sequenceName="seq_payment_schedule_id",allocationSize=1)
public class PaymentSchedule implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 66970368698016252L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_payment_schedule_id")
    private Long payment_schedule_id;
	
	@Column(name = "bukrs")	
	private String bukrs;
	
	@Column(name="payment_date")
	private Date payment_date;
	
	@Column(name = "sum2")	
	private double sum;
	
	@Column(name="paid")
	private double paid;

	@Column(name="is_firstpayment")
	private byte isFirstPayment;
	
	public static byte ISFIRSTPAYMENT = 1;
	
	@Column(name = "awkey")
	private Long awkey;
	
	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public byte getIsFirstPayment() {
		return isFirstPayment;
	}

	public void setIsFirstPayment(byte isFirstPayment) {
		this.isFirstPayment = isFirstPayment;
	}

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

	public void setPayment_schedule_id(Long payment_schedule_id) {
		this.payment_schedule_id = payment_schedule_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result + isFirstPayment;
		long temp;
		temp = Double.doubleToLongBits(paid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((payment_date == null) ? 0 : payment_date.hashCode());
		result = prime
				* result
				+ ((payment_schedule_id == null) ? 0 : payment_schedule_id
						.hashCode());
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
		PaymentSchedule other = (PaymentSchedule) obj;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (isFirstPayment != other.isFirstPayment)
			return false;
		if (Double.doubleToLongBits(paid) != Double
				.doubleToLongBits(other.paid))
			return false;
		if (payment_date == null) {
			if (other.payment_date != null)
				return false;
		} else if (!payment_date.equals(other.payment_date))
			return false;
		if (payment_schedule_id == null) {
			if (other.payment_schedule_id != null)
				return false;
		} else if (!payment_schedule_id.equals(other.payment_schedule_id))
			return false;
		if (Double.doubleToLongBits(sum) != Double.doubleToLongBits(other.sum))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PaymentSchedule [payment_schedule_id=" + payment_schedule_id
				+ ", bukrs=" + bukrs + ", payment_date=" + payment_date
				+ ", sum=" + sum + ", paid=" + paid + ", isFirstPayment="
				+ isFirstPayment + "]";
	}

}
