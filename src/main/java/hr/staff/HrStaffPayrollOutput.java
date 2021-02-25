package hr.staff;

import java.util.Date;

public class HrStaffPayrollOutput {

	private Long staffId;
	private String bukrs;
	private String drcrk; //Дебет/Кредит
	private Double dmbtr;
	private Date payroll_date;
	private String currency;
	
	public Long getStaffId() {
		return staffId;
	}
	public void setStaffId(Long staffId) {
		this.staffId = staffId;
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

	public Double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(Double dmbtr) {
		this.dmbtr = dmbtr;
	}
	public Date getPayroll_date() {
		return payroll_date;
	}
	public void setPayroll_date(Date payroll_date) {
		this.payroll_date = payroll_date;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
}
