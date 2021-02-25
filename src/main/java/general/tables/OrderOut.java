package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OrderOut
 *
 */
@Entity
@Table(name="order_out")
@javax.persistence.SequenceGenerator(name="seq_order_out_id",sequenceName="seq_order_out_id",allocationSize=1)
public class OrderOut implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_order_out_id")
	@Column(name = "id")
	private Long id;
	
	private Long customer_id;
	private Date created_date;
	private Date updated_date;
	private Long created_by;
	private Long updated_by;
	private int status;
	@Column(name="comment2")
	private String comment;
	private String bukrs;
	private Long invoice_id;
	
	private static final long serialVersionUID = 1L;

	public OrderOut() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public Long getCustomer_id() {
		return this.customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}   
	
	
	
	public Date getCreated_date() {
		return created_date;
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
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public Long getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
	}
	
	@Transient
	private String customerName;

	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	
}
