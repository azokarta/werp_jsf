package general.tables; 

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "blart_type")
public class Blart implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "bukrs")
	private String bukrs;
	
	@Id
	@Column(name = "blart")
	private String blart;
	
	@Column(name = "text45")
	private String text45;
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getBlart() {
		return blart;
	}
	public void setBlart(String blart) {
		this.blart = blart;
	}
	public String getText45() {
		return text45;
	}
	public void setText45(String text45) {
		this.text45 = text45;
	} 
	
}