package general.tables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "serv_packet")
@javax.persistence.SequenceGenerator(name="SEQ_SERV_PACKET_ID",sequenceName="SEQ_SERV_PACKET_ID",allocationSize=1)
public class ServPacket {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SERV_PACKET_ID")
    private Long id;

	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "country_id")
	private Long country_id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "price")
	private double price;
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "master_bonus")
	private double master_bonus;
	
	@Column(name = "oper_bonus")
	private double oper_bonus;
	
	@Column(name = "start_date")
	private Date start_date;
	
	@Column(name = "end_date")
	private Date end_date;
	
	@Column(name = "active")
	private int active;
	
	@Column(name = "info")
	private String info;
	
	@Column(name = "discount")
	private double discount;
	
	@Column(name = "summ")
	private double summ;
	
	@Column(name = "tovar_category")
	private int tovar_category;
	
	@Column(name = "tovar_id")
	private Long tovar_id;

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

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public double getMaster_bonus() {
		return master_bonus;
	}

	public void setMaster_bonus(double master_bonus) {
		this.master_bonus = master_bonus;
	}

	public double getOper_bonus() {
		return oper_bonus;
	}

	public void setOper_bonus(double oper_bonus) {
		this.oper_bonus = oper_bonus;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getSumm() {
		return summ;
	}

	public void setSumm(double summ) {
		this.summ = summ;
	}

	public int getTovar_category() {
		return tovar_category;
	}

	public void setTovar_category(int tovar_category) {
		this.tovar_category = tovar_category;
	}

	public Long getTovar_id() {
		return tovar_id;
	}

	public void setTovar_id(Long tovar_id) {
		this.tovar_id = tovar_id;
	}	
	
}
