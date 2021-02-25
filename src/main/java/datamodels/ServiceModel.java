package datamodels;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import service.ServiceTableOutput;
import general.Validation;
import general.dao.ServiceDao;
import general.tables.search.ServiceSearch;

public class ServiceModel extends LazyDataModel<ServiceTableOutput>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4266930187174283528L;
	public ServiceSearch searchModel = new ServiceSearch();
	public ServiceSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(ServiceSearch searchModel) {
		this.searchModel = searchModel;
	}

	private String cond;
	public void setCondition(String condition) {
		this.cond = condition;
	}
	
	private ServiceDao servDao;
	public ServiceModel(ServiceDao servDao) {
		// TODO Auto-generated constructor stub
		this.servDao = servDao;
	}
	
	@Override
	public List<ServiceTableOutput> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		
		String condition = searchModel.getCondition();
		if (!Validation.isEmptyString(this.cond)) {
			condition += this.cond;
		}
		
		//System.out.println("COND: " + condition);
		setRowCount(servDao.getRowCount(condition));
		
		//System.out.println("ROW_COUNT: " + this.getRowCount());
				
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}
		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY date_open DESC, id DESC ";
		} else {
			condition += " ORDER BY "
					+ sortField + " "
					+ (sortOrder.name().equals("ASCENDING") ? " ASC "
							: " DESC ");
		}
		
		// System.out.println("SORT: " + sortOrder.name() + " -> " +
		// sortOrder.name().equals(SortOrder.ASCENDING) );
		List<ServiceTableOutput> soList = servDao.findAll(condition, first, pageSize);
		
		return soList;
	}
	
}
