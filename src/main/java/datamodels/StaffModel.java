package datamodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.StaffDao;
import general.tables.Branch;
import general.tables.Staff;
import general.tables.search.StaffSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class StaffModel extends LazyDataModel<Staff> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8741229933385159233L;

	private String bukrs;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	private Long branchId;

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	private Long departmentId;

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	private StaffDao staffDao;
	private StaffSearch searchModel = new StaffSearch();

	public StaffSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(StaffSearch searchModel) {
		this.searchModel = searchModel;
	}

	public StaffModel(StaffDao d) {
		this.staffDao = d;
	}

	private boolean dismissed = false;

	public boolean isDismissed() {
		return dismissed;
	}

	public void setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
	}

	@Override
	public List<Staff> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		String condition = getPreparedCondition();
		String salCond = getSalaryCondition();
		setRowCount(dismissed ? staffDao.getRowCountDissmissed(condition)
				: staffDao.getRowCountCurrent(condition, salCond));
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}

		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY staff_id DESC ";
		} else {
			condition += " ORDER BY " + sortField + " " + (sortOrder.name().equals("ASCENDING") ? " ASC " : " DESC ");
		}

		if (dismissed) {
			return staffDao.findAllDissmissedLazy(condition, first, pageSize);
		}
		return staffDao.findAllCurrentLazy(condition, salCond, first, pageSize);
	}

	private String getSalaryCondition() {
		String cond = "";
		if (userBranches.size() > 0) {
			String[] ids = new String[userBranches.size()];
			for (int k = 0; k < userBranches.size(); k++) {
				ids[k] = userBranches.get(k).getBranch_id().toString();
			}

			cond = String.format(" sal.branch_id IN(%s) ", "'" + String.join("','", ids) + "'");
		} else if (!Validation.isEmptyLong(branchId)) {
			cond = String.format(" sal.branch_id = " + getBranchId());
		}

		if (branchIds != null && branchIds.size() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ")
					+ String.format(" branch_id IN(%s) ", String.join(",", branchIds));
		}

		if (!Validation.isEmptyLong(departmentId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " sal.department_id = " + getDepartmentId();
		}

		if (!Validation.isEmptyString(getBukrs())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " sal.bukrs = '" + getBukrs() + "' ";
		}

		return cond;
	}

	private String getPreparedCondition() {
		String cond = "";

		if (!Validation.isEmptyString(searchModel.getIin_bin())) {
			cond += " iin_bin = '" + searchModel.getIin_bin() + "' ";
		}

		if (!Validation.isEmptyString(searchModel.getFirstname())) {
			cond += (cond.length() > 0 ? " AND " : "") + " firstname LIKE '%" + searchModel.getFirstname() + "%' ";
		}

		if (!Validation.isEmptyString(searchModel.getLastname())) {
			cond += (cond.length() > 0 ? " AND " : "") + " lastname LIKE '%" + searchModel.getLastname() + "%' ";
		}

		if (!Validation.isEmptyString(searchModel.getEmail())) {
			cond += (cond.length() > 0 ? " AND " : "") + " email = '" + searchModel.getEmail() + "' ";
		}
		return cond;
	}

	private List<String> branchIds;

	public List<String> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<String> branchIds) {
		this.branchIds = branchIds;
	}

}
