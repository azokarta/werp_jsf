package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "staff_course")
@javax.persistence.SequenceGenerator(name="seq_staff_course_id",sequenceName="seq_staff_course_id",allocationSize=1)
public class StaffCourse {
	
	public StaffCourse(){
		this.sc_id = 0L;
		this.staff_id = 0L;
		this.begin_date = null;
		this.finish_date = null;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_staff_course_id")
    private Long sc_id;
	
	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "course_id")
	private Long course_id;
	
	@Column(name = "begin_date", nullable = true)
	private Date begin_date;
	
	@Column(name = "finish_date", nullable = true)
	private Date finish_date;
	
	@Column(name = "created_by", nullable = true)
	private Long created_by;
	
	@Column(name = "created_date", nullable = true)
	private Date created_date;
	
	@Column(name = "updated_by", nullable = true)
	private Long updated_by;
	
	@Column(name = "updated_date", nullable = true)
	private Date updated_date;
	
	private Long educator_id;
	

	public Long getEducator_id() {
		return educator_id;
	}

	public void setEducator_id(Long educator_id) {
		this.educator_id = educator_id;
	}

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

	public Long getCourse_id() {
		return course_id;
	}

	public void setCourse_id(Long course_id) {
		this.course_id = course_id;
	}

	public Date getBegin_date() {
		return begin_date;
	}

	public void setBegin_date(Date begin_date) {
		this.begin_date = begin_date;
	}

	public Date getFinish_date() {
		return finish_date;
	}

	public void setFinish_date(Date finish_date) {
		this.finish_date = finish_date;
	}

	public Long getSc_id() {
		return sc_id;
	}
}
