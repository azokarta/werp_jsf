package general.tables.report;

import java.util.ArrayList;
import java.util.List;

import general.tables.MatnrList;

public class LogReport4 extends MatnrList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2552665549271640985L;

	public LogReport4() {
		this.setMenge(0D);
	}

	List<String> barcodes = new ArrayList<>();

	public List<String> getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(List<String> barcodes) {
		this.barcodes = barcodes;
	}

	public void addBarcode(String s) {
		this.barcodes.add(s);
	}

	public void removeBarcode(String s) {
		this.barcodes.remove(s);
	}
}
