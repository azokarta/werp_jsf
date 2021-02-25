package general.tables;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "REQUEST_OUT")
public class RequestOut {
	@Id
	@SequenceGenerator(name="REQUEST_OUT_ID_GENERATOR", sequenceName="SEQ_REQUEST_OUT_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REQUEST_OUT_ID_GENERATOR")
	private Long id;
	
	
	private String note;
	private Integer status_id;
	private Long department_id;
	private Date created_at;
	private Date updated_at;
	private Long created_by;
	private Long updated_by;
	private String bukrs;
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Integer getStatus_id() {
		return status_id;
	}
	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RequestOut() {
		super();
		setCreated_at(Calendar.getInstance().getTime());
	}
	
	public static final Integer STATUS_OPENED = 1;
	public static final Integer STATUS_CLOSED = 2;
	
	public String getStatusName(){
		if(getStatus_id() != null){
			if(getStatus_id().equals(STATUS_OPENED)){
				return "Открыт";
			}else{
				return "Закрыт";
			}
		}
		
		return "";
	}
	
	public static final String CONTEXT = "request_out";
	
	@Transient
	private Staff creator;
	public Staff getCreator() {
		return creator;
	}
	public void setCreator(Staff creator) {
		this.creator = creator;
	}
	
}
