package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "werks_type")
@javax.persistence.SequenceGenerator(name="seq_werks_type_id",sequenceName="seq_werks_type_id", allocationSize = 1)
public class Werks implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "bukrs")
	private String bukrs;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_werks_type_id")
	@Column(name = "werks")
	private Long werks;
	 
	@Column(name = "text45")
	private String text45;
	
	@Column(name = "is_main")
	private int is_main;
	 
	
	public int getIs_main() {
		return is_main;
	}
	public void setIs_main(int is_main) {
		this.is_main = is_main;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public Long getWerks() {
		return werks;
	}
	public void setWerks(Long werks) {
		this.werks = werks;
	} 
	public String getText45() {
		return text45;
	}
	public void setText45(String text45) {
		this.text45 = text45;
	}
	
}
