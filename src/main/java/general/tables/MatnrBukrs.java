package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "matnr_bukrs")
@javax.persistence.SequenceGenerator(name="SEQ_MATNR_BUKRS",sequenceName="SEQ_MATNR_BUKRS", allocationSize = 1)
public class MatnrBukrs {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_MATNR_BUKRS")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "matnr")
	private Long matnr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	
	
}
