package general.output.tables;

public class FaeaOutputTable {
	private Long payroll_id;
	private String bukrs;
	private Long branch_id;
	private String branchName;
	private Long staff_id;
	private String staffFio;
	private int approve;
	private String approveText;
	private String waers;
	private double dmbtr;
	private Long created_by;
	private String created_byFio;
	private Long approved_by;
	private String approved_byFio;
	private String payroll_date;
	private int status;
	private boolean noteditable;
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
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
	public String getStaffFio() {
		return staffFio;
	}
	public void setStaffFio(String staffFio) {
		this.staffFio = staffFio;
	}
	public int getApprove() {
		return approve;
	}
	public void setApprove(int approve) {
		this.approve = approve;
	}
	public String getApproveText() {
		return approveText;
	}
	public void setApproveText(String approveText) {
		this.approveText = approveText;
	}
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}
	public Long getCreated_by() {
		return created_by;
	}
	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}
	public String getCreated_byFio() {
		return created_byFio;
	}
	public void setCreated_byFio(String created_byFio) {
		this.created_byFio = created_byFio;
	}
	public Long getApproved_by() {
		return approved_by;
	}
	public void setApproved_by(Long approved_by) {
		this.approved_by = approved_by;
	}
	public String getApproved_byFio() {
		return approved_byFio;
	}
	public void setApproved_byFio(String approved_byFio) {
		this.approved_byFio = approved_byFio;
	}
	public String getPayroll_date() {
		return payroll_date;
	}
	public void setPayroll_date(String payroll_date) {
		this.payroll_date = payroll_date;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getPayroll_id() {
		return payroll_id;
	}
	public void setPayroll_id(Long payroll_id) {
		this.payroll_id = payroll_id;
	}
	public boolean isNoteditable() {
		return noteditable;
	}
	public void setNoteditable(boolean noteditable) {
		this.noteditable = noteditable;
	}
	
}
