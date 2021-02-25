package general.filter.branch;

import general.filter.BranchFilter;
import general.tables.Branch;

public class BranchBukrsFilter implements BranchFilter {

	private String bukrs;
	public BranchBukrsFilter(String bukrs) {
		this.bukrs = bukrs;
	}

	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		return bukrs.equals(b.getBukrs());
	}
}
