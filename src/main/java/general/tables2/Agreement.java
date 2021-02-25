package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "agreement") 
@javax.persistence.SequenceGenerator(name="seq_agree_id",sequenceName="seq_agree_id", allocationSize = 1)
public class Agreement implements Serializable {
private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_agree_id")
	@Column(name = "agree_id")
	private Long agree_id;
	
	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "step")
	private int step;

	@Column(name = "current_a")
	private int current_a;
	
	@Column(name = "context_a")
	private String context_a;
	
	@Column(name = "context_id")
	private Long context_id;
	
	@Column(name = "status_id")
	private int status_id;
	
	@Column(name = "note")
	private String note;
	
	
	public static int waiting_status = 1;
	public static int confirmed_status = 2;
	public static int rejected_status=3;
	

	public int getStatus_id() {
		return status_id;
	}

	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getAgree_id() {
		return agree_id;
	}

	public void setAgree_id(Long agree_id) {
		this.agree_id = agree_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getCurrent_a() {
		return current_a;
	}

	public void setCurrent_a(int current_a) {
		this.current_a = current_a;
	}

	public String getContext_a() {
		return context_a;
	}

	public void setContext_a(String context_a) {
		this.context_a = context_a;
	}

	public Long getContext_id() {
		return context_id;
	}

	public void setContext_id(Long context_id) {
		this.context_id = context_id;
	}
	
	
}


