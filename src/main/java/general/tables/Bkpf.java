package general.tables;

import general.GeneralUtil;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column; 
import javax.persistence.Entity;  
import javax.persistence.Id; 
import javax.persistence.Table;

@Entity 
@Table(name = "bkpf") 
public class Bkpf implements Serializable {
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
	
	@Column(name = "blart")
	private String blart; 	

	@Column(name = "budat")
	private Date budat;

	@Column(name = "bldat")
	private Date bldat;

	@Column(name = "monat")
	private int monat;

	@Column(name = "business_area_id")
	private Long business_area_id;
	
	@Column(name = "cpudt")
	private Date cpudt;


	@Column(name = "usnam")
	private Long usnam;

	@Column(name = "tcode")
	private String tcode; 	

	@Column(name = "stblg")
	private Long stblg;

	@Column(name = "stjah")
	private int stjah;

	@Column(name = "bktxt")
	private String bktxt;

	@Column(name = "waers")
	private String waers;

	@Column(name = "kursf")
	private double kursf;

	@Column(name = "brnch")
	private Long brnch;

	@Column(name = "awtyp")
	private int awtyp;

	@Column(name = "awkey")
	private Long awkey;
	
	@Column(name = "contract_number")
	private Long contract_number;	

	@Column(name = "customer_id")
	private Long customer_id;
	
	@Column(name = "storno", nullable = true)
	private int storno;
	
	@Column(name = "dmbtr")
	private double dmbtr;
 
	@Column(name = "dmbtr_paid")
	private double dmbtr_paid;
	
	@Column(name = "wrbtr")
	private double wrbtr;
 
	@Column(name = "wrbtr_paid")
	private double wrbtr_paid;
	
	@Column(name = "awkey2")
	private Long awkey2;
	
	@Column(name = "dep")
	private Long dep;

	@Column(name = "log_doc")
	private Long log_doc;
	
	@Column(name = "payroll_id")
	private Long payroll_id;
	
	@Column(name = "invoice_id")
	private Long invoice_id;
	
	@Column(name = "closed")
	private int closed;
	
	@Column(name = "official")
	private int official;

	public int getOfficial() {
		return official;
	}

	public void setOfficial(int official) {
		this.official = official;
	}
	
	public int getClosed() {
		return closed;
	}

	public void setClosed(int closed) {
		this.closed = closed;
	}

	public Long getPayroll_id() {
		return payroll_id;
	}

	public void setPayroll_id(Long payroll_id) {
		this.payroll_id = payroll_id;
	}

	public Long getLog_doc() {
		return log_doc;
	}

	public void setLog_doc(Long log_doc) {
		this.log_doc = log_doc;
	}

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public int getStorno() {
		return storno;
	}

