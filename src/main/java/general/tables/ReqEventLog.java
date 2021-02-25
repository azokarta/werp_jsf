package general.tables;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;


@Entity
@Table(name="REQ_EVENT_LOG")
public class ReqEventLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="REQ_EVENT_LOG_ID_GENERATOR", sequenceName="SEQ_REQ_EVENT_LOG_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REQ_EVENT_LOG_ID_GENERATOR")
	private Long id;
	

	private Long request_id;
	private Date datetime;
	private Long staff_id;
	private Integer action_id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRequest_id() {
		return request_id;
	}
	public void setRequest_id(Long request_id) {
		this.request_id = request_id;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	public Integer getAction_id() {
		return action_id;
	}
	public void setAction_id(Integer action_id) {
		this.action_id = action_id;
	}
	
	public String getActionName(){
		return getActions().get(getAction_id());
	}
	
	public static final Integer ACTION_CREATE = 1;
	public static final Integer ACTION_VIEW = 2;
	public static final Integer ACTION_UPDATE = 3;
	public static final Integer ACTION_SEND = 4;
	public static final Integer ACTION_CONFIRM = 5;
	//public static final Integer ACTION_REFUSE = 6;
	public static final Integer ACTION_CLOSED = 7;
	
	public Map<Integer, String> getActions(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		out.put(ReqEventLog.ACTION_CREATE, "Создание");
		out.put(ReqEventLog.ACTION_VIEW, "Просмотр");
		out.put(ReqEventLog.ACTION_UPDATE, "Редактирование");
		out.put(ReqEventLog.ACTION_SEND, "Отправление");
		out.put(ReqEventLog.ACTION_CONFIRM, "Согласование");
		out.put(ReqEventLog.ACTION_CLOSED, "Закрыт");
		return out;
	}
	
	
	
	@ManyToOne(targetEntity=Staff.class,fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="staff_id",referencedColumnName="staff_id",updatable=false,insertable=false)
	private Staff staff;
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	
}