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
@Table(name = "payment_schedule_temporary")
@javax.persistence.SequenceGenerator(name="SEQ_PS_TEMPORARY_ID",sequenceName="SEQ_PS_TEMPORARY_ID",allocationSize=1)
public class PaymentScheduleTemporary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2852193467871555146L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_PS_TEMPORARY_ID")
	private Long id;
	
	@Column(name = "bukrs")	
	private String bukrs;
	
	@Column(name="payment_date")
	private Date payment_date;
	
	@Column(name = "sum")	
	private double sum;
	
	@Column(name="paid")
	private double paid;

	@Column(name="is_firstpayment")
	private byte isFirstPayment;
	
	public static byte ISFIRSTPAYMENT = 1;
	
	@Column(name = "contract_id")
	private Long contractId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public byte getIsFirstPayment() {
		return isFirstPayment;
	}

	public void setIsFirstPayment(byte isFirstPayment) {
		this.isFirstPayment = isFirstPayment;
	}

	public static byte getISFIRSTPAYMENT() {
		return ISFIRSTPAYMENT;
	}

	public static void setISFIRSTPAYMENT(byte iSFIRSTPAYMENT) {
		ISFIRSTPAYMENT = iSFIRSTPAYMENT;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	
}
