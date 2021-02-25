package general.tables.search;

import java.util.List;

import general.Validation;
import general.tables.MatnrList;

public class MatnrListSearch extends MatnrList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> selectedWerksIds;

	public List<String> getSelectedWerksIds() {
		return selectedWerksIds;
	}

	public void setSelectedWerksIds(List<String> selectedWerksIds) {
		this.selectedWerksIds = selectedWerksIds;
	}

	public String getCondition() {
		String condition = "";
		if (!Validation.isEmptyString(this.getBukrs())) {
			condition += " bukrs = '" + this.getBukrs() + "'";
		}

		if (!Validation.isEmptyLong(getWerks())) {
			condition += (condition.length() > 0 ? " AND " : "") + " werks = " + getWerks();
		}

		if (!Validation.isEmptyLong(getMatnr())) {
			condition += (condition.length() > 0 ? " AND " : "") + " matnr = " + getMatnr();
		}

		if (!Validation.isEmptyString(getStatus())) {
			condition += (condition.length() > 0 ? " AND " : "") + " status = '" + getStatus() + "' ";
		}

		if (!Validation.isEmptyString(this.getBarcode())) {
			condition += (condition.length() > 0 ? " AND " : "") + " barcode = '" + this.getBarcode() + "' ";
		}

		if (getStaff_id() != null && getStaff_id() > 0) {
			condition += (condition.length() > 0 ? " AND " : "") + " staff_id = '" + this.getStaff_id() + "' ";
		}

		if (selectedWerksIds != null && selectedWerksIds.size() > 0) {
			if(selectedWerksIds.contains("0")){
				selectedWerksIds.remove("0");
			}
			
			if(selectedWerksIds.size() > 0){
				condition += (condition.length() > 0 ? " AND " : "") + " werks IN(" + String.join(",", selectedWerksIds.toArray(new String[selectedWerksIds.size()])) + ") ";
			}
		}

		return condition;
	}
}
