package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payroll_bonus_payment")
@javax.persistence.SequenceGenerator(name="seq_payroll_bonus_payment_id",sequenceName="seq_payroll_bonus_payment_id",allocationSize=1)
public class PayrollBonusPayment {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_payroll_bonus_payment_id")
	private Long id;
	
	@Column(name = "bukrs")
	private String bukrs;	

	@Column(name = "staff_id")
	private Long staff_id;
	
	@Column(name = "parent_staff_id")
	private Long parent_staff_id;
	
	@Column(name = "branch_id", nullable = true)
	private Long branch_id;	
	
	@Column(name = "monat", nullable = true)
	private int monat;
	
	@Column(name = "gjahr", nullable = true)
	private int gjahr;
	
	@Column(name = "pyramid_id") 
	private Long pyramid_id;
	
	@Column(name = "position_id", nullable = true)
	private Long position_id;
	
	@Column(name = "bonus")
	private double bonus; 
	
	@Column(name = "salary")
	private double salary; 
	
	@Column(name = "premium")
	private double premium; 
	
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

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public Long getParent_staff_id() {
		return parent_staff_id;
	}

	public void setParent_staff_id(Long parent_staff_id) {
		this.parent_staff_id = parent_staff_id;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
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

	public Long getPyramid_id() {
		return pyramid_id;
	}

	public void setPyramid_id(Long pyramid_id) {
		this.pyramid_id = pyramid_id;
	}

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

}