	public void setStorno(int storno) {
		this.storno = storno;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public String getBlart() {
		return blart;
	}

	public void setBlart(String blart) {
		this.blart = blart;
	}

	public Date getBudat() {
		return budat;
	}

	public void setBudat(Date budat) {
		budat = GeneralUtil.removeTime(budat);
		this.budat = budat;
	}

	public Date getBldat() {
		return bldat;
	}

	public void setBldat(Date bldat) {
		bldat = GeneralUtil.removeTime(bldat);
		this.bldat = bldat;
	}

	public int getMonat() {
		return monat;
	}

	public void setMonat(int monat) {
		this.monat = monat;
	}

	public Date getCpudt() {
		return cpudt;
	}

	public void setCpudt(Date cpudt) {
		this.cpudt = cpudt;
	}


	public Long getUsnam() {
		return usnam;
	}

	public void setUsnam(Long usnam) {
		this.usnam = usnam;
	}

  

	public String getTcode() {
		return tcode;
	}

	public void setTcode(String tcode) {
		this.tcode = tcode;
	}
  

	public Long getStblg() {
		return stblg;
	}

	public void setStblg(Long stblg) {
		this.stblg = stblg;
	}

	public int getStjah() {
		return stjah;
	}

	public void setStjah(int stjah) {
		this.stjah = stjah;
	}

	public String getBktxt() {
		return bktxt;
	}

	public void setBktxt(String bktxt) {
		this.bktxt = bktxt;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public double getKursf() {
		return kursf;
	}

	public void setKursf(double kursf) {
		this.kursf = kursf;
	}

	public Long getBrnch() {
		return brnch;
	}

	public void setBrnch(Long brnch) {
		this.brnch = brnch;
	}

	public int getAwtyp() {
		return awtyp;
	}

	public void setAwtyp(int awtyp) {
		this.awtyp = awtyp;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}
	
	public Long getAwkey2() {
		return awkey2;
	}

	public void setAwkey2(Long awkey2) {
		this.awkey2 = awkey2;
	}

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public double getDmbtr() {
		return dmbtr;
	}

	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}

	public double getDmbtr_paid() {
		return dmbtr_paid;
	}

	public void setDmbtr_paid(double dmbtr_paid) {
		this.dmbtr_paid = dmbtr_paid;
	}

	public double getWrbtr() {
		return wrbtr;
	}

	public void setWrbtr(double wrbtr) {
		this.wrbtr = wrbtr;
	}

	public double getWrbtr_paid() {
		return wrbtr_paid;
	}

	public void setWrbtr_paid(double wrbtr_paid) {
		this.wrbtr_paid = wrbtr_paid;
	}

		
	
	public Long getDep() {
		return dep;
	}

	public void setDep(Long dep) {
		this.dep = dep;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awkey == null) ? 0 : awkey.hashCode());
		result = prime * result + ((awkey2 == null) ? 0 : awkey2.hashCode());
		result = prime * result + awtyp;
		result = prime * result + ((belnr == null) ? 0 : belnr.hashCode());
		result = prime * result + ((bktxt == null) ? 0 : bktxt.hashCode());
		result = prime * result + ((blart == null) ? 0 : blart.hashCode());
		result = prime * result + ((bldat == null) ? 0 : bldat.hashCode());
		result = prime * result + ((brnch == null) ? 0 : brnch.hashCode());
		result = prime * result + ((budat == null) ? 0 : budat.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime
				* result
				+ ((business_area_id == null) ? 0 : business_area_id.hashCode());
		result = prime * result
				+ ((contract_number == null) ? 0 : contract_number.hashCode());
		result = prime * result + ((cpudt == null) ? 0 : cpudt.hashCode());
		result = prime * result
				+ ((customer_id == null) ? 0 : customer_id.hashCode());
		result = prime * result + ((dep == null) ? 0 : dep.hashCode());
		long temp;
		temp = Double.doubleToLongBits(dmbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(dmbtr_paid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + gjahr;
		temp = Double.doubleToLongBits(kursf);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((log_doc == null) ? 0 : log_doc.hashCode());
		result = prime * result + monat;
		result = prime * result
				+ ((payroll_id == null) ? 0 : payroll_id.hashCode());
		result = prime * result + ((stblg == null) ? 0 : stblg.hashCode());
		result = prime * result + stjah;
		result = prime * result + storno;
		result = prime * result + ((tcode == null) ? 0 : tcode.hashCode());
		result = prime * result + ((usnam == null) ? 0 : usnam.hashCode());
		result = prime * result + ((waers == null) ? 0 : waers.hashCode());
		temp = Double.doubleToLongBits(wrbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(wrbtr_paid);
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
		Bkpf other = (Bkpf) obj;
		if (awkey == null) {
			if (other.awkey != null)
				return false;
		} else if (!awkey.equals(other.awkey))
			return false;
		if (awkey2 == null) {
			if (other.awkey2 != null)
				return false;
		} else if (!awkey2.equals(other.awkey2))
			return false;
		if (awtyp != other.awtyp)
			return false;
		if (belnr == null) {
			if (other.belnr != null)
				return false;
		} else if (!belnr.equals(other.belnr))
			return false;
		if (bktxt == null) {
			if (other.bktxt != null)
				return false;
		} else if (!bktxt.equals(other.bktxt))
			return false;
		if (blart == null) {
			if (other.blart != null)
				return false;
		} else if (!blart.equals(other.blart))
			return false;
		if (bldat == null) {
			if (other.bldat != null)
				return false;
		} else if (!bldat.equals(other.bldat))
			return false;
		if (brnch == null) {
			if (other.brnch != null)
				return false;
		} else if (!brnch.equals(other.brnch))
			return false;
		if (budat == null) {
			if (other.budat != null)
				return false;
		} else if (!budat.equals(other.budat))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (business_area_id == null) {
			if (other.business_area_id != null)
				return false;
		} else if (!business_area_id.equals(other.business_area_id))
			return false;
		if (contract_number == null) {
			if (other.contract_number != null)
				return false;
		} else if (!contract_number.equals(other.contract_number))
			return false;
		if (cpudt == null) {
			if (other.cpudt != null)
				return false;
		} else if (!cpudt.equals(other.cpudt))
			return false;
		if (customer_id == null) {
			if (other.customer_id != null)
				return false;
		} else if (!customer_id.equals(other.customer_id))
			return false;
		if (dep == null) {
			if (other.dep != null)
				return false;
		} else if (!dep.equals(other.dep))
			return false;
		if (Double.doubleToLongBits(dmbtr) != Double
				.doubleToLongBits(other.dmbtr))
			return false;
		if (Double.doubleToLongBits(dmbtr_paid) != Double
				.doubleToLongBits(other.dmbtr_paid))
			return false;
		if (gjahr != other.gjahr)
			return false;
		if (Double.doubleToLongBits(kursf) != Double
				.doubleToLongBits(other.kursf))
			return false;
		if (log_doc == null) {
			if (other.log_doc != null)
				return false;
		} else if (!log_doc.equals(other.log_doc))
			return false;
		if (monat != other.monat)
			return false;
		if (payroll_id == null) {
			if (other.payroll_id != null)
				return false;
		} else if (!payroll_id.equals(other.payroll_id))
			return false;
		if (stblg == null) {
			if (other.stblg != null)
				return false;
		} else if (!stblg.equals(other.stblg))
			return false;
		if (stjah != other.stjah)
			return false;
		if (storno != other.storno)
			return false;
		if (tcode == null) {
			if (other.tcode != null)
				return false;
		} else if (!tcode.equals(other.tcode))
			return false;
		if (usnam == null) {
			if (other.usnam != null)
				return false;
		} else if (!usnam.equals(other.usnam))
			return false;
		if (waers == null) {
			if (other.waers != null)
				return false;
		} else if (!waers.equals(other.waers))
			return false;
		if (Double.doubleToLongBits(wrbtr) != Double
				.doubleToLongBits(other.wrbtr))
			return false;
		if (Double.doubleToLongBits(wrbtr_paid) != Double
				.doubleToLongBits(other.wrbtr_paid))
			return false;
		return true;
	}

	public Long getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
	}

	
	public boolean getBooleanOfficial()
	{
		if (this.official==0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void setBooleanOfficial(boolean checkBox)
	{
		if (checkBox)
		{
			this.official = 1;
		}
		else
		{
			this.official = 0;
		}
	}
	

	
	 
}
