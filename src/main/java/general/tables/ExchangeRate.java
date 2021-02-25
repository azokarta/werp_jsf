package general.tables; 

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "exchange_rate")
@javax.persistence.SequenceGenerator(name="seq_exchange_rate_id",sequenceName="seq_exchange_rate_id", allocationSize = 1)
public class ExchangeRate {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_exchange_rate_id")
	@Column(name = "exrate_id")
	private Long exrate_id;
	
	@Column(name = "exrate_date")
	private Date exrate_date;
	
	@Column(name = "main_currency")
	private String main_currency;
	
	@Column(name = "mc_value")
	private double mc_value;
	
	@Column(name = "secondary_currency")
	private String secondary_currency;
	
	@Column(name = "sc_value")
	private double sc_value;
	
	@Column(name = "type")
	private int type;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	
	
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getExrate_id() {
		return exrate_id;
	}

	public void setExrate_id(Long exrate_id) {
		this.exrate_id = exrate_id;
	}

	public Date getExrate_date() {
		return exrate_date;
	}

	public void setExrate_date(Date exrate_date) {
		this.exrate_date = exrate_date;
	}

	public String getMain_currency() {
		return main_currency;
	}

	public void setMain_currency(String main_currency) {
		this.main_currency = main_currency;
	}

	public double getMc_value() {
		return mc_value;
	}

	public void setMc_value(double mc_value) {
		this.mc_value = mc_value;
	}

	public String getSecondary_currency() {
		return secondary_currency;
	}

	public void setSecondary_currency(String secondary_currency) {
		this.secondary_currency = secondary_currency;
	}

	public double getSc_value() {
		return sc_value;
	}

	public void setSc_value(double sc_value) {
		this.sc_value = sc_value;
	}
	
	
}
