package crm.tables.search;

import java.util.Date;
import java.util.List;

import crm.tables.CrmDocReco;
import general.Validation;
import general.tables.search.ISearchTable;

public class CrmDocRecoSearch extends CrmDocReco implements ISearchTable {

	public CrmDocRecoSearch() {
		setStatusId(null);
	}

	private List<String> branchIds;
	private List<String> responsibleIds;
	private List<String> statuses;
	private Date fromDate;
	private Date toDate;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	public List<String> getResponsibleIds() {
		return responsibleIds;
	}

	public void setResponsibleIds(List<String> responsibleIds) {
		this.responsibleIds = responsibleIds;
	}

	public List<String> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<String> branchIds) {
		this.branchIds = branchIds;
	}

	@Override
	public String getCondition() {
		String cond = "";
		if (!Validation.isEmptyString(getBukrs())) {
			cond = " bukrs = " + getBukrs();
		}

		if (Validation.isEmptyLong(getBranchId())) {
			if (!Validation.isEmptyCollection(getBranchIds())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " branch_id IN(" + String.join(",", getBranchIds())
						+ ") ";
			}
		} else {
			cond += (cond.length() > 0 ? " AND " : " ") + " branch_id = " + getBranchId();
		}

		if (!Validation.isEmptyCollection(getResponsibleIds())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " responsible_id IN(" + String.join(",", getResponsibleIds())
					+ ") ";
		}

		return cond;
	}

}