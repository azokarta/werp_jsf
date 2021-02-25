package dms.promotion;

import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Country;
import general.tables.Matnr;
import general.tables.Promotion;

public class PromoTable {
		
		public PromoTable() {
			this.index = 0;
			this.promo = new Promotion();
			this.branch = new Branch();
			this.region = new Branch();
			this.bukr = new Bukrs();
			this.matnr = new Matnr();
			this.country = new Country();
		}
		
		public PromoTable(int i) {
			this.index = i;
			this.promo = new Promotion();
			this.branch = new Branch();
			this.region = new Branch();
			this.bukr = new Bukrs();
			this.matnr = new Matnr();
			this.country = new Country();
		}
		
		private int index; 
		private Promotion promo;
		private Bukrs bukr;
		private Branch branch;
		private Branch region;
		private Country country;
		private Matnr matnr;
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public Promotion getPromo() {
			return promo;
		}

		public void setPromo(Promotion promo) {
			this.promo = promo;
		}

		public Bukrs getBukr() {
			return bukr;
		}

		public void setBukr(Bukrs bukr) {
			this.bukr = bukr;
		}

		public Branch getBranch() {
			return branch;
		}

		public void setBranch(Branch branch) {
			this.branch = branch;
		}

		public Country getCountry() {
			return country;
		}

		public void setCountry(Country country) {
			this.country = country;
		}

		public Matnr getMatnr() {
			return matnr;
		}

		public void setMatnr(Matnr matnr) {
			this.matnr = matnr;
		}
	
		public Branch getRegion() {
			return region;
		}

		public void setRegion(Branch region) {
			this.region = region;
		}
}
