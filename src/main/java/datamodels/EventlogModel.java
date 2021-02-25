package datamodels;

import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.EventLogDao;
import general.tables.EventLog;
import general.tables.search.EventLogSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class EventlogModel extends LazyDataModel<EventLog> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8741229933385159233L;
	private EventLogDao elDao;
	private EventLogSearch searchModel = new EventLogSearch();
	
	public EventLogSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(EventLogSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	public EventlogModel(EventLogDao d) {
		this.elDao = d;
	}
	


	@Override
	public List<EventLog> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		String condition = searchModel.getCondition();
		if(condition.length() == 0){
			condition += " 1 = 1 ";
		}
		
		setRowCount(elDao.getRowCount(condition));
		
		
		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY ID DESC ";
		} else {
			condition += " ORDER BY "
					+ sortField
					+ " "
					+ (sortOrder.name().equals("ASCENDING") ? " ASC "
							: " DESC ");
		}

		List<EventLog> t = elDao.findAllLazy(condition, first, pageSize);
		return t;
	}

}
