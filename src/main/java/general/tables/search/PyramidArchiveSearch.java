package general.tables.search;

import general.Validation;
import general.tables.PyramidArchive;

public class PyramidArchiveSearch extends PyramidArchive{

	public String getCondition(){
		String c = "";
		if(!Validation.isEmptyString(this.getBukrs())){
			c += " bukrs = " + this.getBukrs();
		}
		
		if(this.getYear() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " year = " + this.getYear();
		}
		
		if(this.getMonth() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " month = " + this.getMonth();
		}
		
		return c;
	}
}
