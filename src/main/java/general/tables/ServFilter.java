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
@Table(name = "serv_filter")
@javax.persistence.SequenceGenerator(name="seq_serv_filter_id",sequenceName="seq_serv_filter_id",allocationSize=1)
public class ServFilter implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4510169215483191077L;

	@Override
    public ServFilter clone() throws CloneNotSupportedException {
        return (ServFilter) super.clone();
    }
	
	public static int CRMCAT_GREEN = 1;
	public static int CRMCAT_YELLOW = 2;
	public static int CRMCAT_RED = 3;
	public static int CRMCAT_BLACK = 4;
	
	// ******************************************************************
	public static int LIST_ALL = 1;
	public static int LIST_CURRENT = 2;
	public static int LIST_OVERDUE = 3;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_serv_filter_id")
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
	
	@Column(name = "f2_mt")
	private int f2_mt;
	
	@Column(name = "f2_sid")
	private Long f2_sid;
	
	@Column(name = "f2_date")
	private Date f2_date;
	
	@Column(name = "f2_sid_prev")
	private Long f2_sid_prev;
	
	@Column(name = "f2_date_prev")
	private Date f2_date_prev;
	
	@Column(name = "f2_date_next")
	private Date f2_date_next;

	@Column(name = "f3_mt")
	private int f3_mt;
	
	@Column(name = "f3_sid")
	private Long f3_sid;
	
	@Column(name = "f3_date")
	private Date f3_date;
	
	@Column(name = "f3_sid_prev")
	private Long f3_sid_prev;
	
	@Column(name = "f3_date_prev")
	private Date f3_date_prev;
	
	@Column(name = "f3_date_next")
	private Date f3_date_next;
	
	@Column(name = "f4_mt")
	private int f4_mt;
	
	@Column(name = "f4_sid")
	private Long f4_sid;
	
	@Column(name = "f4_date")
	private Date f4_date;
	
	@Column(name = "f4_sid_prev")
	private Long f4_sid_prev;
	
	@Column(name = "f4_date_prev")
	private Date f4_date_prev;
	
	@Column(name = "f4_date_next")
	private Date f4_date_next;
	
	@Column(name = "f5_mt")
	private int f5_mt;
	
	@Column(name = "f5_sid")
	private Long f5_sid;
	
	@Column(name = "f5_date")
	private Date f5_date;
	
	@Column(name = "f5_sid_prev")
	private Long f5_sid_prev;
	
	@Column(name = "f5_date_prev")
	private Date f5_date_prev;
	
	@Column(name = "f5_date_next")
	private Date f5_date_next;

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

	public int getF2_mt() {
		return f2_mt;
	}

	public void setF2_mt(int f2_mt) {
		this.f2_mt = f2_mt;
	}

	public Long getF2_sid() {
		return f2_sid;
	}

	public void setF2_sid(Long f2_sid) {
		this.f2_sid = f2_sid;
	}

	public Date getF2_date() {
		return f2_date;
	}

	public void setF2_date(Date f2_date) {
		this.f2_date = f2_date;
	}

	public Long getF2_sid_prev() {
		return f2_sid_prev;
	}

	public void setF2_sid_prev(Long f2_sid_prev) {
		this.f2_sid_prev = f2_sid_prev;
	}

	public Date getF2_date_prev() {
		return f2_date_prev;
	}

	public void setF2_date_prev(Date f2_date_prev) {
		this.f2_date_prev = f2_date_prev;
	}

	public Date getF2_date_next() {
		return f2_date_next;
	}

	public void setF2_date_next(Date f2_date_next) {
		this.f2_date_next = f2_date_next;
	}

	public int getF3_mt() {
		return f3_mt;
	}

	public void setF3_mt(int f3_mt) {
		this.f3_mt = f3_mt;
	}

	public Long getF3_sid() {
		return f3_sid;
	}

	public void setF3_sid(Long f3_sid) {
		this.f3_sid = f3_sid;
	}

	public Date getF3_date() {
		return f3_date;
	}

	public void setF3_date(Date f3_date) {
		this.f3_date = f3_date;
	}

	public Long getF3_sid_prev() {
		return f3_sid_prev;
	}

	public void setF3_sid_prev(Long f3_sid_prev) {
		this.f3_sid_prev = f3_sid_prev;
	}

	public Date getF3_date_prev() {
		return f3_date_prev;
	}

	public void setF3_date_prev(Date f3_date_prev) {
		this.f3_date_prev = f3_date_prev;
	}

	public Date getF3_date_next() {
		return f3_date_next;
	}

	public void setF3_date_next(Date f3_date_next) {
		this.f3_date_next = f3_date_next;
	}

	public int getF4_mt() {
		return f4_mt;
	}

	public void setF4_mt(int f4_mt) {
		this.f4_mt = f4_mt;
	}

	public Long getF4_sid() {
		return f4_sid;
	}

	public void setF4_sid(Long f4_sid) {
		this.f4_sid = f4_sid;
	}

	public Date getF4_date() {
		return f4_date;
	}

	public void setF4_date(Date f4_date) {
		this.f4_date = f4_date;
	}

	public Long getF4_sid_prev() {
		return f4_sid_prev;
	}

	public void setF4_sid_prev(Long f4_sid_prev) {
		this.f4_sid_prev = f4_sid_prev;
	}

	public Date getF4_date_prev() {
		return f4_date_prev;
	}

	public void setF4_date_prev(Date f4_date_prev) {
		this.f4_date_prev = f4_date_prev;
	}

	public Date getF4_date_next() {
		return f4_date_next;
	}

	public void setF4_date_next(Date f4_date_next) {
		this.f4_date_next = f4_date_next;
	}

	public int getF5_mt() {
		return f5_mt;
	}

	public void setF5_mt(int f5_mt) {
		this.f5_mt = f5_mt;
	}

	public Long getF5_sid() {
		return f5_sid;
	}

	public void setF5_sid(Long f5_sid) {
		this.f5_sid = f5_sid;
	}

	public Date getF5_date() {
		return f5_date;
	}

	public void setF5_date(Date f5_date) {
		this.f5_date = f5_date;
	}

	public Long getF5_sid_prev() {
		return f5_sid_prev;
	}

	public void setF5_sid_prev(Long f5_sid_prev) {
		this.f5_sid_prev = f5_sid_prev;
	}

	public Date getF5_date_prev() {
		return f5_date_prev;
	}

	public void setF5_date_prev(Date f5_date_prev) {
		this.f5_date_prev = f5_date_prev;
	}

	public Date getF5_date_next() {
		return f5_date_next;
	}

	public void setF5_date_next(Date f5_date_next) {
		this.f5_date_next = f5_date_next;
	}

	public static double OVERDUE_RATE = 0.03;
	
}
