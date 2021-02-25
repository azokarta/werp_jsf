package general.tables.search;

import general.GeneralUtil;
import general.Validation;
import general.tables.MatnrPrice;

public class MatnrPriceSearch extends MatnrPrice{

	private String matnrName;
	public String getMatnrName() {
		return matnrName;
	}
	public void setMatnrName(String matnrName) {
		this.matnrName = matnrName;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7086308204066613344L;
	public String getCondition(){
		String c = "";
		if(!Validation.isEmptyLong(getCountry_id())){
			c = " country_id = " + getCountry_id();
		}
		
		if(!Validation.isEmptyString(getBukrs())){
			c += (c.length() > 0 ? " AND " : " ") + " bukrs = '" + getBukrs() + "' ";
		}
		
		if(!Validation.isEmptyLong(getCustomer_id())){
			c += (c.length() > 0 ? " AND " : " ") + " customer_id = " + getCustomer_id();
		}
		
		return c;
	}
}
