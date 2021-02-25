package datamodels;

import general.Validation;
import general.dao.ContractDao;
import general.tables.search.ContractSearch;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import dms.contract.ContractDetails;

public class ContractModel extends LazyDataModel<ContractDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 283217586588981701L;
	private ContractDao contractDao;
	
	public ContractSearch searchModel = new ContractSearch();
	private String cond;
	
	public void setCondition(String condition) {
		this.cond = condition;
	}
	
	public ContractSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(ContractSearch searchModel) {
		this.searchModel = searchModel;
	}

	public ContractModel(ContractDao contractDao) {
		this.contractDao = contractDao;
	}

	@Override
	public List<ContractDetails> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		
		String condition = searchModel.getCondition();
		if (!Validation.isEmptyString(this.cond)) {
			condition += this.cond;
		}
		
		//System.out.println("COND: " + condition);
		setRowCount(contractDao.getRowCount(condition));
		
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}
		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY c.contract_date DESC ";
		} else {
			condition += " ORDER BY "
					+ sortField
					+ " "
					+ (sortOrder.name().equals("ASCENDING") ? " ASC "
							: " DESC ");
		}
		
		// System.out.println("SORT: " + sortOrder.name() + " -> " +
		// sortOrder.name().equals(SortOrder.ASCENDING) );
		List<ContractDetails> cList = contractDao.findAll(condition, first, pageSize);
		
		return cList;
	}

}
