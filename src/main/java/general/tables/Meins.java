package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "meins_type")
@javax.persistence.SequenceGenerator(name="seq_meins_type_id",sequenceName="seq_meins_type_id",allocationSize=1)
public class Meins {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_meins_type_id")
	@Column(name = "meins")
	private Long meins;
	
	@Column(name = "text45")
	private String text45;
	
	@Column(name = "spras")
	private String spras;
	
	public Long getMeins() {
		return meins;
	}
	public void setMeins(Long meins) {
		this.meins = meins;
	}
	public String getText45() {
		return text45;
	}
	public void setText45(String text45) {
		this.text45 = text45;
	}
	public String getSpras() {
		return spras;
	}
	public void setSpras(String spras) {
		this.spras = spras;
	} 
}
