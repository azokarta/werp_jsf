package general.filter.branch;

import java.util.ArrayList;
import java.util.List;

import general.filter.BranchFilter;
import general.tables.Branch;

public class BranchMatchAll implements BranchFilter {

	private ArrayList<BranchFilter> bfl;
	
	public BranchMatchAll() {
		bfl = new ArrayList<BranchFilter>();
	}
	
	public void addFilter(BranchFilter bf) {
		bfl.add(bf);
	}
	
	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		for (BranchFilter bf:bfl) {
			if (!bf.satisfies(b))
				return false;
		}
		return true;
	}
	
	public List<Branch> filterBranch(List<Branch> a_bl) {
		List<Branch> res_bl = new ArrayList<Branch>();
		for (Branch b:a_bl) {
			if (satisfies(b))
				res_bl.add(b);
		}
		return res_bl;
	}
}
