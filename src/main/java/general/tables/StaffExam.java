package general.tables;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: StaffExam
 *
 */
@Entity
@Table(name="staff_exam")
@javax.persistence.SequenceGenerator(name="seq_staff_exam_id",sequenceName="seq_staff_exam_id",allocationSize=1)
public class StaffExam implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_staff_exam_id")
	private Long se_id;
	private Long staff_id;
	private String title;
	private Double grade;
	private Long created_by;
	private Date created_date;
	private Date exam_date;
	private static final long serialVersionUID = 1L;

	public StaffExam() {
		super();
	}   
	public Long getSe_id() {
		return this.se_id;
	}

	public void setSe_id(Long se_id) {
		this.se_id = se_id;
	}   
	public Long getStaff_id() {
		return this.staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}   
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}   
	 
	public Long getCreated_by() {
		return this.created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}   
	public Date getCreated_date() {
		return this.created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Date getExam_date() {
		return exam_date;
	}
	public void setExam_date(Date exam_date) {
		this.exam_date = exam_date;
	}
	public Double getGrade() {
		return grade;
	}
	public void setGrade(Double grade) {
		this.grade = grade;
	}
   
}
