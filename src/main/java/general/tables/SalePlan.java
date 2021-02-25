package general.tables;

import java.lang.Long;
import java.lang.String;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "sale_plan")
@javax.persistence.SequenceGenerator(name="seq_sale_plan_id",sequenceName="seq_sale_plan_id",allocationSize=1)
public class SalePlan {

	public SalePlan() {
		this.branch_id = 0L;
		this.business_area_id = 0L;
		this.country_id = 0L;
		this.created_by = 0L;
		this.created_date = null;
		this.month = 0;
		this.plan_count = 0;
		this.plan_sum = 0D;
		this.updated_by = 0L;
		this.updated_date = null;
		this.waers = "";
		this.bukrs = "";
		this.year = 0;
		this.done_count = 0;
		this.plan_type=0;
	}
	
	@Id
	@Column(name = "sp_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_sale_plan_id")
	private Long sp_id;

	@Column(name = "created_by", nullable = true)
	private Long created_by;

	@Column(name = "created_date", nullable = true)
	private Date created_date;

	@Column(name = "updated_by", nullable = true)
	private Long updated_by;

	@Column(name = "updated_date", nullable = true)
	private Date updated_date;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name="plan_count")
	private int plan_count;
	
	@Column(name="business_area_id")
	private Long business_area_id;
	
	@Column(name="waers")
	private String waers;
	
	@Column(name="plan_sum")
	private Double plan_sum;
	
	@Column(name="country_id")
	private Long country_id;
	
	@Column(name="month")
	private int month;
	
	@Column(name="year")
	private int year;
	
	@Column(name="bukrs")
	private String bukrs;
	
	@Column(name="done_count")
	private int done_count;
	
	@Column(name="done_sum")
	private Double done_sum;
	
	@Column(name="plan_type")
	private byte plan_type;
	
	public static byte TYPE_CURRENT_CEBILON = 1;
	public static byte TYPE_OVERDUE_CEBILON = 2;

	public static byte TYPE_CURRENT_ROBOCLEAN = 11;
	public static byte TYPE_OVERDUE_ROBOCLEAN = 12;
	
	public int getDone_count() {
		return done_count;
	}

	public void setDone_count(int done_count) {
		this.done_count = done_count;
	}

	public Double getDone_sum() {
		return done_sum;
	}

	public void setDone_sum(Double done_sum) {
		this.done_sum = done_sum;
	}

	public byte getPlan_type() {
		return plan_type;
	}

	public void setPlan_type(byte plan_type) {
		this.plan_type = plan_type;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getSp_id() {
		return sp_id;
	}

	public void setSp_id(Long sp_id) {
		this.sp_id = sp_id;
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

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getPlan_count() {
		return plan_count;
	}

	public void setPlan_count(int plan_count) {
		this.plan_count = plan_count;
	}

	public Double getPlan_sum() {
		return plan_sum;
	}

	public void setPlan_sum(Double plan_sum) {
		this.plan_sum = plan_sum;
	}
	
	
}
