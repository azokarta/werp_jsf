package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;

@Entity 
@Table(name = "message_attach") 
@javax.persistence.SequenceGenerator(name="seq_mess_attach_id",sequenceName="seq_mess_attach_id", allocationSize = 1)
public class MessageAttach implements Serializable {
private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_mess_attach_id")
	@Column(name = "attach_id")
	private Long attach_id;
	
	@Column(name = "mess_id")
	private Long mess_id;
	
	
	@Column(name = "mess_attach")
	private Blob mess_attach;

	
	@Column(name = "ext")
	private String ext;

	public Long getAttach_id() {
		return attach_id;
	}

	public void setAttach_id(Long attach_id) {
		this.attach_id = attach_id;
	}

	public Long getMess_id() {
		return mess_id;
	}

	public void setMess_id(Long mess_id) {
		this.mess_id = mess_id;
	}

	public Blob getMess_attach() {
		return mess_attach;
	}

	public void setMess_attach(Blob mess_attach) {
		this.mess_attach = mess_attach;
	}



	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
	
	
}
