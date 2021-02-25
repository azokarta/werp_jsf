package datamodels;

import java.util.List;
import java.util.Map;

import general.Helper;
import general.Validation;
import general.dao.MatnrDao;
import general.dao.SalaryDao;
import general.tables.Matnr;
import general.tables.Salary;
import general.tables.search.MatnrSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class MatnrModel extends LazyDataModel<Matnr> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9010659366292756071L;
	
	private MatnrDao matnrDao;

	public MatnrModel(MatnrDao d) {
		this.matnrDao = d;
	}
	
	private String bukrs;
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	private MatnrSearch searchModel = new MatnrSearch();
	public MatnrSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(MatnrSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	@Override
	public List<Matnr> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {

		String condition = searchModel.getCondition();
		condition = condition.length() > 0 ? condition : " 1 = 1 ";
		setRowCount(matnrDao.getRowCount(condition));
		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY text45 ASC ";
		} else {
			condition += " ORDER BY "
					+ sortField
					+ " "
					+ (sortOrder.name().equals("ASCENDING") ? " ASC "
							: " DESC ");
		}

		List<Matnr> t = matnrDao.findAllLazy(condition, first, pageSize);
		return t;
	}
	
	public MatnrDao getMatnrDao() {
		return matnrDao;
	}

	public void setMatnrDao(MatnrDao matnrDao) {
		this.matnrDao = matnrDao;
	}

	/********************** SEARCH DATA ***************/

	
	public class SearchClass{
		private boolean isCurrent = true;
		String bukrs;
		Long branch_id;
		private String firstname;
		private String lastname;
		private Long position_id;		

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
	}
}
