package general.tables; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class Bukrs {
	
	@Id
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "spras")
	private String spras;
	
	@Column(name = "name")
	private String name;
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getSpras() {
		return spras;
	}
	public void setSpras(String spras) {
		this.spras = spras;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
}
