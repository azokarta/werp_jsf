package general.tables.search;

import general.Validation;
import general.tables.PriceList;

public class PriceListSearch extends PriceList{

	public String getCondition(){
		String condition = "";
		
		if(!Validation.isEmptyString(this.getBukrs())){
			condition += " bukrs = '" + this.getBukrs() + "'";
		}
		
		return condition;
	}
}
