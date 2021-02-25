package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * The persistent class for the STAFF_EDUCATION database table.
 * 
 */
@Entity
@Table(name="STAFF_EDUCATION")
@NamedQuery(name="StaffEducation.findAll", query="SELECT s FROM StaffEducation s")
public class StaffEducation implements Serializable {
	private static final long serialVersionUID = 1L;
	

	@Id
	@SequenceGenerator(name="STAFF_EDUCATION_SEID_GENERATOR", sequenceName="SEQ_STAFF_EDUCATION_ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STAFF_EDUCATION_SEID_GENERATOR")
	@Column(name="SE_ID")
	private Long seId;

	@Column(name="BEGIN_YEAR")
	private int beginYear;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="EDU_LEVEL")
	private int eduLevel;

	@Column(name="END_YEAR")
	private BigDecimal endYear;

	private String faculty;

	@Column(name="INSTITUTION_NAME")
	private String institutionName;

	private String specialization;

	@Column(name="STAFF_ID")
	private Long staffId;

	@Column(name="UPDATED_BY")
	private Long updatedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	public StaffEducation() {
	}

	public Long getSeId() {
		return this.seId;
	}

	public void setSeId(Long seId) {
		this.seId = seId;
	}

	public int getBeginYear() {
		return this.beginYear;
	}

	public void setBeginYear(int beginYear) {
		this.beginYear = beginYear;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getEduLevel() {
		return this.eduLevel;
	}

	public void setEduLevel(int eduLevel) {
		this.eduLevel = eduLevel;
	}

	public BigDecimal getEndYear() {
		return this.endYear;
	}

	public void setEndYear(BigDecimal endYear) {
		this.endYear = endYear;
	}

	public String getFaculty() {
		return this.faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getInstitutionName() {
		return this.institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getSpecialization() {
		return this.specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public Long getStaffId() {
		return this.staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public Long getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public Map<Integer, String> getLevels(){
		HashMap<Integer, String> out = new HashMap<Integer, String>();
		out.put(10, "Высшее");
		out.put(20, "Неоконченное высшее");
		out.put(30, "Среднее специальное");
		out.put(40, "Среднее");
		
		return out;
	}
	
	public String getLevelName(){
		System.out.println(eduLevel);
		return getLevels().get(eduLevel);
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
}