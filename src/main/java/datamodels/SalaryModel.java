package datamodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import general.Helper;
import general.Validation;
import general.dao.SalaryDao;
import general.tables.Branch;
import general.tables.Salary;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class SalaryModel extends LazyDataModel<Salary> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 319681212334676799L;
	/**
	 * 
	 */

	private SalaryDao salaryDao;

	public SalaryModel(SalaryDao d) {
		this.salaryDao = d;
	}

	private String bukrs;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	private SearchClass searchModel = new SearchClass();

	public SearchClass getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchClass searchModel) {
		this.searchModel = searchModel;
	}

	@Override
	public List<Salary> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		String condition = searchModel.getPreparedCondition();
		setRowCount(salaryDao.getRowCountWithStaff(condition));
		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY s1.salary_id DESC ";
		} else {
			condition += " ORDER BY " + sortField + " " + (sortOrder.name().equals("ASCENDING") ? " ASC " : " DESC ");
		}

		List<Salary> t = salaryDao.findAllWithStaff(condition, first, pageSize);
		return t;
	}

	public SalaryDao getSalaryDao() {
		return salaryDao;
	}

	public void setSalaryDao(SalaryDao salaryDao) {
		this.salaryDao = salaryDao;
	}

	/********************** SEARCH DATA ***************/

	public class SearchClass {
		private boolean isCurrent = true;
		String bukrs;
		Long branch_id;
		private String firstname;
		private String lastname;
		private Long position_id;
		private List<Branch> userBranches = new ArrayList<>();

		public List<Branch> getUserBranches() {
			return userBranches;
		}

		public void setUserBranches(List<Branch> userBranches) {
			this.userBranches = userBranches;
		}

		public boolean isCurrent() {
			return isCurrent;
		}

		public void setCurrent(boolean isCurrent) {
			this.isCurrent = isCurrent;
		}

		public String getBukrs() {
			return bukrs;
		}

		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public Long getBranch_id() {
			return branch_id;
		}

		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}

		public Long getPosition_id() {
			return position_id;
		}

		public void setPosition_id(Long position_id) {
			this.position_id = position_id;
		}

		public String getPreparedCondition() {
			String cond = " s1.staff_id IS NOT NULL AND s1.staff_id != 0 ";
			// System.out.println("TEMP: " + bukrs + " => " + getPosition_id());
			if (!Validation.isEmptyString(getBukrs())) {
				cond += " AND s1.bukrs = '" + getBukrs() + "' ";
			}

			if (Validation.isEmptyLong(getBranch_id())) {
				if (userBranches.size() > 0) {
					String[] brIds = new String[userBranches.size()];
					for (int k = 0; k < userBranches.size(); k++) {
						brIds[k] = userBranches.get(k).getBranch_id().toString();
					}

					cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id IN( " + String.join(",", brIds) + ") ";
				}
			} else {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + getBranch_id() + " ";
			}

			if (!Validation.isEmptyLong(getPosition_id())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.position_id = " + getPosition_id() + " ";
			}

			if (!Validation.isEmptyString(getFirstname())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s2.firstname LIKE '%" + getFirstname() + "%' ";
			}

			if (!Validation.isEmptyString(getLastname())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s2.lastname LIKE '%" + getLastname() + "%' ";
			}

			if (isCurrent) {
				cond += " AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "' ";
			}

			// System.out.println("CONDDD: " + cond);
			return cond.length() > 0 ? cond : " 1 = 1 ";
		}
	}
}
