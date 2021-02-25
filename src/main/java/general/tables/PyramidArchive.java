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
@Table(name = "pyramid_archive")
public class PyramidArchive  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Id
	@Column(name = "pyramid_id")
	private Long pyramid_id;
	
	@Id
	@Column(name = "bukrs")
	private String bukrs;	
	
	@Column(name = "business_area_id") 
	private Long business_area_id;
	
	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "parent_pyramid_id")
	private Long parent_pyramid_id;
	
	@Column(name = "branch_id")
	private Long branch_id;	
	
	@Id
	@Column(name = "month")
	private Integer month;
	
	@Id
	@Column(name = "year")
	private Integer year;
	
	@Column(name = "position_id")
	private Long position_id;


	@Column(name = "created_by")
	private Long created_by;
	
	@Column(name = "created_date")
	private Date created_date;
	
	@Column(name = "updated_by")
	private Long updated_by;
	
	@Column(name = "updated_date")
	private Date updated_date;
	
	public Long getPyramid_id() {
		return pyramid_id;
	}
	public void setPyramid_id(Long pyramid_id) {
		this.pyramid_id = pyramid_id;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public Long getBusiness_area_id() {
		return business_area_id;
	}
	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	public Long getParent_pyramid_id() {
		return parent_pyramid_id;
	}
	public void setParent_pyramid_id(Long parent_pyramid_id) {
		this.parent_pyramid_id = parent_pyramid_id;
	}
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer m) {
		this.month = m;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer y) {
		this.year = y;
	}
	public Long getPosition_id() {
		return position_id;
	}
	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
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
}
 