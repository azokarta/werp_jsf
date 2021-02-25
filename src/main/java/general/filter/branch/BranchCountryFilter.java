package general.filter.branch;

import general.filter.BranchFilter;
import general.tables.Branch;

public class BranchCountryFilter implements BranchFilter {

	private Long country;
	public BranchCountryFilter(Long country) {
		this.country = country;
	}

	@Override
	public boolean satisfies(Branch b) {
		// TODO Auto-generated method stub
		return b.getCountry_id().equals(country);
	}
}
