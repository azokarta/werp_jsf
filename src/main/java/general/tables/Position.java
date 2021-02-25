package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "position")
@javax.persistence.SequenceGenerator(name = "seq_position_id", sequenceName = "seq_position_id", allocationSize = 1)
public class Position {

	public static final Long DEALER_POSITION_ID = 4L;
	public static final Long STAZHER_DEALER_POSITION_ID = 67L;
	public static final Long MANAGER_POSITION_ID = 3L;
	public static final Long DEMOSEC_POSITION_ID = 8L;
	public static final Long DIRECTOR_POSITION_ID = 10L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_position_id")
	private Long position_id;

	@Column(name = "spras")
	private String spras;

	@Column(name = "text")
	private String text;

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

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public String getSpras() {
		return spras;
	}

	public void setSpras(String spras) {
		this.spras = spras;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Position() {
		super();
		this.spras = "ru";
	}
	

	
	public String getName(String a_lang){
		if (a_lang.equals("en")) return this.text_en;
		else if (a_lang.equals("tr")) return this.text_tr;
		else return this.text;
		
	}

}
