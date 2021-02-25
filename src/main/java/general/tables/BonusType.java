package general.tables;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bonus_type")
public class BonusType {
	@Id
	@Column(name = "bonus_type_id")
	private Long bonus_type_id;
	
	@Column(name = "text45")
	private String text45;
	
	@Column(name = "spras")
	private String spras;


	@Column(name = "text_en")
	private String text_en;


	@Column(name = "text_tr")
	private String text_tr;
	
	
	

	public String getText_en() {
		return text_en;
	}

	public void setText_en(String text_en) {
		this.text_en = text_en;
	}

	public String getText_tr() {
		return text_tr;
	}

	public void setText_tr(String text_tr) {
		this.text_tr = text_tr;
	}

	public Long getBonus_type_id() {
		return bonus_type_id;
	}

	public void setBonus_type_id(Long bonus_type_id) {
		this.bonus_type_id = bonus_type_id;
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
	
	public String getName(String a_lang){
		
		if (a_lang.equals("en")) return this.text_en;
		else if (a_lang.equals("tr")) return this.text_tr;
		else return this.text45;
		
	}
}