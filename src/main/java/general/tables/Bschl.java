package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "bschl_type")
public class Bschl {
	
	@Id
	@Column(name = "bschl")
	private String bschl;
	
	@Column(name = "shkzg")
	private String shkzg;
	
	@Column(name = "text45")
	private String text45;
	
	@Column(name = "koart")
	private String koart;
	
	@Column(name = "sort")
	private int sort;
	
	public String getBschl() {
		return bschl;
	}
	public void setBschl(String bschl) {
		this.bschl = bschl;
	}
	public String getShkzg() {
		return shkzg;
	}
	public void setShkzg(String shkzg) {
		this.shkzg = shkzg;
	}	
	public String getKoart() {
		return koart;
	}
	public void setKoart(String koart) {
		this.koart = koart;
	}
	public String getText45() {
		return text45;
	}
	public void setText45(String text45) {
		this.text45 = text45;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	
}
