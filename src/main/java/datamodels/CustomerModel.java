package datamodels;

import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.CustomerDao;
import general.tables.Customer;
import general.tables.search.CustomerSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class CustomerModel extends LazyDataModel<Customer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8403558355576877634L;
	private CustomerDao customerDao;
	private CustomerSearch searchModel = new CustomerSearch();
	public CustomerSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(CustomerSearch searchModel) {
		this.searchModel = searchModel;
	}

	public CustomerModel(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	@Override
	public List<Customer> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		
		String condition = searchModel.getCondition();
		setRowCount(customerDao.getRowCount(condition));
		
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
		List<Customer> t = customerDao.findAll(condition, first, pageSize);
		return t;
	}

}