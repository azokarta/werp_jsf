package general.tables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "matnr_war")
@javax.persistence.SequenceGenerator(name="SEQ_MATNR_WAR_ID",sequenceName="SEQ_MATNR_WAR_ID", allocationSize = 1)
public class MatnrWar {	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_MATNR_WAR_ID")
    private Long id;
	
	@Column(name = "matnr_id")
    private Long matnr_id;
	
	@Column(name = "from_date")
    private Date from_date;
	
	@Column(name = "war_months")
    private int war_months;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
