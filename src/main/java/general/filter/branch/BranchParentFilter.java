package general.filter.branch;

import general.filter.BranchFilter;
import general.tables.Branch;

public class BranchParentFilter implements BranchFilter {

	private Long parentId;
	public BranchParentFilter(Long pid) {
		this.parentId = pid;
	}

	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		return parentId.equals(b.getParent_branch_id());
	}
}
