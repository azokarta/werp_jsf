package general.tables2;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;

@Entity 
@Table(name = "prikaz_attach") 
@javax.persistence.SequenceGenerator(name="seq_prikaz_attach_id",sequenceName="seq_prikaz_attach_id", allocationSize = 1)
public class PrikazAttach implements Serializable {
private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_prikaz_attach_id")
	@Column(name = "prikaz_attach_id")
	private Long prikaz_attach_id;
	
	@Column(name = "id_prikaz")
	private Long id_prikaz;
	
	
	@Column(name = "prikaz_attach")
	private Blob prikaz_attach;

	@Column(name = "name")
	private String name;
	
	@Column(name = "ext")
	private String ext;

	
	public Long getPrikaz_attach_id() {
		return prikaz_attach_id;
	}

	public void setPrikaz_attach_id(Long prikaz_attach_id) {
		this.prikaz_attach_id = prikaz_attach_id;
	}

	public Long getId_prikaz() {
		return id_prikaz;
	}

	public void setId_prikaz(Long id_prikaz) {
		this.id_prikaz = id_prikaz;
	}

	public Blob getPrikaz_attach() {
		return prikaz_attach;
	}

	public void setPrikaz_attach(Blob prikaz_attach) {
		this.prikaz_attach = prikaz_attach;
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
	
	
}
