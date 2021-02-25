package general.tables.search;

import general.Validation;
import general.tables.OrderOut;

public class OrderOutSearch extends OrderOut{

	/**
	 * 
	 */
	private static final long serialVersionUID = 202570880433938656L;

	
	public String getCondition(){
		String cond = "";
		if(!Validation.isEmptyString(this.getBukrs())){
			cond += "bukrs = '" + this.getBukrs() + "'";
		}
		
		if(this.getInvoice_id() != null && this.getInvoice_id() > 0L){
			cond += (cond.length() > 0 ? " AND " : " ") + " invoice_id = " + this.getInvoice_id();
		}
		
		if(this.getCustomer_id() != null && this.getCustomer_id() > 0L){
			cond += (cond.length() > 0 ? " AND " : " ") + " customer_id = " + this.getCustomer_id();
		}
		return cond;
	}
}
