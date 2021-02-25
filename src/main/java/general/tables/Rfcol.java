package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rfcol")
@javax.persistence.SequenceGenerator(name="seq_rfcol_id",sequenceName="seq_rfcol_id", allocationSize = 1)
public class Rfcol {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_rfcol_id")
	private Long rfcol_id;
	

	@Column(name = "bukrs", nullable = true)
	private String bukrs;
	
	@Column(name = "branch_id", nullable = true)
	private Long branch_id;
	
	@Column(name = "branch_name", nullable = true)
	private String branch_name;
	
	@Column(name = "collector_name", nullable = true)
	private String collector_name;
	
	@Column(name = "contract_amount", nullable = true)
	private int contract_amount;
	
	@Column(name = "waers", nullable = true)
	private String waers;
	
	@Column(name = "ras_plan", nullable = true)
	private double  ras_plan;
	
	@Column(name = "ras_poluchen", nullable = true)
	private double  ras_poluchen;
	
	
	@Column(name = "one_month_plan", nullable = true)
	private double  one_month_plan;
	
	@Column(name = "one_month_poluchen", nullable = true)
	private double  one_month_poluchen;
	
	
	@Column(name = "ras_usd_plan", nullable = true)
	private double  ras_usd_plan;
	
	@Column(name = "ras_usd_poluchen", nullable = true)
	private double  ras_usd_poluchen;
	
	@Column(name = "one_month_usd_plan", nullable = true)
	private double  one_month_usd_plan;
	
	@Column(name = "one_month_usd_poluchen", nullable = true)
	private double  one_month_usd_poluchen;
	
	@Column(name = "monat", nullable = true)
	private int monat;
	
	@Column(name = "gjahr", nullable = true)
	private int gjahr;
	
	@Column(name = "staff_id", nullable = true)
	private Long staff_id;
	
	@Column(name = "status", nullable = true)
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getBranch_id() {
		return branch_id;
	}
	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}
	public String getBranch_name() {
		return branch_name;
	}
	public void setBranch_name(String branch_name) {
		this.branch_name = branch_name;
	}
	public String getCollector_name() {
		return collector_name;
	}
	public void setCollector_name(String collector_name) {
		this.collector_name = collector_name;
	}
	public int getContract_amount() {
		return contract_amount;
	}
	public void setContract_amount(int contract_amount) {
		this.contract_amount = contract_amount;
	}
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public double getRas_plan() {
		return ras_plan;
	}
	public void setRas_plan(double ras_plan) {
		this.ras_plan = ras_plan;
	}
	public double getRas_poluchen() {
		return ras_poluchen;
	}
	public void setRas_poluchen(double ras_poluchen) {
		this.ras_poluchen = ras_poluchen;
	}

	public double getOne_month_plan() {
		return one_month_plan;
	}
	public void setOne_month_plan(double one_month_plan) {
		this.one_month_plan = one_month_plan;
	}
	public double getOne_month_poluchen() {
		return one_month_poluchen;
	}
	public void setOne_month_poluchen(double one_month_poluchen) {
		this.one_month_poluchen = one_month_poluchen;
	}

	public double getRas_usd_plan() {
		return ras_usd_plan;
	}
	public void setRas_usd_plan(double ras_usd_plan) {
		this.ras_usd_plan = ras_usd_plan;
	}
	public double getRas_usd_poluchen() {
		return ras_usd_poluchen;
	}
	public void setRas_usd_poluchen(double ras_usd_poluchen) {
		this.ras_usd_poluchen = ras_usd_poluchen;
	}
	public double getOne_month_usd_plan() {
		return one_month_usd_plan;
	}
	public void setOne_month_usd_plan(double one_month_usd_plan) {
		this.one_month_usd_plan = one_month_usd_plan;
	}
	public double getOne_month_usd_poluchen() {
		return one_month_usd_poluchen;
	}
	public void setOne_month_usd_poluchen(double one_month_usd_poluchen) {
		this.one_month_usd_poluchen = one_month_usd_poluchen;
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
	
	public Long getRfcol_id() {
		return rfcol_id;
	}
	public void setRfcol_id(Long rfcol_id) {
		this.rfcol_id = rfcol_id;
	}
}
