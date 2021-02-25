package general.output.tables;

import java.util.Date;

import general.tables.ServFilter;

public class ServFilterOutput extends ServFilter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9152583050171576163L;

	public ServFilterOutput(int i) {
		// TODO Auto-generated constructor stub
		this.index = i;
	}
	
	public int index;
	public String branch;
	public Long conNumber;
	public String tovSerial;
	public Date conDate;
	public String customerFIO;
	public String dealerFIO;
	public String rayon;
	public String addrService;
	public String telDom;
	public String mob1;
	public String mob2;
	public String tel;
	public String telRab;	
	public int crmCat;
	public int f1;
	public int f2;
	public int f3;
	public int f4;
	public int f5;
	public Long oldSN;
	public Long CustomerID;
	public String crmColor;

	public String getCrmColor() {
		return crmColor;
	}
	public void setCrmColor(String crmColor) {
		this.crmColor = crmColor;
	}
	public String getTelRab() {
		return telRab;
	}
	public void setTelRab(String telRab) {
		this.telRab = telRab;
	}

	public Long getOldSN() {
		return oldSN;
	}
	public void setOldSN(Long oldSN) {
		this.oldSN = oldSN;
	}
	public Long getCustomerID() {
		return CustomerID;
	}
	public void setCustomerID(Long customerID) {
		CustomerID = customerID;
	}
	public int getCrmCat() {
		return crmCat;
	}
	public void setCrmCat(int crmCat) {
		this.crmCat = crmCat;
	}
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getTovSerial() {
		return tovSerial;
	}
	public void setTovSerial(String tovSerial) {
		this.tovSerial = tovSerial;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public Long getConNumber() {
		return conNumber;
	}
	public void setConNumber(Long conNumber) {
		this.conNumber = conNumber;
	}

	public Date getConDate() {
		return conDate;
	}
	public void setConDate(Date conDate) {
		this.conDate = conDate;
	}
	public String getCustomerFIO() {
		return customerFIO;
	}
	public void setCustomerFIO(String customerFIO) {
		this.customerFIO = customerFIO;
	}
	public String getDealerFIO() {
		return dealerFIO;
	}
	public void setDealerFIO(String dealerFIO) {
		this.dealerFIO = dealerFIO;
	}
	public String getRayon() {
		return rayon;
	}
	public void setRayon(String rayon) {
		this.rayon = rayon;
	}
	public String getAddrService() {
		return addrService;
	}
	public void setAddrService(String addrService) {
		this.addrService = addrService;
	}
	public String getTelDom() {
		return telDom;
	}
	public void setTelDom(String telDom) {
		this.telDom = telDom;
	}
	public String getMob1() {
		return mob1;
	}
	public void setMob1(String mob1) {
		this.mob1 = mob1;
	}
	public String getMob2() {
		return mob2;
	}
	public void setMob2(String mob2) {
		this.mob2 = mob2;
	}
	public int getF1() {
		return f1;
	}
	public void setF1(int f1) {
		this.f1 = f1;
	}
	public int getF2() {
		return f2;
	}
	public void setF2(int f2) {
		this.f2 = f2;
	}
	public int getF3() {
		return f3;
	}
	public void setF3(int f3) {
		this.f3 = f3;
	}
	public int getF4() {
		return f4;
	}
	public void setF4(int f4) {
		this.f4 = f4;
	}
	public int getF5() {
		return f5;
	}
	public void setF5(int f5) {
		this.f5 = f5;
	}	
}
