package general.output.tables;

public class FosaBranchResultTable {
	private String branch_name;
	private int amount_not_approved;
	public String getBranch_name() {
		return branch_name;
	}
	public void setBranch_name(String branch_name) {
		this.branch_name = branch_name;
	}
	public int getAmount_not_approved() {
		return amount_not_approved;
	}
	public void setAmount_not_approved(int amount_not_approved) {
		this.amount_not_approved = amount_not_approved;
	}
}
