package general.tables.search;

import general.tables.Payroll;

public class PayrollSearch extends Payroll{

	public String getCondition(){
		String c = "";
		if(this.getStaff_id() != null && this.getStaff_id().longValue() > 0L){
			c += " staff_id = " + this.getStaff_id();
		}
		
		if(this.getMonat() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " monat = " + this.getMonat();
		}
		
		if(this.getGjahr() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " gjahr = " + this.getGjahr();
		}
		System.out.println(c);
		return c;
	}
}