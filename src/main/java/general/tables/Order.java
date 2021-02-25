package general.tables;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_table")
@javax.persistence.SequenceGenerator(name = "seq_order_table_id", sequenceName = "seq_order_table_id", allocationSize = 1)
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_order_table_id")
	private Long id;

	public void setId(Long id) {
		this.id = id;
	}

	private String bukrs;
	private Long customer_id;
	private double total_amount;
	private Integer status_id;
	private Long country_id;
	private String currency;
	private Date order_date;
	private Long department_id;
	private Date created_at;
	private Date updated_at;
	private Long created_by;
	private Long updated_by;
	private String note;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public Long getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(Long department_id) {
		this.department_id = department_id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public Order() {
		super();
		setCreated_at(Calendar.getInstance().getTime());
	}

	
	public static final Integer STATUS_OPENED = 1;
	public static final Integer STATUS_CLOSED = 2;
	
	public String getStatusName(){
		if(getStatus_id() != null){
			if(getStatus_id() == STATUS_OPENED){
				return "Открытый";
			}else{
				return "Закрыт";
			}
		}
		return null;
	}
	
	@Transient
	private Staff creator;
	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}
	
	
	@Transient Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
	public static final String CONTEXT = "order";
}
