package logistics;

public class LogFinClass {

	private Long matnr;
	private Double menge;
	private Double totalDmbtr;
	private Long invoiceId;

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	public Double getTotalDmbtr() {
		return totalDmbtr;
	}
	public void setTotalDmbtr(Double totalDmbtr) {
		this.totalDmbtr = totalDmbtr;
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
	
	
	public LogFinClass() {
		super();
		setMenge(0D);
		setTotalDmbtr(0D);
	}
	public LogFinClass(Long matnr, Double menge,Double totalDmbtr) {
		super();
		this.matnr = matnr;
		this.menge = menge;
		this.totalDmbtr = totalDmbtr;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogFinClass other = (LogFinClass) obj;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		return true;
	}
	
}
