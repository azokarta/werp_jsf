package general.tables2;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "message_group") 
@javax.persistence.SequenceGenerator(name="seq_mess_group_id",sequenceName="seq_mess_group_id", allocationSize = 1)
public class MessageGroup implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_mess_group_id")
	@Column(name = "group_id")
	private Long group_id;	
	
	@Column(name = "group_name")
	private String group_name;

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	

}
