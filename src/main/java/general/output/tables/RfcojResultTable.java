package general.output.tables;

public class RfcojResultTable {
	private String bukrs;
	private int gjahr;
	private Long belnr;
	private String t_date;
	private String t_s_h;
	private double t_summa_prihod = 0L;
	private double t_summa_rashod = 0L;
	private Long t_customer;
	private String t_waers;
	private String t_blart;
	private Long t_contract_number;
	private String t_bktxt;
	private String userFio = "";
	private Long usnam;
	private Long old_sn;
	private String cusFio="";
	public String getUserFio() {
		return userFio;
	}
	public void setUserFio(String userFio) {
		this.userFio = userFio;
	}
	public String getT_date() {
		return t_date;
	}
	public void setT_date(String t_date) {
		this.t_date = t_date;
	}
	public String getT_s_h() {
		return t_s_h;
	}
	public void setT_s_h(String t_s_h) {
		this.t_s_h = t_s_h;
	}
	
	public double getT_summa_prihod() {
		return t_summa_prihod;
	}
	public void setT_summa_prihod(double t_summa_prihod) {
		this.t_summa_prihod = t_summa_prihod;
	}
	public double getT_summa_rashod() {
		return t_summa_rashod;
	}
	public void setT_summa_rashod(double t_summa_rashod) {
		this.t_summa_rashod = t_summa_rashod;
	}
	public Long getT_customer() {
		return t_customer;
	}
	public void setT_customer(Long t_customer) {
		this.t_customer = t_customer;
	}
	public String getT_waers() {
		return t_waers;
	}
	public void setT_waers(String t_waers) {
		this.t_waers = t_waers;
	}
	public String getT_blart() {
		return t_blart;
	}
	public void setT_blart(String t_blart) {
		this.t_blart = t_blart;
	}
	public Long getT_contract_number() {
		return t_contract_number;
	}
	public void setT_contract_number(Long t_contract_number) {
		this.t_contract_number = t_contract_number;
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
	public String getT_bktxt() {
		return t_bktxt;
	}
	public void setT_bktxt(String t_bktxt) {
		this.t_bktxt = t_bktxt;
	}
	public Long getUsnam() {
		return usnam;
	}
	public void setUsnam(Long usnam) {
		this.usnam = usnam;
	}
	public Long getOld_sn() {
		return old_sn;
	}
	public void setOld_sn(Long old_sn) {
		this.old_sn = old_sn;
	}
	public String getCusFio() {
		return cusFio;
	}
	public void setCusFio(String cusFio) {
		this.cusFio = cusFio;
	}
}
