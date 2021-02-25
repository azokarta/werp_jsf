package general.tables;

import general.GeneralUtil;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column; 
import javax.persistence.Entity;  
import javax.persistence.Id; 
import javax.persistence.Table;

@Entity 
@Table(name = "bkpf_old") 
public class BkpfOld implements Serializable {
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
		cpudt = GeneralUtil.removeTime(cpudt);
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
