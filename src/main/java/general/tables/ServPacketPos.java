package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "serv_packet_pos")
@javax.persistence.SequenceGenerator(name="SEQ_SERV_PACKET_POS_ID",sequenceName="SEQ_SERV_PACKET_POS_ID",allocationSize=1)
public class ServPacketPos {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SERV_PACKET_POS_ID")
    private Long id;

	@Column(name = "sp_id")
	private Long sp_id;
	
	@Column(name = "matnr_id")
	private Long matnr_id;
	
	@Column(name = "operation")
	private Long operation;
	
	@Column(name = "info")
	private String info;
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Column(name = "price")
	private double price;
	
	@Column(name = "quantity")
	private Long quantity;
	
	@Column(name = "summ")
	private double summ;
	
	@Column(name = "new_war_inm")
	private int new_war_inm;
	
	@Column(name = "waers")
	private String waers;

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

	public Long getOperation() {
		return operation;
	}

	public void setOperation(Long operation) {
		this.operation = operation;
	}

	

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public double getSumm() {
		return summ;
	}

	public void setSumm(double summ) {
		this.summ = summ;
	}

	public int getNew_war_inm() {
		return new_war_inm;
	}

	public void setNew_war_inm(int new_war_inm) {
		this.new_war_inm = new_war_inm;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}
	
}
