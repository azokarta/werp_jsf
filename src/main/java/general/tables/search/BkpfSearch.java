package general.tables.search;

import general.Validation;
import general.tables.Bkpf;

public class BkpfSearch extends Bkpf{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8769985530786491130L;

	public String getCondition(){
		String c = "";
		if(!Validation.isEmptyString(this.getBlart())){
			c += " blart = '" + this.getBlart() + "'";
		}
		
		if(this.getBelnr() != null && this.getBelnr().longValue() > 0L){
			c += (c.length() > 0 ? " AND " : " ") + " belnr = " + this.getBelnr() + " ";
		}
		
		if(this.getContract_number() != null && this.getContract_number() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " contract_number = " + this.getContract_number() + " ";
		}
		
		if(this.getGjahr() > 0){
			c += (c.length() > 0 ? " AND " : " ") + " gjahr = " + this.getGjahr() + " ";
		}
		
		if(this.getStorno() == 0 || this.getStorno() == 1){
			c += (c.length() > 0 ? " AND " : " ") + " storno = " + this.getStorno() + " ";
		}
		return c;
	}
}
