package general.tables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "serv_con_matnr_war")
@javax.persistence.SequenceGenerator(name="SEQ_SERVCONMATNRWAR_ID",sequenceName="SEQ_SERVCONMATNRWAR_ID", allocationSize = 1)
public class ServConMatnrWar {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SERVCONMATNRWAR_ID")
    private Long id;
	
	@Column(name = "contract_id")
    private Long contract_id;
	
	@Column(name = "tovar_sn")
    private String tovar_sn;
	
	@Column(name = "matnr_id")
    private Long matnr_id;
	
	@Column(name = "from_date")
    private Date from_date;
	
	@Column(name = "to_date")
    private Date to_date;

	@Column(name = "war_months")
    private int war_months;
	
	@Column(name = "serv_id")
    private Long serv_id;

	public Date getTo_date() {
		return to_date;
	}

	public void setTo_date(Date to_date) {
		this.to_date = to_date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public String getTovar_sn() {
		return tovar_sn;
	}

	public void setTovar_sn(String tovar_sn) {
		this.tovar_sn = tovar_sn;
	}

	public Long getMatnr_id() {
		return matnr_id;
	}

	public void setMatnr_id(Long matnr_id) {
		this.matnr_id = matnr_id;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public int getWar_months() {
		return war_months;
	}

	public void setWar_months(int war_months) {
		this.war_months = war_months;
	}

	public Long getServ_id() {
		return serv_id;
	}

	public void setServ_id(Long serv_id) {
		this.serv_id = serv_id;
	}
	
}
