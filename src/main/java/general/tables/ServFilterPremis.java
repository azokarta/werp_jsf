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
@Table(name = "SERV_FILTER_PREMIS")
@javax.persistence.SequenceGenerator(name="SERV_PREMI_FITER_ID",sequenceName="SERV_PREMI_FITER_ID",allocationSize=1)
public class ServFilterPremis implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7434292203913825089L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SERV_PREMI_FITER_ID")
    private Long id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "date_start")
	private Date date_start;
	
	@Column(name = "fc")
	private int fc;
	
	@Column(name = "mc")
	private int mc;
	
	@Column(name = "office")
	private double office;
	
	@Column(name = "master")
	private double master;
	
	@Column(name = "operator")
	private double operator;
	
	@Column(name = "total")
	private double total;
	
	@Column(name = "discount")
	private double discount;
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "country_id")
	private Long country_id;

	
	public int getFc() {
		return fc;
	}

	public void setFc(int fc) {
		this.fc = fc;
	}

	public int getMc() {
		return mc;
	}

	public void setMc(int mc) {
		this.mc = mc;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate_start() {
		return date_start;
	}

	public void setDate_start(Date date_start) {
		this.date_start = date_start;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public double getMaster() {
		return master;
	}

	public void setMaster(double master) {
		this.master = master;
	}

	public double getOperator() {
		return operator;
	}

	public void setOperator(double operator) {
		this.operator = operator;
	}

	public double getOffice() {
		return office;
	}

	public void setOffice(double office) {
		this.office = office;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}
	
	
}
