package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "price_list")
@javax.persistence.SequenceGenerator(name = "seq_price_list_id", sequenceName = "seq_price_list_id", allocationSize = 1)
public class PriceList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 883659288730792878L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_price_list_id")
	private Long price_list_id;

	@Column(name = "bukrs")
	private String bukrs;

	@Column(name = "price")
	private double price;

	@Column(name = "matnr")
	private Long matnr;

	@Column(name = "month")
	private int month;

	@Column(name = "from_date")
	private Date from_date;

	@Column(name = "country_id")
	private Long country_id;

	@Column(name = "waers")
	private String waers;

	@Column(name = "customer_id")
	private Long customer_id;

	@Column(name = "active")
	private Long active;

	@Column(name = "prem_div")
	private Long prem_div;

	@Column(name = "branch_id")
	private Long branch_id;	
	
	@Column(name = "MONTH_TYPE")
	private Long month_type;	
	
	@Column(name = "to_date")
	private Date to_date;
	

	@Column(name = "trade_in")
	private int tradeIn;
	
	@Column(name = "bank_partner_id")
	private Long bankPartnerId;
	
	

	public Long getBankPartnerId() {
		return bankPartnerId;
	}

	public void setBankPartnerId(Long bankPartnerId) {
		this.bankPartnerId = bankPartnerId;
	}

	public int getTradeIn() {
		return tradeIn;
	}

	public void setTradeIn(int tradeIn) {
		this.tradeIn = tradeIn;
	}
	
	public Date getTo_date() {
		return to_date;
	}
	public void setTo_date(Date to_date) {
		this.to_date = to_date;
	}
	
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getMonth_type() {
		return month_type;
	}
	public void setMonth_type(Long month_type) {
		this.month_type = month_type;
	}

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumns({ //@JoinColumn(name = "bukrs", referencedColumnName
	 * ="bukrs", insertable = false, updatable = false),
	 * 
	 * })
	 */
	@ManyToOne(targetEntity=Matnr.class,fetch=FetchType.LAZY)
	@JoinColumn(name = "matnr", referencedColumnName = "matnr", insertable = false, updatable = false)
	private Matnr rmatnr;
	public Matnr getRmatnr() {
		return rmatnr;
	}
	public void setRmatnr(Matnr rmatnr) {
		this.rmatnr = rmatnr;
	}

	public Long getPrice_list_id() {
		return price_list_id;
	}

	public void setPrice_list_id(Long price_list_id) {
		this.price_list_id = price_list_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
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

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getActive() {
		return active;
	}

	public void setActive(Long active) {
		this.active = active;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public Long getPrem_div() {
		return prem_div;
	}

	public void setPrem_div(Long prem_div) {
		this.prem_div = prem_div;
	}

	@Transient
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

	public static final double EPSILON = 0.20;
	public static final double SIGMA = 0.60;

}
