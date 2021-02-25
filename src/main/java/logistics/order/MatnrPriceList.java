package logistics.order;

import general.tables.Matnr;

public class MatnrPriceList {
	
	public MatnrPriceList() {
		// TODO Auto-generated constructor stub
		this.matnr = new Matnr(); 
	}
	
	public MatnrPriceList(int i) {
		this.index = i;
		this.matnr = new Matnr();
		this.setPrice(0);
		this.setCurrency("");
	}
	
	public MatnrPriceList(int i, Matnr a_matnr, double a_price, String a_currency) {
		this.setIndex(i);
		this.setMatnr(a_matnr);
		this.setPrice(a_price);
		this.setCurrency(a_currency);
	}
	
	private int index;
	private Matnr matnr;
	private double price;
	private String currency;
	private double menge;
	private double qMinus;
	private double qInit;
	
	public double getqMinus() {
		return qMinus;
	}

	public void setqMinus(double qMinus) {
		this.qMinus = qMinus;
	}

	public double getqInit() {
		return qInit;
	}

	public void setqInit(double qInit) {
		this.qInit = qInit;
	}

	public double getMenge() {
		return menge;
	}

	public void setMenge(double menge) {
		this.menge = menge;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Matnr getMatnr() {
		return matnr;
	}

	public void setMatnr(Matnr matnr) {
		this.matnr = matnr;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}	
	
}
