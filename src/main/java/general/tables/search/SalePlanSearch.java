package general.tables.search;

import general.Validation;
import general.tables.SalePlan;

public class SalePlanSearch extends SalePlan{

	public SalePlanSearch() {
		super();
	}
	public String getCondition(){
		String condition = "";
		
		if(!Validation.isEmptyString(this.getBukrs())){
			condition += " bukrs = '" + this.getBukrs() + "'";
		}
		
		if(this.getBranch_id().longValue() > 0L){
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" branch_id = %d", this.getBranch_id());
		}
		return condition;
	}
}
