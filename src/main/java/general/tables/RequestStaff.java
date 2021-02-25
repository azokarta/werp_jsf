package general.tables;

import java.io.Serializable;

import javax.persistence.*;


@Entity
@Table(name="REQUEST_STAFF")
public class RequestStaff implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="REQUEST_STAFF_ID_GENERATOR", sequenceName="SEQ_REQUEST_STAFF_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REQUEST_STAFF_ID_GENERATOR")
	private Long id;
	

	private Long staff_id;
	private Long request_id;
	private Integer status_id;
	private int flag;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	public Long getRequest_id() {
		return request_id;
	}
	public void setRequest_id(Long request_id) {
		this.request_id = request_id;
	}
	public Integer getStatus_id() {
		return status_id;
	}
	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
	
}