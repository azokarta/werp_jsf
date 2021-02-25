package general.tables;

import java.io.Serializable; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bsik")
public class Bsik implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	@Column(name = "invoice_id", nullable = true)
	private Long invoice_id;
	
	public Long getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
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
	      
}

