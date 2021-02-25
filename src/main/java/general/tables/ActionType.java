package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACTION_TYPE")
public class ActionType {
	@Id
	@Column(name = "action_id")
	private Long action_id;
	
	@Column(name = "text10")
	private String text10;

	public Long getAction_id() {
		return action_id;
	}

	public void setAction_id(Long action_id) {
		this.action_id = action_id;
	}

	public String getText10() {
		return text10;
	}

	public void setText10(String text10) {
		this.text10 = text10;
	}
}
