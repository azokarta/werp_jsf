package general.filter.branch;

import general.filter.BranchFilter;
import general.tables.Branch;

public class BranchParentBranchFilter implements BranchFilter {

	private Long pbId;
	public BranchParentBranchFilter(Long pbId) {
		this.pbId = pbId;
	}

	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		return b.getParent_branch_id().equals(pbId);
	}
}
