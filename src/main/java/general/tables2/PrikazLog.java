package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "prikaz_log") 
@javax.persistence.SequenceGenerator(name="seq_prikaz_log_id",sequenceName="seq_prikaz_log_id", allocationSize = 1)
public class PrikazLog implements Serializable {
private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_prikaz_log_id")
	@Column(name = "prikaz_log_id")
	private Long prikaz_log_id;
	
	@Column(name = "id_prikaz")
	private Long id_prikaz;
	
	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "action_id")
	private int action_id;

	@Column(name = "note")
	private String note;

	public Long getPrikaz_log_id() {
		return prikaz_log_id;
	}

	public void setPrikaz_log_id(Long prikaz_log_id) {
		this.prikaz_log_id = prikaz_log_id;
	}

	public Long getId_prikaz() {
		return id_prikaz;
	}

	public void setId_prikaz(Long id_prikaz) {
		this.id_prikaz = id_prikaz;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public int getAction_id() {
		return action_id;
	}

	public void setAction_id(int action_id) {
		this.action_id = action_id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	
}


