package logistics.invoice;

import general.tables.AccountMatnrState;
import general.tables.Matnr;

import java.util.ArrayList;
import java.util.List;

public class AccountMatnrStateOutputTable {

	private Long matnr;
	private List<AccountMatnrState> items = new ArrayList<AccountMatnrState>();
	private Matnr matnrObject;
	private String barcode;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public List<AccountMatnrState> getItems() {
		return items;
	}

	public void setItems(List<AccountMatnrState> items) {
		this.items = items;
	}

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

}
