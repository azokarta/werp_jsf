package general.tables.report;

import general.tables.Demonstration;

public class TrainingReport1 extends Demonstration{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4687997596563087621L;
	private String dealerName;
	private Double shownQuantity = 0D;
	private Double cancelledQuantity = 0D;
	private Double movedQuantity = 0D;
	private Double saleQuantity = 0D;
	private Double refPerDemoQuantity = 0D;
	private Double salePerDemoQuantity = 0D;

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public Double getShownQuantity() {
		return shownQuantity;
	}

	public void setShownQuantity(Double shownQuantity) {
		this.shownQuantity = shownQuantity;
	}

	public Double getCancelledQuantity() {
		return cancelledQuantity;
	}

	public void setCancelledQuantity(Double cancelledQuantity) {
		this.cancelledQuantity = cancelledQuantity;
	}

	public Double getMovedQuantity() {
		return movedQuantity;
	}

	public void setMovedQuantity(Double movedQuantity) {
		this.movedQuantity = movedQuantity;
	}

	public Double getSaleQuantity() {
		return saleQuantity;
	}

	public void setSaleQuantity(Double saleQuantity) {
		this.saleQuantity = saleQuantity;
	}

	public Double getRefPerDemoQuantity() {
		return refPerDemoQuantity;
	}

	public void setRefPerDemoQuantity(Double refPerDemoQuantity) {
		this.refPerDemoQuantity = refPerDemoQuantity;
	}

	public Double getSalePerDemoQuantity() {
		return salePerDemoQuantity;
	}

	public void setSalePerDemoQuantity(Double salePerDemoQuantity) {
		this.salePerDemoQuantity = salePerDemoQuantity;
	}

}
