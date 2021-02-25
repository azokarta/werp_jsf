package general.tables.search;

import general.Validation;
import general.tables.EventLog;

public class EventLogSearch extends EventLog{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5169679651967948558L;

	public String getCondition(){
		String cond = "";
		if(!Validation.isEmptyString(getBukrs())){
			cond += " bukrs = '" + getBukrs() + "' ";
		}
		System.out.println("COND: " + cond);
		return cond;
	}
}
