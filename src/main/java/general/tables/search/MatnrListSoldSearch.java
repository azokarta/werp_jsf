package general.tables.search;

import general.Validation;
import general.tables.MatnrListSold;

public class MatnrListSoldSearch extends MatnrListSold{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6972476458598488837L;

	public String getCondition(){
		String c = "";
		if(!Validation.isEmptyString(this.getBukrs())){
			c = " bukrs = '" + this.getBukrs() + "' ";
		}
		if(this.getGjahr() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " gjahr = " + this.getGjahr();
		}
		
		if(this.getWerks() != null && this.getWerks() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " werks = " + this.getWerks();
		}
		
		if(!Validation.isEmptyString(this.getBarcode())){
			c += (c.length() > 0 ? " AND " : " ") + " barcode = '" + this.getBarcode() + "'";
		}
		
		return c;
	}
}
