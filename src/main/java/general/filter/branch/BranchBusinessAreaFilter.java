package general.filter.branch;

import general.filter.BranchFilter;
import general.tables.Branch;
import general.tables.BusinessArea;

public class BranchBusinessAreaFilter implements BranchFilter {

	private int ba;
	public BranchBusinessAreaFilter(int ba) {
		this.ba = ba;
	}

	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		if (ba > 0)
			return ba == b.getBusiness_area_id();
		else if (ba == 0)
			return (b.getBusiness_area_id() != BusinessArea.AREA_SERVICE) && (b.getBusiness_area_id() != 0);
		else 
			return false;
	}
}
