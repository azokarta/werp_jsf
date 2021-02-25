package datamodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.InvoiceDao;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.Staff;
import general.tables.search.InvoiceSearch;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import user.User;

public class InvoiceModel extends LazyDataModel<Invoice> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2114392363641636823L;
	private List<Branch> userBranches = new ArrayList<Branch>();
	private String barcode;
	private String matnrCode;

	public String getMatnrCode() {
		return matnrCode;
	}

	public void setMatnrCode(String matnrCode) {
		this.matnrCode = matnrCode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	private InvoiceDao invoiceDao;
	private InvoiceSearch searchModel = new InvoiceSearch();

	public InvoiceSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(InvoiceSearch searchModel) {
		this.searchModel = searchModel;
	}

	User userData;

	public InvoiceModel(InvoiceDao d, User userData) {
		this.invoiceDao = d;
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
	public List<Invoice> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		String condition = getPreparedCondition();
		setRowCount(invoiceDao.getRowCount(condition));
		if (Validation.isEmptyString(condition)) {
			condition = " 1 = 1 ";
		}

		if (Validation.isEmptyString(sortField)) {
			condition += " ORDER BY id DESC ";
		} else {
			condition += " ORDER BY " + sortField + " " + (sortOrder.name().equals("ASCENDING") ? " ASC " : " DESC ");
		}

		List<Invoice> l = invoiceDao.findAllLazy(condition, first, pageSize);
		for (Invoice i : l) {
			Staff stf = stfMap.get(i.getResponsible_id());
			if (stf != null) {
				i.setResponsible(stf);
			}
		}
		return l;
	}

	private String getPreparedCondition() {
		String cond = searchModel.getCondition();
		if (!Validation.isEmptyString(getAdditionalCondition())) {
			cond += (cond.length() > 0 ? " AND " : " ") + getAdditionalCondition();
		}
		if (!Validation.isEmptyString(getBarcode())) {
			cond += cond.length() > 0 ? " AND " : " ";
			cond += " id IN(SELECT invoice_id FROM InvoiceItem WHERE barcode = '" + getBarcode() + "' ) ";
		}

		if (!Validation.isEmptyString(getMatnrCode())) {
			cond += cond.length() > 0 ? " AND " : " ";
			cond += String.format(
					" id IN(SELECT invoice_id FROM InvoiceItem WHERE matnr IN(SELECT m.matnr FROM Matnr m WHERE code = '%s' ) ) ",
					getMatnrCode());
		}

		if (!userData.isMain() && !userData.isSys_admin()) {
			if (Validation.isEmptyLong(searchModel.getBranch_id())) {
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

		// if(serviceType > 0){
		// if(serviceType == 1){
		// cond += (cond.length() > 0 ? " AND " : " ") + " contract_number IS
		// NOT NULL AND contract_number > 0 ";
		// }else if(serviceType == 2){
		// cond += (cond.length() > 0 ? " AND " : " ") + " service_number IS NOT
		// NULL AND service_number > 0 ";
		// }
		// }

		// if (!Validation.isEmptyString(searchModel.getIin_bin())) {
		// cond += " iin_bin = '" + searchModel.getIin_bin() + "' ";
		// }
		//
		// if (!Validation.isEmptyString(searchModel.getFirstname())) {
		// cond += (cond.length() > 0 ? " AND " : "") + " firstname LIKE '%"
		// + searchModel.getFirstname() + "%' ";
		// }
		//
		// if (!Validation.isEmptyString(searchModel.getLastname())) {
		// cond += (cond.length() > 0 ? " AND " : "") + " lastname LIKE '%"
		// + searchModel.getLastname() + "%' ";
		// }
		//
		// if (!Validation.isEmptyString(searchModel.getEmail())) {
		// cond += (cond.length() > 0 ? " AND " : "") + " email = '"
		// + searchModel.getEmail() + "' ";
		// }

		return cond;
	}
}
