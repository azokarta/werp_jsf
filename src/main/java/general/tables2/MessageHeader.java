package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

@Entity 
@Table(name = "message_header") 
@javax.persistence.SequenceGenerator(name="seq_mess_id",sequenceName="seq_mess_id", allocationSize = 1)
public class MessageHeader implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_mess_id")
	@Column(name = "mess_id")
	private Long mess_id;
	
	@Column(name = "mess_from")
	private Long mess_from;
	
	
	@Column(name = "mess_text")
	private String mess_text;
	
	@Column(name = "mess_date")
	private Date mess_date;
	
	@Column(name = "mess_head_text")
	private String mess_head_text;
	
	@Column(name = "mess_to_names")
	private String mess_to_names;
	
	

	public String getMess_to_names() {
		return mess_to_names;
	}

	public void setMess_to_names(String mess_to_names) {
		this.mess_to_names = mess_to_names;
	}

	public Long getMess_id() {
		return mess_id;
	}

	public void setMess_id(Long mess_id) {
		this.mess_id = mess_id;
	}

	public Long getMess_from() {
		return mess_from;
	}

	public void setMess_from(Long mess_from) {
		this.mess_from = mess_from;
	}

	public String getMess_text() {
		return mess_text;
	}




	public void setMess_text(String mess_text) {
		this.mess_text = mess_text;
	}

	public Date getMess_date() {
		return mess_date;
	}

	public void setMess_date(Date mess_date) {
		this.mess_date = mess_date;
	}

	public String getMess_head_text() {
		return mess_head_text;
	}

	public void setMess_head_text(String mess_head_text) {
		this.mess_head_text = mess_head_text;
	}
	
	
}
