package general.filter.branch;

import general.filter.BranchFilter;
import general.tables.Branch;

public class BranchTypeFilter implements BranchFilter {

	private int branch_type;
	public BranchTypeFilter(int branch_type) {
		this.branch_type = branch_type;
	}

	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		return b.getType() == branch_type;
	}
}
