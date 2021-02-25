package logistics;

final public class LogToFinance {

	Long matnr;
	Double menge;
	Double dmbtr;
	Double teDmbtr = 0D;
	
	public Double getTeDmbtr() {
		return teDmbtr;
	}
	public void setTeDmbtr(Double teDmbtr) {
		this.teDmbtr = teDmbtr;
	}
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public Double getMenge() {
		return menge;
	}
	public void setMenge(Double menge) {
		this.menge = menge;
	}
	public Double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(Double dmbtr) {
		this.dmbtr = dmbtr;
	}
}
