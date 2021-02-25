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
@Table(name = "promotion")
@javax.persistence.SequenceGenerator(name="seq_promotion_id",sequenceName="seq_promotion_id",allocationSize=1)
public class Promotion implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3336301056515395695L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_promotion_id")
	private Long id;
	
	@Column(name = "pm_number")
	private Long pm_number;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "date_start")
	private Date date_start;
	
	@Column(name = "date_end")
	private Date date_end;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "country_id")
	private Long country_id;
	
	@Column(name = "region_id")
	private Long region_id;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "matnr")
	private Long matnr;
	
	@Column(name = "from_dealer")
	private double from_dealer;
	
	@Column(name = "fd_currency")
	private String fd_currency;
	
	@Column(name = "discount")
	private double discount;
	
	@Column(name = "pm_type")
	private Long pm_type;
	
	@Column(name = "bonus")
	private Long bonus;
	
	@Column(name = "pm_scope")
	private String pm_scope;
	
	@Column(name = "isactive")
	private Long is_active;
	
	@Column(name = "info")
	private String info;
	
	@Column(name = "contract_type")
	private Long contract_type;

	public Long getContract_type() {
		return contract_type;
	}

	public void setContract_type(Long contract_type) {
		this.contract_type = contract_type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPm_number() {
		return pm_number;
	}

	public void setPm_number(Long pm_number) {
		this.pm_number = pm_number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate_start() {
		return date_start;
	}

	public void setDate_start(Date date_start) {
		this.date_start = date_start;
	}

	public Date getDate_end() {
		return date_end;
	}

	public void setDate_end(Date date_end) {
		this.date_end = date_end;
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

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public double getFrom_dealer() {
		return from_dealer;
	}

	public void setFrom_dealer(double from_dealer) {
		this.from_dealer = from_dealer;
	}

	public String getFd_currency() {
		return fd_currency;
	}

	public void setFd_currency(String fd_currency) {
		this.fd_currency = fd_currency;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public Long getPm_type() {
		return pm_type;
	}

	public void setPm_type(Long pm_type) {
		this.pm_type = pm_type;
	}

	public Long getBonus() {
		return bonus;
	}

	public void setBonus(Long bonus) {
		this.bonus = bonus;
	}

	public String getPm_scope() {
		return pm_scope;
	}

	public void setPm_scope(String pm_scope) {
		this.pm_scope = pm_scope;
	}

	public Long getIs_active() {
		return is_active;
	}

	public void setIs_active(Long isActive) {
		this.is_active = isActive;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Long region_id) {
		this.region_id = region_id;
	}
}
