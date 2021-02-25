package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "serv_packet_war")
@javax.persistence.SequenceGenerator(name="SEQ_SERV_PACKET_WAR_ID",sequenceName="SEQ_SERV_PACKET_WAR_ID",allocationSize=1)
public class ServPacketWar {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SERV_PACKET_WAR_ID")
    private Long id;

	@Column(name = "sp_id")
	private Long sp_id;
	
	@Column(name = "matnr_id")
	private Long matnr_id;
	
	@Column(name = "info")
	private String info;
	
	@Column(name = "war_months")
	private int war_months;
	
	public int getWar_months() {
		return war_months;
	}

	public void setWar_months(int war_months) {
		this.war_months = war_months;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSp_id() {
		return sp_id;
	}

	public void setSp_id(Long sp_id) {
		this.sp_id = sp_id;
	}

	public Long getMatnr_id() {
		return matnr_id;
	}

	public void setMatnr_id(Long matnr_id) {
		this.matnr_id = matnr_id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
