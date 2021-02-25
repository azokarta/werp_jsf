package dms.contract;

import java.util.Date;

public class PriceTableClass implements Cloneable{
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
		public Long pListId;
		public double price;
		public double priceNative;
		public Long month;
		public double firstPayment;
		public double m1;
		public double mrest;
		public Long matnrId;
		public Long premDiv;
		public String waers;
		public Date from_date;
		

		public String getWaers() {
			return waers;
		}

		public void setWaers(String waers) {
			this.waers = waers;
		}

		public Date getFrom_date() {
			return from_date;
		}

		public void setFrom_date(Date from_date) {
			this.from_date = from_date;
		}

		public Long getPremDiv() {
			return premDiv;
		}

		public void setPremDiv(Long premDiv) {
			this.premDiv = premDiv;
		}

		public Long getMatnrId() {
			return matnrId;
		}

		public void setMatnrId(Long matnrId) {
			this.matnrId = matnrId;
		}

		public double getPriceNative() {
			return priceNative;
		}

		public void setPriceNative(double priceNative) {
			this.priceNative = priceNative;
		}

		public Long getpListId() {
			return pListId;
		}

		public void setpListId(Long pListId) {
			this.pListId = pListId;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public Long getMonth() {
			return month;
		}

		public void setMonth(Long month) {
			this.month = month;
		}

		public double getFirstPayment() {
			return firstPayment;
		}

		public void setFirstPayment(double firstPayment) {
			this.firstPayment = firstPayment;
		}

		public double getM1() {
			return m1;
		}

		public void setM1(double m1) {
			this.m1 = m1;
		}

		public double getMrest() {
			return mrest;
		}

		public void setMrest(double mrest) {
			this.mrest = mrest;
		}
}
