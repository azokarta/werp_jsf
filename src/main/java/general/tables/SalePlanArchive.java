package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "sale_plan_archive")
public class SalePlanArchive  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	@Id
	@Column(name = "sp_id")
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
	
	@Id
	@Column(name="month")
	private int month;
	
	@Id
	@Column(name="year")
	private int year;
	
	@Id
	@Column(name="bukrs")
	private String bukrs;
	
	

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
