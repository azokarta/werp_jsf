package datamodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DemoDao;
import general.tables.Branch;
import general.tables.Demonstration;
import general.tables.Staff;
import general.tables.search.DemoSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import user.User;

public class DemoModel extends LazyDataModel<Demonstration> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7207259026898337381L;
	private DemoDao demoDao;
	private DemoSearch searchModel = new DemoSearch();
	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	public DemoSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(DemoSearch searchModel) {
		this.searchModel = searchModel;
	}

	User userData;

	public DemoModel(DemoDao d, User userData) {
		this.demoDao = d;
		this.userData = userData;
	}

	private String additionalCondition;

	public String getAdditionalCondition() {
		return additionalCondition;
	}

	public void setAdditionalCondition(String additionalCondition) {
		this.additionalCondition = additionalCondition;
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	public Map<Long, Staff> getStfMap() {
		return stfMap;
	}

	public void setStfMap(Map<Long, Staff> stfMap) {
		this.stfMap = stfMap;
	}

	private int serviceType = 0;

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public List<Demonstration> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		String condition = getPreparedCondition();
		setRowCount(demoDao.getRowCountByCriteria(getSearchModel()));
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}

		String orderByField = "id";
		String orderByDir = "DESC";
		if (!Validation.isEmptyString(sortField)) {
			orderByField = sortField;
			orderByDir = sortOrder.name().equals("ASCENDING") ? "ASC" : "DESC";
		}

		List<Demonstration> l = demoDao.findAllByCriteriaLazy(getSearchModel(), orderByField, orderByDir, first,
				pageSize);
		return l;
	}

	private String getPreparedCondition() {
		String cond = searchModel.getCondition();
		if (!Validation.isEmptyString(getAdditionalCondition())) {
			cond += (cond.length() > 0 ? " AND " : " ") + getAdditionalCondition();
		}

		if (!userData.isMain() && !userData.isSys_admin()) {
			if (Validation.isEmptyLong(searchModel.getBranchId())) {
				if (userBranches.size() > 0) {
					String[] ids = new String[userBranches.size()];
					for (int k = 0; k < userBranches.size(); k++) {
						ids[k] = userBranches.get(k).getBranch_id().toString();
					}

					cond += (cond.length() > 0 ? " AND " : " ")
							+ String.format(" branch_id IN(%s) ", String.join(",", ids));
				} else {
					cond += (cond.length() > 0 ? " AND " : "") + " branch_id = -1 ";
				}
			}
		}

		return cond;
	}
}
