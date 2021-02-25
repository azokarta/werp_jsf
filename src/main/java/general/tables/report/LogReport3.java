package general.tables.report;

import general.tables.Invoice;

public class LogReport3 extends Invoice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4918871247035852712L;

	private Double accountQuantity = 0D;
	private Double returnFromAccountQuantity = 0D;
	private Double wrOffQuantity = 0D;
	private Double wrOffDocQuantity = 0D;
	private Double balance;

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getAccountQuantity() {
		return accountQuantity;
	}

	public void setAccountQuantity(Double accountQuantity) {
		this.accountQuantity = accountQuantity;
	}

	public Double getReturnFromAccountQuantity() {
		return returnFromAccountQuantity;
	}

	public void setReturnFromAccountQuantity(Double returnFromAccountQuantity) {
		this.returnFromAccountQuantity = returnFromAccountQuantity;
	}

	public Double getWrOffQuantity() {
		return wrOffQuantity;
	}

	public void setWrOffQuantity(Double wrOffQuantity) {
		this.wrOffQuantity = wrOffQuantity;
	}

	public Double getWrOffDocQuantity() {
		return wrOffDocQuantity;
	}

	public void setWrOffDocQuantity(Double wrOffDocQuantity) {
		this.wrOffDocQuantity = wrOffDocQuantity;
	}

}
