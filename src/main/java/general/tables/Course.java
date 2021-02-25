package general.tables;

import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="course")
@javax.persistence.SequenceGenerator(name="seq_course_id",sequenceName="seq_course_id", allocationSize = 1)
public class Course{

	   
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_course_id")
	private Long c_id;   
	
	@Column(name="name_ru")
	private String name_ru; 

	@Column(name="created_date")
	private Date created_date;   
	
	@Column(name="updated_date")
	private Date updated_date;   

	@Column(name="created_by")
	private Long created_by;
	
	@Column(name="updated_by")
	private Long updated_by;   
	
	@Column(name="name_en")
	private String name_en;
	
	@Column(name="name_tr")
	private String name_tr;
	
	@Column(name="description")
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Course() {
		this.c_id = 0L;
	}   
	public Long getC_id() {
		return this.c_id;
	}

	public void setC_id(Long c_id) {
		this.c_id = c_id;
	}   
	public String getName_ru() {
		return this.name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}
	
	public Date getCreated_date() {
		return this.created_date;
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
	public String getName_en() {
		return this.name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}   
	public String getName_tr() {
		return this.name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}
	
	public String getName(String lang){
		if(lang.equals("en") && this.name_en.length() > 0){
			return this.name_en;
		}else if(lang.equals("tr") && this.name_tr.length() > 0){
			return this.name_tr;
		}
		
		return this.name_ru;
	}
}
