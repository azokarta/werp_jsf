package general.tables.search;

import general.Validation;
import general.tables.MatnrReturned;

public class MatnrReturnedSearch extends MatnrReturned{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1292854235522628420L;

	public String getCondition(){
		String c = "";
		if(!Validation.isEmptyString(this.getBukrs())){
			c += " bukrs = '" + this.getBukrs() + "' ";
		}
		return c;
	}
	
}
