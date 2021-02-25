package general.tables.search;

import general.Validation;
import general.tables.Matnr;

public class MatnrSearch extends Matnr{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6468791894985695644L;

	public MatnrSearch() {
		this.setCode("");
		this.setText45("");
	}
	
	public String getCondition(){
		String c = "";
//		if(!Validation.isEmptyString(this.getBukrs())){
//			c += " bukrs = '" + this.getBukrs() + "' ";
//		}
		
		if(!Validation.isEmptyString(this.getCode())){
			c += (c.length() > 0 ? " AND " : " ") + " code LIKE '" + this.getCode() + "%'"; 
		}
		
		if(!Validation.isEmptyString(this.getText45())){
			c += (c.length() > 0 ? " AND " : " ") + " text45 LIKE '%" + this.getText45() + "%'"; 
		}
		
		return c;
	}
}
