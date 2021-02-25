package datamodels;

import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.HrDocDao;
import general.tables.HrDoc;
import general.tables.search.HrDocSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class HrDocModel extends LazyDataModel<HrDoc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1100723415624264195L;

	HrDocDao hrDocDao;
	HrDocSearch searchModel;

	public HrDocModel(HrDocDao hrDocDao, HrDocSearch searchModel) {
		super();
		this.hrDocDao = hrDocDao;
		this.searchModel = searchModel;
	}

	@Override
	public List<HrDoc> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		String condition = searchModel.getCondition();
		setRowCount(hrDocDao.getRowCount(condition));
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}
		
		System.out.println("COND: " + condition);

		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY id DESC ";
		} else {
			condition += " ORDER BY " + sortField + " " + (sortOrder.name().equals("ASCENDING") ? " ASC " : " DESC ");
		}

		return hrDocDao.findAllLazy(condition, first, pageSize);
	}
}
