package general.output.tables;

public class FosaResultTable {
	private String bukrs;
	private String fio;
	private Long staff_id;
	private Long branch_id;
	private String branch_name;
	private String waers;
	private double amount;
	private double amount2;
	private double amount3;
	private double amount4;
	private Long salary_id;
	private int approve;
	private String text;
	private boolean editable;
	public String getFio() {
		return fio;
	}
	public void setFio(String fio) {
		this.fio = fio;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
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
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Long getSalary_id() {
		return salary_id;
	}
	public void setSalary_id(Long salary_id) {
		this.salary_id = salary_id;
	}
	public int getApprove() {
		return approve;
	}
	public void setApprove(int approve) {
		this.approve = approve;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public double getAmount2() {
		return amount2;
	}
	public void setAmount2(double amount2) {
		this.amount2 = amount2;
	}
	public double getAmount3() {
		return amount3;
	}
	public void setAmount3(double amount3) {
		this.amount3 = amount3;
	}
	public double getAmount4() {
		return amount4;
	}
	public void setAmount4(double amount4) {
		this.amount4 = amount4;
	}
	
}
