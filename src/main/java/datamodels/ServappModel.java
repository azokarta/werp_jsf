package datamodels;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import general.Validation;
import general.dao.ServiceApplicationDao;
import general.tables.ServiceApplication;
import general.tables.search.ServappSearch;

public class ServappModel extends LazyDataModel<ServiceApplication>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6548782804695430296L;
	public ServappSearch searchModel = new ServappSearch();
	public ServappSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(ServappSearch searchModel) {
		this.searchModel = searchModel;
	}

	private String cond;
	
	public void setCondition(String condition) {
		this.cond = condition;
	}
	
	private ServiceApplicationDao servappDao;
	
	public ServappModel(ServiceApplicationDao servappDao) {
		// TODO Auto-generated constructor stub
		this.servappDao = servappDao;
	}

	@Override
	public List<ServiceApplication> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		
		String condition = searchModel.getCondition();
		if (!Validation.isEmptyString(this.cond)) {
			condition += this.cond;
		}
		
		//System.out.println("COND: " + condition);
		setRowCount(servappDao.getRowCount(condition));
		//System.out.println("ROW_COUNT: " + this.getRowCount());
				
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}
		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY id DESC ";
		} else {
			condition += " ORDER BY "
					+ sortField
					+ " "
					+ (sortOrder.name().equals("ASCENDING") ? " ASC "
							: " DESC ");
		}
		
		// System.out.println("SORT: " + sortOrder.name() + " -> " +
		// sortOrder.name().equals(SortOrder.ASCENDING) );
		List<ServiceApplication> saList = servappDao.findAll(condition, first, pageSize);
		
		return saList;
	}
	
}
