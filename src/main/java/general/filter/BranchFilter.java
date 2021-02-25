package general.filter;

import general.tables.Branch;

public interface BranchFilter {
	public boolean satisfies(Branch b);
}
