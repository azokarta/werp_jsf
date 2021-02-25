package general.tables; 

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "skat")
public class Hkont implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "bukrs")
	private String bukrs;
	
	@Id
	@Column(name = "hkont")
	private String hkont;
	
	@Column(name = "text45")
	private String text45;

	@Column(name = "name_en")
	private String name_en;
	
	@Column(name = "name_tr")
	private String name_tr;
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "grp")
	private String grp;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getHkont() {
		return hkont;
	}
	public void setHkont(String hkont) {
		this.hkont = hkont;
	}
	public String getText45() {
		return text45;
	}
	public void setText45(String text45) {
		this.text45 = text45;
	}
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public String getGrp() {
		return grp;
	}
	public void setGrp(String grp) {
		this.grp = grp;
	}
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}
	public String getName_tr() {
		return name_tr;
	}
	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}
	
	
	public String getName(String a_lang){
		String waers = "";
		String text = "";
		if (this.waers!=null)
		{
			waers = this.waers;
		}
		if (a_lang==null || a_lang.equals("ru"))
		{
			text = this.text45 + " "+waers;		
		}
		else if (a_lang.equals("en"))
		{
			if (this.name_en==null) text = this.text45 + " "+waers;
			else text = this.name_en + " "+waers;
		}
		else if (a_lang.equals("tr"))
		{

			if (this.name_en==null) text = this.text45 + " "+waers;
			else text = this.name_tr + " "+waers;
		}
		return text;
	}
	
}
