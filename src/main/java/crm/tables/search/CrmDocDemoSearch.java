package crm.tables.search;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import crm.tables.CrmDocDemo;
import general.Validation;
import general.tables.search.ISearchTable;

public class CrmDocDemoSearch extends CrmDocDemo implements ISearchTable {

	private List<String> branchIds;
	private List<String> dealerIds;
	private Date fromDate;
	private Date toDate;
	private Integer excludeResultId;

	public CrmDocDemoSearch() {
		setResultId(null);
	}

	public Integer getExcludeResultId() {
		return excludeResultId;
	}

	public void setExcludeResultId(Integer excludeResultId) {
		this.excludeResultId = excludeResultId;
	}

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

	public List<String> getDealerIds() {
		return dealerIds;
	}

	public void setDealerIds(List<String> dealerIds) {
		this.dealerIds = dealerIds;
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
			cond = " d.bukrs = " + getBukrs();
		}

		if (Validation.isEmptyLong(getBranchId())) {
			if (!Validation.isEmptyCollection(getBranchIds())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId IN(" + String.join(",", getBranchIds())
						+ ") ";
			}
		} else {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId = " + getBranchId();
		}

		if (getFromDate() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String fromDateStr = sdf.format(getFromDate()) + " 00:00:00";
			cond += (cond.length() > 0 ? " AND " : "") + " TO_CHAR( d.dateTime, 'YYYY-MM-DD HH24:MI:SS' ) >= '"
					+ fromDateStr + "' ";
		}

		if (getToDate() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String toDateStr = sdf.format(getToDate()) + " 23:59:00";
			cond += (cond.length() > 0 ? " AND " : "") + " TO_CHAR( d.dateTime, 'YYYY-MM-DD HH24:MI:SS' ) <= '"
					+ toDateStr + "' ";
		}

		if (getDateTime() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String fromDateStr = sdf.format(getDateTime()) + " 00:00:00";
			String toDateStr = sdf.format(getDateTime()) + " 23:59:00";
			cond += (cond.length() > 0 ? " AND " : "") + " TO_CHAR( d.dateTime, 'YYYY-MM-DD HH24:MI:SS' ) >= '"
					+ fromDateStr + "' AND TO_CHAR( d.dateTime, 'YYYY-MM-DD HH24:MI:SS' ) <= '" + toDateStr + "' ";
		}

		if (getResultId() != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.resultId = " + getResultId();
		}

		if (getExcludeResultId() != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.resultId != " + getExcludeResultId();
		}

		if (!Validation.isEmptyLong(getDealerId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.dealerId = " + getDealerId();
		} else if (!Validation.isEmptyCollection(getDealerIds())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.dealerId IN(" + String.join(",", getDealerIds()) + ")";
		}

		return cond;
	}

}