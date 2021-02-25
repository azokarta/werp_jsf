package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role")
@javax.persistence.SequenceGenerator(name="seq_role_id",sequenceName="seq_role_id",allocationSize=1)
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_role_id")
	private Long role_id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "text45")
	private String text45;

	public Long getRole_id() {
		return role_id;
	}

	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getText45() {
		return text45;
	}

	public void setText45(String text45) {
		this.text45 = text45;
	}
}
