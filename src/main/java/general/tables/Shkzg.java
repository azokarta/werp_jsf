package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shkzg_type")
public class Shkzg {
	@Id
	@Column(name = "shkzg")
	private String shkzg;
	
	@Column(name = "text10")
	private String text10;
	public String getShkzg() {
		return shkzg;
	}
	public void setShkzg(String shkzg) {
		this.shkzg = shkzg;
	}
	public String getText10() {
		return text10;
	}
	public void setText10(String text10) {
		this.text10 = text10;
	} 

}
