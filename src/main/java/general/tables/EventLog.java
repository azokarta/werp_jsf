package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name="EVENT_LOG")
public class EventLog implements Serializable {
	public static final Integer TYPE_WARNING = 1;// Предупреждение
	
	public Map<Integer, String> getTypes(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		
		out.put(EventLog.TYPE_WARNING, "Предупреждение");
		return out;
	}
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SEQ_EVENT_LOG_ID_GENERATOR", sequenceName="SEQ_EVENT_LOG_ID",allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_EVENT_LOG_ID_GENERATOR")
	private long id;


	private Integer type;
	private String msg;
	private Timestamp datetime;
	private Long staff_id;
	private String bukrs;
	private Long transaction_id;
	
	public Long getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Timestamp getDatetime() {
		return datetime;
	}
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	public EventLog() {
		super();
	}
	public EventLog(String bukrs,Integer type, String msg, Long staff_id) {
		super();
		this.bukrs = bukrs;
		this.type = type;
		this.msg = msg;
		this.staff_id = staff_id;
	}
	
	public String getTypeName(){
		return getTypes().get(getType());
	}
	
	@ManyToOne
	@JoinColumn(name="STAFF_ID",referencedColumnName="staff_id",updatable=false,insertable=false)
	private Staff staff;
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	

}