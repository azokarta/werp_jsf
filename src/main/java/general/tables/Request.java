package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * The persistent class for the REQUEST database table.
 * 
 */
@Entity
@Table(name="REQUEST")
public class Request implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5264373911032413817L;

	public Request() {
		super();
		// TODO Auto-generated constructor stub
		setCreated_at(Calendar.getInstance().getTime());
		this.res_branch_id = 0L;
	}



	private Date created_at;
	private Date updated_at;
	private String note;
	private String bukrs;
	private Integer status_id;
	private Long current_responsible;
	private Long department_id;
	private Long created_by;
	private Long updated_by;
	private Long last_responsible;
	private Long branch_id;
	private Long res_branch_id;

	@Id
	@SequenceGenerator(name="REQUEST_ID_GENERATOR", sequenceName="SEQ_REQUEST_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REQUEST_ID_GENERATOR")
	private Long id;
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
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

	public Long getCurrent_responsible() {
		return current_responsible;
	}

	public void setCurrent_responsible(Long current_responsible) {
		this.current_responsible = current_responsible;
	}

	public Long getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(Long department_id) {
		this.department_id = department_id;
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
	
	public String getStatusName(){
		if(getStatus_id() != null && getStatus_id() > 0){
			return getStatuses().get(getStatus_id());
		}
		
		return null;
	}
	
	public Long getLast_responsible() {
		return last_responsible;
	}

	public void setLast_responsible(Long last_responsible) {
		this.last_responsible = last_responsible;
	}
	
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	
	



	public Long getRes_branch_id() {
		return res_branch_id;
	}
	public void setRes_branch_id(Long res_branch_id) {
		this.res_branch_id = res_branch_id;
	}



	public static final String CONTEXT = "request";
	
	
	public static final Integer STATUS_1 = 1;// На создании
	//public static final Integer STATUS_2 = 2;// На рассмотрении
	public static final Integer STATUS_3 = 3;// На исполнении
	public static final Integer STATUS_4 = 4;// Исполнен
	//public static final Integer STATUS_5 = 5;// Отказано
	
	public Map<Integer, String> getStatuses(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		out.put(STATUS_1, "НОВЫЙ");
		//out.put(STATUS_2, "На рассмотрении");
		out.put(STATUS_3, "На исполнении");
		out.put(STATUS_4, "ЗАКРЫТЫЙ");
		return out;
	}
	

	
	@Transient
	Staff responsibleObject;

	public Staff getResponsibleObject() {
		return responsibleObject;
	}

	public void setResponsibleObject(Staff responsibleObject) {
		this.responsibleObject = responsibleObject;
	}
	
	@Transient
	private Staff creator;

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}
	
	
//	@OneToMany(mappedBy="requestObject",targetEntity=RequestMatnr.class,fetch=FetchType.LAZY)
//	private List<RequestMatnr> requestMatnrs;
//	public List<RequestMatnr> getRequestMatnrs() {
//		return requestMatnrs;
//	}
//	public void setRequestMatnrs(List<RequestMatnr> requestMatnrs) {
//		this.requestMatnrs = requestMatnrs;
//	}
	
}