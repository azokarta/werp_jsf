package general.tables;

import general.GeneralUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "payroll")
@javax.persistence.SequenceGenerator(name="seq_payroll_id",sequenceName="seq_payroll_id",allocationSize=1)
public class Payroll {
	
	public Payroll(){
		this.bonus_type_id = 0;
		this.matnr_count = 0;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_payroll_id")
    private Long payroll_id;

	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "drcrk")
	private String drcrk;
	
	@Column(name = "dmbtr")
	private double dmbtr;
	
	@Column(name = "text45", nullable = true)
	private String text45;
	
	@Column(name = "salary_id", nullable = true)
	private Long salary_id;
	
	@Column(name = "payroll_date")
	private Date payroll_date;
	
	@Column(name = "monat")
	private int monat;
	
	@Column(name = "gjahr")
	private int gjahr;
	
	@Column(name = "bonus_id") 
	private Long bonus_id;
	
	@Column(name = "waers") 
	private String waers;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "bonus_type_id")
	private int bonus_type_id;
	
	@Column(name = "position_id")
	private Long position_id;
	
	@Column(name = "matnr_count")
	private int matnr_count;
	
	@Column(name = "plan_amount")
	private double plan_amount;
	
	@Column(name = "fact_amount")
	private double fact_amount;
	
	@Column(name = "bldat")
	private Date bldat;
	
	@Column(name = "contract_number")
	private Long contract_number;
	
	@Column(name = "approve")
	private int approve;
	
	@Column(name = "parent_payroll_id")
	private Long parent_payroll_id;
	
	@Column(name = "manager_id")
	private Long manager_id;
	
	@Column(name = "trainer_id")
	private Long trainer_id;
	
	@Column(name = "director_id")
	private Long director_id;
	
	@Column(name = "main_trainer_id")
	private Long main_trainer_id;
	
	@Column(name = "coordinator_id")
	private Long coordinator_id;
	
	@Column(name = "approved_by")
	private Long approved_by;
	
	@Column(name = "created_by")
	private Long created_by;
	
	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	@Column(name = "awkey")
	private Long awkey;
	
	public String getApproveName(String a_lang)
	{
		String name = "";
		//if(a_lang==null || a_lang.length()==0 || a_lang.equals("") || a_lang.equals("ru"))
		//{
			if (approve==0)
			{
				if (a_lang.equals("en")) name = "Accrued"; else name = "Начислено";
			}
			else if (approve==1)
			{
				if (a_lang.equals("en")) name = "Deposited"; else name = "Депозит";
			}
			else if (approve==2)
			{
				if (a_lang.equals("en")) name = "Rejected"; else name = "Отклонено";
			}
			else if (approve==3)
			{
				if (a_lang.equals("en")) name = "Advance request"; else name = "Запрос на Аванс";
			}
			else if (approve==4)
			{
				if (a_lang.equals("en")) name = "Approved advance"; else name = "Аванс(Одобренный)";
			}
			else if (approve==5)
			{
				if (a_lang.equals("en")) name = "In process"; else name = "Б/У на одобрении";
			}
			else if (approve==6)
			{
				if (a_lang.equals("en")) name = "Request to accrual"; else name = "Запрос на Начисление";
			}
			else if (approve==9)
			{
				if (a_lang.equals("en")) name = "Request to deduction"; else name = "Запрос на Удержание";
			}
			else if (approve==8)
			{
				if (a_lang.equals("en")) name = "Loan"; else name = "Долг";
			}
			
			
			
		//}
		
		return name;
	}
	
	public Long getApproved_by() {
		return approved_by;
	}

	public void setApproved_by(Long approved_by) {
		this.approved_by = approved_by;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Long getManager_id() {
		return manager_id;
	}

	public void setManager_id(Long manager_id) {
		this.manager_id = manager_id;
	}

	public Long getTrainer_id() {
		return trainer_id;
	}

	public void setTrainer_id(Long trainer_id) {
		this.trainer_id = trainer_id;
	}

	public Long getDirector_id() {
		return director_id;
	}

	public void setDirector_id(Long director_id) {
		this.director_id = director_id;
	}

	public Long getMain_trainer_id() {
		return main_trainer_id;
	}

	public void setMain_trainer_id(Long main_trainer_id) {
		this.main_trainer_id = main_trainer_id;
	}

	public Long getCoordinator_id() {
		return coordinator_id;
	}

	public void setCoordinator_id(Long coordinator_id) {
		this.coordinator_id = coordinator_id;
	}

	public int getApprove() {
		return approve;
	}

	public void setApprove(int approve) {
		this.approve = approve;
	}

	public Long getParent_payroll_id() {
		return parent_payroll_id;
	}

	public void setParent_payroll_id(Long parent_payroll_id) {
		this.parent_payroll_id = parent_payroll_id;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public int getBonus_type_id() {
		return bonus_type_id;
	}
	

	public void setBonus_type_id(int bonus_type_id) {
		this.bonus_type_id = bonus_type_id;
	}


	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public int getMatnr_count() {
		return matnr_count;
	}

	public void setMatnr_count(int matnr_count) {
		this.matnr_count = matnr_count;
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

	public Date getBldat() {
		return bldat;
	}

	public void setBldat(Date bldat) {
		this.bldat = GeneralUtil.removeTime(bldat);
	}

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
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

	public int getMonat() {
		return monat;
	}

	public void setMonat(int monat) {
		this.monat = monat;
	}

	public int getGjahr() {
		return gjahr;
	}

	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}

	public Long getPayroll_id() {
		return payroll_id;
	}

	public void setPayroll_id(Long payroll_id) {
		this.payroll_id = payroll_id;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getDrcrk() {
		return drcrk;
	}

	public void setDrcrk(String drcrk) {
		this.drcrk = drcrk;
	}

	public double getDmbtr() {
		return dmbtr;
	}

	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}

	public String getText45() {
		return text45;
	}

	public void setText45(String text45) {
		this.text45 = text45;
	}

	public Long getSalary_id() {
		return salary_id;
	}

	public void setSalary_id(Long salary_id) {
		this.salary_id = salary_id;
	}

	public Date getPayroll_date() {
		return payroll_date;
	}

	public void setPayroll_date(Date payroll_date) {
		this.payroll_date = GeneralUtil.removeTime(payroll_date);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bonus_id == null) ? 0 : bonus_id.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		long temp;
		temp = Double.doubleToLongBits(dmbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((drcrk == null) ? 0 : drcrk.hashCode());
		result = prime * result + gjahr;
		result = prime * result + monat;
		result = prime * result
				+ ((payroll_date == null) ? 0 : payroll_date.hashCode());
		result = prime * result
				+ ((payroll_id == null) ? 0 : payroll_id.hashCode());
		result = prime * result
				+ ((salary_id == null) ? 0 : salary_id.hashCode());
		result = prime * result
				+ ((staff_id == null) ? 0 : staff_id.hashCode());
		result = prime * result + ((text45 == null) ? 0 : text45.hashCode());
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
		Payroll other = (Payroll) obj;
		if (bonus_id == null) {
			if (other.bonus_id != null)
				return false;
		} else if (!bonus_id.equals(other.bonus_id))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (Double.doubleToLongBits(dmbtr) != Double
				.doubleToLongBits(other.dmbtr))
			return false;
		if (drcrk == null) {
			if (other.drcrk != null)
				return false;
		} else if (!drcrk.equals(other.drcrk))
			return false;
		if (gjahr != other.gjahr)
			return false;
		if (monat != other.monat)
			return false;
		if (payroll_date == null) {
			if (other.payroll_date != null)
				return false;
		} else if (!payroll_date.equals(other.payroll_date))
			return false;
		if (payroll_id == null) {
			if (other.payroll_id != null)
				return false;
		} else if (!payroll_id.equals(other.payroll_id))
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
		if (text45 == null) {
			if (other.text45 != null)
				return false;
		} else if (!text45.equals(other.text45))
			return false;
		return true;
	}

}
