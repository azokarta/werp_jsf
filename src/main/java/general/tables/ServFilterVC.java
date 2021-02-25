package general.tables;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "serv_filter_vc")
@javax.persistence.SequenceGenerator(name="seq_serv_filter_vc",sequenceName="seq_serv_filter_vc",allocationSize=1)
public class ServFilterVC implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4510169215483191077L;

	@Override
    public ServFilterVC clone() throws CloneNotSupportedException {
        return (ServFilterVC) super.clone();
    }
	
	public static int CRMCAT_GREEN = 1;
	public static int CRMCAT_YELLOW = 2;
	public static int CRMCAT_RED = 3;
	public static int CRMCAT_BLACK = 4;
	
	public static HashMap<String, Double> STD_PRICE = new HashMap<String, Double>();
	
	static {
		STD_PRICE.clear();
		STD_PRICE.put("KZT", 9000d);
		STD_PRICE.put("KGS", 1800d);
		STD_PRICE.put("UZS", 180000d);
		STD_PRICE.put("YTL", 90d);
		STD_PRICE.put("AZN", 45d);
	}
	
	// ******************************************************************
	public static int LIST_ALL = 1;
	public static int LIST_CURRENT = 2;
	public static int LIST_OVERDUE = 3;
	
	public static int F1_MT = 12;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_serv_filter_vc")
    private Long id;
	
	@Column(name = "bukrs")
	private String bukrs;

	@Column(name = "tovar_sn")
	private String tovar_sn;
	
	@Column(name = "crm_category")
	private int crm_category;
	
	@Column(name = "fno")
	private int fno;
	
	@Column(name = "f1_mt")
	private int f1_mt;
	
	@Column(name = "f1_sid")
	private Long f1_sid;
	
	@Column(name = "f1_date")
	private Date f1_date;
	
	@Column(name = "f1_sid_prev")
	private Long f1_sid_prev;
	
	@Column(name = "f1_date_prev")
	private Date f1_date_prev;
	
	@Column(name = "f1_date_next")
	private Date f1_date_next;
	


	@Column(name = "active")
	private byte active;
	
	@Column(name = "serv_branch")
	private Long serv_branch;
	
	@Column(name = "contract_id")
	private Long contract_id;
		
	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public Long getServ_branch() {
		return serv_branch;
	}

	public void setServ_branch(Long serv_branch) {
		this.serv_branch = serv_branch;
	}

	public byte getActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getTovar_sn() {
		return tovar_sn;
	}

	public void setTovar_sn(String tovar_sn) {
		this.tovar_sn = tovar_sn;
	}

	public int getCrm_category() {
		return crm_category;
	}

	public void setCrm_category(int crm_category) {
		this.crm_category = crm_category;
	}

	public int getFno() {
		return fno;
	}

	public void setFno(int fno) {
		this.fno = fno;
	}

	public int getF1_mt() {
		return f1_mt;
	}

	public void setF1_mt(int f1_mt) {
		this.f1_mt = f1_mt;
	}

	public Long getF1_sid() {
		return f1_sid;
	}

	public void setF1_sid(Long f1_sid) {
		this.f1_sid = f1_sid;
	}

	public Date getF1_date() {
		return f1_date;
	}

	public void setF1_date(Date f1_date) {
		this.f1_date = f1_date;
	}

	public Long getF1_sid_prev() {
		return f1_sid_prev;
	}

	public void setF1_sid_prev(Long f1_sid_prev) {
		this.f1_sid_prev = f1_sid_prev;
	}

	public Date getF1_date_prev() {
		return f1_date_prev;
	}

	public void setF1_date_prev(Date f1_date_prev) {
		this.f1_date_prev = f1_date_prev;
	}

	public Date getF1_date_next() {
		return f1_date_next;
	}

	public void setF1_date_next(Date f1_date_next) {
		this.f1_date_next = f1_date_next;
	}


	public static double OVERDUE_RATE = 0.03;
	
}
