package general.tables;

import general.GeneralUtil;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "temp_payroll_archive") 
public class TempPayrollArchive implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	@Id
	@Column(name = "tp_id")
	private Long tp_id;	
	
	@Column(name = "bukrs")
	private String bukrs; 

	@Column(name = "gjahr")
	private int gjahr;

	@Column(name = "monat")
	private int monat;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "amount")
	private double amount;	
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "bonus_id")
	private Long bonus_id;
	
	@Column(name = "type")
	private int type;
	
	@Column(name = "salary_id")
	private Long salary_id;
	
	@Column(name = "from_date")
	private Date from_date;
	
	@Column(name = "to_date")
	private Date to_date;
	
	
	@Column(name = "kursf")
	private double kursf;
	
	@Column(name = "customer_id")
	private Long customer_id;
	
	@Column(name = "branch_name")
	private String branch_name;
	
	@Column(name = "staff_name")
	private String staff_name;
	
	@Column(name = "position_id")
	private Long position_id;
	
	@Column(name = "text45")
	private String text45;
	
	@Column(name = "matnr")
	private Long matnr;
	
	@Column(name = "matnr_count")
	private int matnr_count;
	
	@Column(name = "plan_amount")
	private double plan_amount;
	
	@Column(name = "fact_amount")
	private double fact_amount;
	
	@Column(name = "bldat")
	private Date bldat;
	
	@Column(name = "drcrk")
	private String drcrk;
	
	@Column(name = "contract_number")
	private Long contract_number;
	
	@Column(name = "exp_cus_id")
	private Long exp_cus_id;
	
	public Long getExp_cus_id() {
		return exp_cus_id;
	}

	public void setExp_cus_id(Long exp_cus_id) {
		this.exp_cus_id = exp_cus_id;
	}

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}

	public String getDrcrk() {
		return drcrk;
	}

	public void setDrcrk(String drcrk) {
		this.drcrk = drcrk;
	}

	public Date getBldat() {
		return bldat;
	}

	public void setBldat(Date bldat) {		
		this.bldat = GeneralUtil.removeTime(bldat);
	}
	
	public double getPlan_amount() {
		return plan_amount;
	}

	public void setPlan_amount(double plan_amount) {
		this.plan_amount = plan_amount;
	}

	public double getFact_amount() {
		return fact_amount;
	}

	public void setFact_amount(double fact_amount) {
		this.fact_amount = fact_amount;
	}
	public String getText45() {
		return text45;
	}

	public void setText45(String text45) {
		this.text45 = text45;
	}
	
	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}
	
	public String getBranch_name() {
		return branch_name;
	}

	public void setBranch_name(String branch_name) {
		this.branch_name = branch_name;
	}

	public String getStaff_name() {
		return staff_name;
	}

	public void setStaff_name(String staff_name) {
		this.staff_name = staff_name;
	}


	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}


	public double getKursf() {
		return kursf;
	}

	public void setKursf(double kursf) {
		this.kursf = kursf;
	}

	public Long getTp_id() {
		return tp_id;
	}

	public void setTp_id(Long tp_id) {
		this.tp_id = tp_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public int getGjahr() {
		return gjahr;
	}

	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}

	public int getMonat() {
		return monat;
	}

	public void setMonat(int monat) {
		this.monat = monat;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(Long bonus_id) {
		this.bonus_id = bonus_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getSalary_id() {
		return salary_id;
	}

	public void setSalary_id(Long salary_id) {
		this.salary_id = salary_id;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = GeneralUtil.removeTime(from_date);
	}

	public Date getTo_date() {
		return to_date;
	}

	public void setTo_date(Date to_date) {
		this.to_date = GeneralUtil.removeTime(to_date);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public int getMatnr_count() {
		return matnr_count;
	}

	public void setMatnr_count(int matnr_count) {
		this.matnr_count = matnr_count;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((bonus_id == null) ? 0 : bonus_id.hashCode());
		result = prime * result
				+ ((branch_id == null) ? 0 : branch_id.hashCode());
		result = prime * result
				+ ((branch_name == null) ? 0 : branch_name.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result
				+ ((customer_id == null) ? 0 : customer_id.hashCode());
		result = prime * result
				+ ((from_date == null) ? 0 : from_date.hashCode());
		result = prime * result + gjahr;
		temp = Double.doubleToLongBits(kursf);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + monat;
		result = prime * result
				+ ((position_id == null) ? 0 : position_id.hashCode());
		result = prime * result
				+ ((salary_id == null) ? 0 : salary_id.hashCode());
		result = prime * result
				+ ((staff_id == null) ? 0 : staff_id.hashCode());
		result = prime * result
				+ ((staff_name == null) ? 0 : staff_name.hashCode());
		result = prime * result + ((to_date == null) ? 0 : to_date.hashCode());
		result = prime * result + ((tp_id == null) ? 0 : tp_id.hashCode());
		result = prime * result + type;
		result = prime * result + ((waers == null) ? 0 : waers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TempPayrollArchive other = (TempPayrollArchive) obj;
		if (Double.doubleToLongBits(amount) != Double
				.doubleToLongBits(other.amount))
			return false;
		if (bonus_id == null) {
			if (other.bonus_id != null)
				return false;
		} else if (!bonus_id.equals(other.bonus_id))
			return false;
		if (branch_id == null) {
			if (other.branch_id != null)
				return false;
		} else if (!branch_id.equals(other.branch_id))
			return false;
		if (branch_name == null) {
			if (other.branch_name != null)
				return false;
		} else if (!branch_name.equals(other.branch_name))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (customer_id == null) {
			if (other.customer_id != null)
				return false;
		} else if (!customer_id.equals(other.customer_id))
			return false;
		if (from_date == null) {
			if (other.from_date != null)
				return false;
		} else if (!from_date.equals(other.from_date))
			return false;
		if (gjahr != other.gjahr)
			return false;
		if (Double.doubleToLongBits(kursf) != Double
				.doubleToLongBits(other.kursf))
			return false;
		if (monat != other.monat)
			return false;
		if (position_id == null) {
			if (other.position_id != null)
				return false;
		} else if (!position_id.equals(other.position_id))
			return false;
		if (salary_id == null) {
			if (other.salary_id != null)
				return false;
		} else if (!salary_id.equals(other.salary_id))
			return false;
		if (staff_id == null) {
			if (other.staff_id != null)
				return false;
		} else if (!staff_id.equals(other.staff_id))
			return false;
		if (staff_name == null) {
			if (other.staff_name != null)
				return false;
		} else if (!staff_name.equals(other.staff_name))
			return false;
		if (to_date == null) {
			if (other.to_date != null)
				return false;
		} else if (!to_date.equals(other.to_date))
			return false;
		if (tp_id == null) {
			if (other.tp_id != null)
				return false;
		} else if (!tp_id.equals(other.tp_id))
			return false;
		if (type != other.type)
			return false;
		if (waers == null) {
			if (other.waers != null)
				return false;
		} else if (!waers.equals(other.waers))
			return false;
		return true;
	}

	
	
	
	
}