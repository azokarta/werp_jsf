package crm.tables.search;

import java.util.List;

import crm.tables.CrmDocVisit;
import general.tables.search.ISearchTable;

public class CrmDocVisitSearch extends CrmDocVisit implements ISearchTable {

	private List<String> branchIds;
	private List<String> visitorIds;

	public List<String> getVisitorIds() {
		return visitorIds;
	}

	public void setVisitorIds(List<String> visitorIds) {
		this.visitorIds = visitorIds;
	}

	public List<String> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<String> branchIds) {
		this.branchIds = branchIds;
	}

	@Override
	public String getCondition() {
		return null;
	}

}
