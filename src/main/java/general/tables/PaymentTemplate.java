package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_template")
@javax.persistence.SequenceGenerator(name="seq_payment_template_id",sequenceName="seq_payment_template_id",allocationSize=1)
public class PaymentTemplate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7845265489236195791L;

	public PaymentTemplate() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PaymentTemplate(int i) {
		this.pt_order = i;
		this.month_num = 1;
		if (this.pt_order > 0) {
			this.info = "monthly_payment";
		} else {
			this.info = "first_payment";
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_payment_template_id")
    private Long id;
	
	@Column(name = "price_list_id")	
	private Long price_list_id;

	@Column(name = "pt_order")	
	private int pt_order;
	
	@Column(name = "month_num")	
	private int month_num;
	
	@Column(name = "monthly_payment_sum")	
	private double monthly_payment_sum;
	
	@Column(name = "info")	
	private String info;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPrice_list_id() {
		return price_list_id;
	}

	public void setPrice_list_id(Long price_list_id) {
		this.price_list_id = price_list_id;
	}

	public int getPt_order() {
		return pt_order;
	}

	public void setPt_order(int pt_order) {
		this.pt_order = pt_order;
	}

	public int getMonth_num() {
		return month_num;
	}

	public void setMonth_num(int month_num) {
		this.month_num = month_num;
	}

	public double getMonthly_payment_sum() {
		return monthly_payment_sum;
	}

	public void setMonthly_payment_sum(double monthly_payment_sum) {
		this.monthly_payment_sum = monthly_payment_sum;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	
	
}
