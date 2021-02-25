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
@Table(name = "bonus")
@javax.persistence.SequenceGenerator(name="seq_bonus_id",sequenceName="seq_bonus_id", allocationSize = 1)
public class Bonus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9123072143427653012L;

	@Id
	@Column(name = "bonus_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_bonus_id")
	private Long bonus_id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "bonus_type_id")
	private Long bonus_type_id;
	
	@Column(name = "position_id")
	private Long position_id;
	
	@Column(name = "country_id")
	private Long country_id; 
	
	@Column(name = "business_area_id")
	private Long business_area_id;
	
	@Column(name = "from_num")
	private double from_num;
	
	@Column(name = "to_num")
	private double to_num;
	
	@Column(name = "coef")
	private double coef;
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "month")
	private int month; 
	
	@Column(name = "year")
	private int year;
	
	@Column(name = "req_value")
	private int req_value;
	
	@Column(name="matnr")
	private Long matnr;
	
	@Column(name="deposit")
	private Double deposit;
	

	@Column(name="category_id")
	private Long category_id;
	

	
	public static int BONUS_CAT_FROM_SELL = 1;
	public static int BONUS_CAT_FROM_WORK = 2;
	public static int BONUS_CAT_FROM_ACCESSORIES = 3;
	public static int BONUS_CAT_FROM_TOTAL = 4;
	
	
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	public int getTerm_in_month() {
		return term_in_month;
	}
	public void setTerm_in_month(int term_in_month) {
		this.term_in_month = term_in_month;
	}
	public Long getCreated_by() {
		return created_by;
	}
	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	@Column(name = "branch_id")
	private Long branch_id;	

	@Column(name = "term_in_month")
	private int term_in_month;	

	@Column(name = "created_by")
	private Long created_by;	

	@Column(name = "created_date")
	private Date created_date;
	
	
	
	
	
	public Long getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Long category_id) {
		this.category_id = category_id;
	}

	public Double getDeposit() {
		return deposit;
	}

	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Long getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(Long bonus_id) {
		this.bonus_id = bonus_id;
	}

	public Long getBonus_type_id() {
		return bonus_type_id;
	}

	public void setBonus_type_id(Long bonus_type_id) {
		this.bonus_type_id = bonus_type_id;
	}

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public double getFrom_num() {
		return from_num;
	}

	public void setFrom_num(double from_num) {
		this.from_num = from_num;
	}

	public double getTo_num() {
		return to_num;
	}

	public void setTo_num(double to_num) {
		this.to_num = to_num;
	}

	public double getCoef() {
		return coef;
	}

	public void setCoef(double coef) {
		this.coef = coef;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int m) {
		this.month = m;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int y) {
		this.year = y;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public int getReq_value() {
		return req_value;
	}

	public void setReq_value(int req_value) {
		this.req_value = req_value;
	}

	 
}
