package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "message_group_user")
@javax.persistence.SequenceGenerator(name="seq_mess_group_user_id",sequenceName="seq_mess_group_user_id", allocationSize = 1)
public class MessageGroupUser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_mess_group_user_id")
	@Column(name = "mgu_id")
	private Long mgu_id;
	
	@Column(name = "group_id")
	private Long group_id;	
	
	@Column(name = "user_id")
	private Long user_id;

	public Long getMgu_id() {
		return mgu_id;
	}

	public void setMgu_id(Long mgu_id) {
		this.mgu_id = mgu_id;
	}

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	

	

}
