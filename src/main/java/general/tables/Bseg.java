package general.tables;
 
import java.io.Serializable; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bseg")
public class Bseg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Bseg(int a_buzei){
		this.buzei = a_buzei;
	}
	
	public Bseg(){
	}
	
	
	@Id
	@Column(name = "bukrs")
	private String bukrs; 

	@Id
	@Column(name = "belnr")
	private Long belnr;

	@Id
	@Column(name = "gjahr")
	private int gjahr;

	@Id
	@Column(name = "buzei")
	private int buzei;
		  
	@Column(name = "bschl", nullable = true)
	private String bschl;
		
	@Column(name = "shkzg", nullable = true)
	private String shkzg;
	
	@Column(name = "dmbtr", nullable = true)
	private double dmbtr;
	
	@Column(name = "wrbtr", nullable = true)
	private double wrbtr;
		
	@Column(name = "sgtxt", nullable = true)
	private String sgtxt;
	
	@Column(name = "hkont", nullable = true)
	private String hkont;
	
	@Column(name = "lifnr", nullable = true)
	private Long lifnr; 
	
	@Column(name = "matnr", nullable = true)
	private Long matnr;
	
	@Column(name = "werks", nullable = true)
	private Long werks;
	
	@Column(name = "menge", nullable = true)
	private double menge;
	
	@Column(name = "meins", nullable = true)
	private Long meins;
	
	@Column(name = "brnch", nullable = true)
	private Long brnch;
	
	@Column(name = "dep", nullable = true)
	private Long dep;
	
	@Column(name = "awkey", nullable = true)
	private Long awkey;
	
	@Column(name = "invoice_id", nullable = true)
	private Long invoice_id;
	
	public Long getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
	}
	
	
	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public Long getBrnch() {
		return brnch;
	}
	public void setBrnch(Long brnch) {
		this.brnch = brnch;
	}
	public Long getDep() {
		return dep;
	}
	public void setDep(Long dep) {
		this.dep = dep;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public Long getBelnr() {
		return belnr;
	}
	public void setBelnr(Long belnr) {
		this.belnr = belnr;
	}
	public int getGjahr() {
		return gjahr;
	}
	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}
	public int getBuzei() {
		return buzei;
	}
	public void setBuzei(int buzei) {
		this.buzei = buzei;
	}
	public String getBschl() {
		return bschl;
	}
	public void setBschl(String bschl) {
		this.bschl = bschl;
	}
	public String getShkzg() {
		return shkzg;
	}
	public void setShkzg(String shkzg) {
		this.shkzg = shkzg;
	}
	public double getDmbtr() {
		return dmbtr;
	}
	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}
	public double getWrbtr() {
		return wrbtr;
	}
	public void setWrbtr(double wrbtr) {
		this.wrbtr = wrbtr;
	}
	public String getSgtxt() {
		return sgtxt;
	}
	public void setSgtxt(String sgtxt) {
		this.sgtxt = sgtxt;
	}
	public String getHkont() {
		return hkont;
	}
	public void setHkont(String hkont) {
		this.hkont = hkont;
	}

	public Long getLifnr() {
		return lifnr;
	}
	public void setLifnr(Long lifnr) {
		this.lifnr = lifnr;
	}
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public Long getWerks() {
		return werks;
	}
	public void setWerks(Long werks) {
		this.werks = werks;
	}
	public double getMenge() {
		return menge;
	}
	public void setMenge(double menge) {
		this.menge = menge;
	}
	public Long getMeins() {
		return meins;
	}
	public void setMeins(Long meins) {
		this.meins = meins;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awkey == null) ? 0 : awkey.hashCode());
		result = prime * result + ((belnr == null) ? 0 : belnr.hashCode());
		result = prime * result + ((brnch == null) ? 0 : brnch.hashCode());
		result = prime * result + ((bschl == null) ? 0 : bschl.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result + buzei;
		result = prime * result + ((dep == null) ? 0 : dep.hashCode());
		long temp;
		temp = Double.doubleToLongBits(dmbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + gjahr;
		result = prime * result + ((hkont == null) ? 0 : hkont.hashCode());
		result = prime * result + ((lifnr == null) ? 0 : lifnr.hashCode());
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		result = prime * result + ((meins == null) ? 0 : meins.hashCode());
		temp = Double.doubleToLongBits(menge);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((sgtxt == null) ? 0 : sgtxt.hashCode());
		result = prime * result + ((shkzg == null) ? 0 : shkzg.hashCode());
		result = prime * result + ((werks == null) ? 0 : werks.hashCode());
		temp = Double.doubleToLongBits(wrbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Bseg other = (Bseg) obj;
		if (awkey == null) {
			if (other.awkey != null)
				return false;
		} else if (!awkey.equals(other.awkey))
			return false;
		if (belnr == null) {
			if (other.belnr != null)
				return false;
		} else if (!belnr.equals(other.belnr))
			return false;
		if (brnch == null) {
			if (other.brnch != null)
				return false;
		} else if (!brnch.equals(other.brnch))
			return false;
		if (bschl == null) {
			if (other.bschl != null)
				return false;
		} else if (!bschl.equals(other.bschl))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (buzei != other.buzei)
			return false;
		if (dep == null) {
			if (other.dep != null)
				return false;
		} else if (!dep.equals(other.dep))
			return false;
		if (Double.doubleToLongBits(dmbtr) != Double
				.doubleToLongBits(other.dmbtr))
			return false;
		if (gjahr != other.gjahr)
			return false;
		if (hkont == null) {
			if (other.hkont != null)
				return false;
		} else if (!hkont.equals(other.hkont))
			return false;
		if (lifnr == null) {
			if (other.lifnr != null)
				return false;
		} else if (!lifnr.equals(other.lifnr))
			return false;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		if (meins == null) {
			if (other.meins != null)
				return false;
		} else if (!meins.equals(other.meins))
			return false;
		if (Double.doubleToLongBits(menge) != Double
				.doubleToLongBits(other.menge))
			return false;
		if (sgtxt == null) {
			if (other.sgtxt != null)
				return false;
		} else if (!sgtxt.equals(other.sgtxt))
			return false;
		if (shkzg == null) {
			if (other.shkzg != null)
				return false;
		} else if (!shkzg.equals(other.shkzg))
			return false;
		if (werks == null) {
			if (other.werks != null)
				return false;
		} else if (!werks.equals(other.werks))
			return false;
		if (Double.doubleToLongBits(wrbtr) != Double
				.doubleToLongBits(other.wrbtr))
			return false;
		return true;
	}	
	    
}
