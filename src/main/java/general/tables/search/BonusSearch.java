package general.tables.search;

import general.Validation;
import general.tables.Bonus;

public class BonusSearch extends Bonus{

	public String getCondition(){
		String condition = "";
		if(!Validation.isEmptyString(this.getBukrs())){
			condition = " bukrs = '" + this.getBukrs() + "' ";
		}
		
		if(this.getBusiness_area_id() != null && this.getBusiness_area_id() != 0){
			condition += (condition.length() > 0 ? " AND " : " ") + " business_area_id = " + this.getBusiness_area_id();
		}
		
		if(this.getCountry_id() != null && this.getCountry_id() != 0){
			condition += (condition.length() > 0 ? " AND " : " ") + " country_id = " + this.getCountry_id();
		}
		
		return condition;
	}
}
