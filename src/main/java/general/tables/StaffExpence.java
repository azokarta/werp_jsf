package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Date;

@Entity
@Table(name = "staff_expence")
@javax.persistence.SequenceGenerator(name="seq_staff_expence_id",sequenceName="seq_staff_expence_id",allocationSize=1)
public class StaffExpence {
	
	public StaffExpence(){
		this.sum = 0D;
		this.staff_id = 0L;
		this.et_id = 0L;
		this.currency = "";
		this.is_deleted = 0;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_staff_expence_id")
    private Long se_id;
	
	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "et_id")
	private Long et_id;
	
	public Long getEt_id() {
		return et_id;
	}

	public void setEt_id(Long et_id) {
		this.et_id = et_id;
	}
	
	

	@Column(name = "created_by", nullable = true)
	private Long created_by;
	
	@Column(name = "created_date", nullable = true)
	private Date created_date;
	
	@Column(name = "updated_by", nullable = true)
	private Long updated_by;
	
	@Column(name = "updated_date", nullable = true)
	private Date updated_date;

	@Column(name="currency")
	private String currency;
	
	@Column(name = "bukrs")
	private String bukrs;
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	private Date expence_date;
	
	public Date getExpence_date() {
		return expence_date;
	}

	public void setExpence_date(Date expence_date) {
		this.expence_date = expence_date;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getDeleted_date() {
		return deleted_date;
	}

	public void setDeleted_date(Date deleted_date) {
		this.deleted_date = deleted_date;
	}

	public int getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(int is_deleted) {
		this.is_deleted = is_deleted;
	}

	@Column(name="deleted_date")
	private Date deleted_date;
	
	@Column(name="is_deleted")
	private int is_deleted;
	
	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
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
	
	@Column(name="sum2")
	private Double sum;
	
	@Column(name="description")
	private String description;

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSe_id() {
		return se_id;
	}
	
	
	@ManyToOne
	@JoinColumn(name="staff_id",insertable=false,updatable=false)
	private Staff staff;
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	@Transient
	private String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
