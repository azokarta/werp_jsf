package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "salary")
@javax.persistence.SequenceGenerator(name = "seq_salary_id", sequenceName = "seq_salary_id", allocationSize = 1)
public class Salary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8324496645262277104L;

	public Salary() {
		waers = "USD";
		Calendar c = Calendar.getInstance();
		c.set(2099, 0, 1);
		end_date = c.getTime();
		this.bukrs = "";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_salary_id")
	private Long salary_id;

	@Column(name = "bukrs")
	private String bukrs;

	@Column(name = "staff_id")
	private Long staff_id;

	@Column(name = "position_id")
	private Long position_id;

	@Column(name = "beg_date")
	private Date beg_date;

	@Column(name = "amount")
	private double amount;

	@Column(name = "waers")
	private String waers;

	@Column(name = "end_date")
	private Date end_date;

	@Column(name = "branch_id")
	private Long branch_id;

	@Column(name = "business_area_id")
	private Long business_area_id;

	@Column(name = "salary_type")
	private String salary_type;

	@Column(name = "salary_times")
	private Float salary_times;

	@Column(name = "country_id")
	private Long country_id;

	@Column(name = "created_by", nullable = true)
	private Long created_by;

	@Column(name = "created_date", nullable = true)
	private Date created_date;

	@Column(name = "updated_by", nullable = true)
	private Long updated_by;

	@Column(name = "updated_date", nullable = true)
	private Date updated_date;

	@Column(name = "department_id")
	private Long department_id;

	@Column(name = "parent_pyramid_id")
	private Long parent_pyramid_id;

	private Date payroll_date;

	public Date getPayroll_date() {
		return payroll_date;
	}

	public void setPayroll_date(Date payroll_date) {
		this.payroll_date = payroll_date;
	}

	private String note;

	private Long hr_doc_id;

	public Long getHr_doc_id() {
		return hr_doc_id;
	}

	public void setHr_doc_id(Long hr_doc_id) {
		this.hr_doc_id = hr_doc_id;
	}

	public Long getParent_pyramid_id() {
		return parent_pyramid_id;
	}

	public void setParent_pyramid_id(Long parent_pyramid_id) {
		this.parent_pyramid_id = parent_pyramid_id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "staff_id", referencedColumnName = "staff_id", insertable = false, updatable = false)
	private Staff p_staff = new Staff();

	public Staff getP_staff() {
		return p_staff;
	}

	public void setP_staff(Staff p_staff) {
		this.p_staff = p_staff;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	public Long getSalary_id() {
		return salary_id;
	}

	public void setSalary_id(Long salary_id) {
		this.salary_id = salary_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public Date getBeg_date() {
		return beg_date;
	}

	public void setBeg_date(Date beg_date) {
		this.beg_date = beg_date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

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

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public String getSalary_type() {
		return salary_type;
	}

	public void setSalary_type(String salary_type) {
		this.salary_type = salary_type;
	}

	public Float getSalary_times() {
		return salary_times;
	}

	public void setSalary_times(Float salary_times) {
		this.salary_times = salary_times;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the department_id
	 */
	public Long getDepartment_id() {
		return department_id;
	}

	/**
	 * @param department_id
	 *            the department_id to set
	 */
	public void setDepartment_id(Long department_id) {
		this.department_id = department_id;
	}

	public boolean isCurrentSalary() {
		return getEnd_date().after(new Date()) && (new Date()).after(getBeg_date());
	}

	@Transient
	private String staffName;

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

}
