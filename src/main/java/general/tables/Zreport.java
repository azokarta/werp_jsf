package general.tables;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.sql.Blob;

@Entity
@Table(name="zreport")
@javax.persistence.SequenceGenerator(name="seq_zreport_id",sequenceName="seq_zreport_id", allocationSize = 1)
public class Zreport {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_zreport_id")
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="ext")
	private String ext;
	
	@Column(name="fileu")
	private Blob fileu;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Blob getFileu() {
		return fileu;
	}

	public void setFileu(Blob fileu) {
		this.fileu = fileu;
	}
	
	
}

