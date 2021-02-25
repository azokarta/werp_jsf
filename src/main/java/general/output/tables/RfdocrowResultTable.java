package general.output.tables;

import general.Validation;

public class RfdocrowResultTable {
	private int index;
	private String bukrs;
	private int gjahr;
	private Long belnr;
	private String budat;
	private String bldat;
	private String firstname;
	private String lastname;
	private String middlename;
	private String name;
	private int fiz_yur;
	private Long customer_id;
	private double dmbtr;
	private double wrbtr;
	private String shkzg;
	private String waers;
	private double kursf;
	private String blart="";
	private String hkont="";
	private Long contract_number;
	private String bktxt;
	private String userFirstname = "";
	private String userLastname = "";
	private String userMiddlename = "";
	private Long usnam;
	private String budatYYYY;
	
	public String getClientName()
	{
		if (this.fiz_yur==1)
		{
			return name;
		}
		else
		{
			return Validation.returnFioInitials(this.firstname, this.lastname, this.middlename);
		}
		
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public int getGjahr() {
		return gjahr;
	}
	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}
	public Long getBelnr() {
		return belnr;
	}
	public void setBelnr(Long belnr) {
		this.belnr = belnr;
	}
	public String getBudat() {
		return budat;
	}
	public void setBudat(String budat) {
		this.budat = budat;
	}
	public String getBldat() {
		return bldat;
	}
	public void setBldat(String bldat) {
		this.bldat = bldat;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public Long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
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
	public String getWaers() {
		return waers;
	}
	public void setWaers(String waers) {
		this.waers = waers;
	}
	public String getBlart() {
		return blart;
	}
	public void setBlart(String blart) {
		this.blart = blart;
	}
	public String getHkont() {
		return hkont;
	}
	public void setHkont(String hkont) {
		this.hkont = hkont;
	}
	public Long getContract_number() {
		return contract_number;
	}
	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}
	public String getUserFirstname() {
		return userFirstname;
	}
	public void setUserFirstname(String userFirstname) {
		this.userFirstname = userFirstname;
	}
	public String getUserLastname() {
		return userLastname;
	}
	public void setUserLastname(String userLastname) {
		this.userLastname = userLastname;
	}
	public String getUserMiddlename() {
		return userMiddlename;
	}
	public void setUserMiddlename(String userMiddlename) {
		this.userMiddlename = userMiddlename;
	}
	public Long getUsnam() {
		return usnam;
	}
	public void setUsnam(Long usnam) {
		this.usnam = usnam;
	}
	public String getBktxt() {
		return bktxt;
	}
	public void setBktxt(String bktxt) {
		this.bktxt = bktxt;
	}
	public double getKursf() {
		return kursf;
	}
	public void setKursf(double kursf) {
		this.kursf = kursf;
	}
	public String getShkzg() {
		return shkzg;
	}
	public void setShkzg(String shkzg) {
		this.shkzg = shkzg;
	}
	public String getBudatYYYY() {
		return budatYYYY;
	}
	public void setBudatYYYY(String budatYYYY) {
		this.budatYYYY = budatYYYY;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFiz_yur() {
		return fiz_yur;
	}
	public void setFiz_yur(int fiz_yur) {
		this.fiz_yur = fiz_yur;
	}
	
	
	
}
