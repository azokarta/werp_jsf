package general.tables;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column; 
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id; 
import javax.persistence.Table;

@Entity 
@Table(name = "prebkpf") 
@javax.persistence.SequenceGenerator(name="seq_prebkpf_id",sequenceName="seq_prebkpf_id", allocationSize = 1)
public class Prebkpf implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_prebkpf_id")
	@Column(name = "prebkpf_id")
	private Long prebkpf_id;	
	
	
	@Column(name = "done_by")
	private Long done_by;
	
	@Column(name = "created_date")
	private Date created_date;
	
	@Column(name = "done_date")
	private Date done_date;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "hkont_k")
	private String hkont_k;
	
	@Column(name = "hkont_d")
	private String hkont_d;
	
	@Column(name = "summa")
	private double summa;
	
	@Column(name = "podotchetnik_id")
	private Long podotchetnik_id;
	
	
	public String getHkont_k() {
		return hkont_k;
	}

	public void setHkont_k(String hkont_k) {
		this.hkont_k = hkont_k;
	}

	public String getHkont_d() {
		return hkont_d;
	}

	public void setHkont_d(String hkont_d) {
		this.hkont_d = hkont_d;
	}

	public double getSumma() {
		return summa;
	}

	public void setSumma(double summa) {
		this.summa = summa;
	}

	public Long getPodotchetnik_id() {
		return podotchetnik_id;
	}

	public void setPodotchetnik_id(Long podotchetnik_id) {
		this.podotchetnik_id = podotchetnik_id;
	}

	public Long getPrebkpf_id() {
		return prebkpf_id;
	}

	public void setPrebkpf_id(Long prebkpf_id) {
		this.prebkpf_id = prebkpf_id;
	}

	public Long getDone_by() {
		return done_by;
	}

	public void setDone_by(Long done_by) {
		this.done_by = done_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getDone_date() {
		return done_date;
	}

	public void setDone_date(Date done_date) {
		this.done_date = done_date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

	public static final int STATUS_CREATE = 0;   // Cоздано
	public static final int STATUS_APPROVED = 1; // Одобрено
	public static final int STATUS_REFUSED = 2;  // Отказано
	
	public String getStatus(String a_lang) {
		if (a_lang!=null)
		{
			if (a_lang.equals("ru"))
			{
				if (STATUS_CREATE==this.status) return "Cоздано";
				else if (STATUS_REFUSED==this.status) return "Отказано";
				else if (STATUS_APPROVED==this.status) return "Одобрено";
				else return "Cоздано";
			}
			else if (a_lang.equals("tr"))
			{
				if (STATUS_CREATE==this.status) return "Oluşturuldu";
				else if (STATUS_REFUSED==this.status) return "Reddedildi";
				else if (STATUS_APPROVED==this.status) return "Onayli";
				else return "Cоздано";
			}
			else if (a_lang.equals("en"))
			{
				if (STATUS_CREATE==this.status) return "Cоздано";
				else if (STATUS_REFUSED==this.status) return "Denied";
				else if (STATUS_APPROVED==this.status) return "Approved";
				else return "Cоздано";
			}
		}
		return "";
	}
	
	@Column(name = "belnr")
	private Long belnr;
	
	@Column(name = "gjahr")
	private int gjahr;
	
	@Column(name = "bukrs")
	private String bukrs; 
	
	@Column(name = "blart")
	private String blart; 	

	@Column(name = "bldat")
	private Date bldat;

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

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	
	public String getBlart() {
		return blart;
	}

	public void setBlart(String blart) {
		this.blart = blart;
	}

	public Date getBldat() {
		return bldat;
	}

	public void setBldat(Date bldat) {
		this.bldat = bldat;
	}

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
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

	public Long getAwkey2() {
		return awkey2;
	}

	public void setAwkey2(Long awkey2) {
		this.awkey2 = awkey2;
	}

	public Long getDep() {
		return dep;
	}

	public void setDep(Long dep) {
		this.dep = dep;
	}

	public Long getLog_doc() {
		return log_doc;
	}

	public void setLog_doc(Long log_doc) {
		this.log_doc = log_doc;
	}

	public Long getPayroll_id() {
		return payroll_id;
	}

	public void setPayroll_id(Long payroll_id) {
		this.payroll_id = payroll_id;
	}

	public Long getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(Long invoice_id) {
		this.invoice_id = invoice_id;
	}

	public int getClosed() {
		return closed;
	}

	public void setClosed(int closed) {
		this.closed = closed;
	}

	public int getOfficial() {
		return official;
	}

	public void setOfficial(int official) {
		this.official = official;
	}
	 
	
	
}
