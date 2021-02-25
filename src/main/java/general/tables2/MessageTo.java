package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "message_to") 
@javax.persistence.SequenceGenerator(name="seq_message_to_id",sequenceName="seq_message_to_id", allocationSize = 1)
public class MessageTo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_message_to_id")
	@Column(name = "message_to_id")
	private Long message_to_id;	
	
	@Column(name = "mess_id")
	private Long mess_id;
	
	@Column(name = "read_bool")
	private int read_bool;
	
	@Column(name = "mess_to")
	private Long mess_to;

	public Long getMessage_to_id() {
		return message_to_id;
	}

	public void setMessage_to_id(Long message_to_id) {
		this.message_to_id = message_to_id;
	}

	public Long getMess_id() {
		return mess_id;
	}

	public void setMess_id(Long mess_id) {
		this.mess_id = mess_id;
	}

	public int getRead_bool() {
		return read_bool;
	}

	public void setRead_bool(int read_bool) {
		this.read_bool = read_bool;
	}

	public Long getMess_to() {
		return mess_to;
	}

	public void setMess_to(Long mess_to) {
		this.mess_to = mess_to;
	}
	
	
}
