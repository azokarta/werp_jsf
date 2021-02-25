package general.tables.search;

import general.tables.Bseg;

public class BsegSearch extends Bseg{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6491874857265292619L;
	
	public String getCondition(){
		String c = "";
		if(this.getBelnr() != null && this.getBelnr().longValue() > 0L){
			c += " belnr = " + this.getBelnr();
		}
		return c;
	}

}
