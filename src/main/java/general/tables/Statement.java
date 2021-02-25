package general.tables;


import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "hrstatement")
@javax.persistence.SequenceGenerator(name="seq_statement_id",sequenceName="seq_statement_id",allocationSize=1)
public class Statement implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8163194587852160330L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_statement_id")
	private Long id;
	
	@Column(name = "fileu")
	private Blob fileu;
	
	@Column(name = "bukrs")
	private String bukrs;

	@Column(name = "branch_id")
	private Long branch_id;

	@Column(name = "dep")
	private Long dep;
	
	@Column(name = "data")
	private Date data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Blob getFileu() {
		return fileu;
	}

	public void setFileu(Blob fileu) {
		this.fileu = fileu;
	}


	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getDep() {
		return dep;
	}

	public void setDep(Long dep) {
		this.dep = dep;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}