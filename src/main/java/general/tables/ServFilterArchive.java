package general.tables;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "serv_filter_archive")
@javax.persistence.SequenceGenerator(name="SEQ_SFA_ID",sequenceName="SEQ_SFA_ID",allocationSize=1)
public class ServFilterArchive implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5803936247607401731L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SFA_ID")
    private Long id;
	
	public ServFilterArchive() {
		this.fno = 0;
	}
	
	@Column(name = "tovar_sn")
	private String tovar_sn;
	
	@Column(name = "CONTRACT_ID")
	private Long contract_id;
	
	@Column(name = "fno")
	private int fno;
	
	@Column(name = "SERV_ID")
	private Long serv_id;
	
	@Column(name = "SERV_DATE")
	private Date serv_date;

	@Column(name = "bukrs")
	private String bukrs;
	
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTovar_sn() {
		return tovar_sn;
	}

	public void setTovar_sn(String tovar_sn) {
		this.tovar_sn = tovar_sn;
	}

	public int getFno() {
		return fno;
	}

	public void setFno(int fno) {
		this.fno = fno;
	}

	public Long getServ_id() {
		return serv_id;
	}

	public void setServ_id(Long serv_id) {
		this.serv_id = serv_id;
	}

	public Date getServ_date() {
		return serv_date;
	}

	public void setServ_date(Date serv_date) {
		this.serv_date = serv_date;
	}
	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

}
