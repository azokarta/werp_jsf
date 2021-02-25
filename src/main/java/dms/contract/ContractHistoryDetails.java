package dms.contract;

import general.tables.ContractHistory;

public class ContractHistoryDetails {

	ContractHistoryDetails() {
		this.ch = new ContractHistory();
	}

	ContractHistoryDetails(int i) {

		this.index = i;

		this.ch = new ContractHistory();

	}

	// **********************************************

	public int index;
	public ContractHistory ch;

	public String old_text;
	public String new_text;

	public String change_on;
	public String oper_type_name;

	// ***********************************************

	public String getOper_type_name() {
		return oper_type_name;
	}

	public void setOper_type_name(String oper_type_name) {
		this.oper_type_name = oper_type_name;
	}

	public String getChange_on() {
		return change_on;
	}

	public void setChange_on(String change_on) {
		this.change_on = change_on;
	}

	public String getOld_text() {
		return old_text;
	}

	public void setOld_text(String old_text) {
		this.old_text = old_text;
	}

	public String getNew_text() {
		return new_text;
	}

	public void setNew_text(String new_text) {
		this.new_text = new_text;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ContractHistory getCh() {
		return ch;
	}

	public void setCh(ContractHistory ch) {
		this.ch = ch;
	}

}
