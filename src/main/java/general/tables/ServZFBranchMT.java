package general.tables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SERV_ZF_BRANCH_MONTH_TERMS")
@javax.persistence.SequenceGenerator(name="seq_serv_zf_mt_id",sequenceName="seq_serv_zf_mt_id",allocationSize=1)
public class ServZFBranchMT {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_serv_zf_mt_id")
    private Long id;

	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "date_from")
	private Date date_from;
	
	@Column(name = "f1_mt")
	private int f1_mt;
	
	@Column(name = "f2_mt")
	private int f2_mt;
	
	@Column(name = "f3_mt")
	private int f3_mt;
	
	@Column(name = "f4_mt")
	private int f4_mt;
	
	@Column(name = "f5_mt")
	private int f5_mt;

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

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Date getDate_from() {
		return date_from;
	}

	public void setDate_from(Date date_from) {
		this.date_from = date_from;
	}

	public int getF1_mt() {
		return f1_mt;
	}

	public void setF1_mt(int f1_mt) {
		this.f1_mt = f1_mt;
	}

	public int getF2_mt() {
		return f2_mt;
	}

	public void setF2_mt(int f2_mt) {
		this.f2_mt = f2_mt;
	}

	public int getF3_mt() {
		return f3_mt;
	}

	public void setF3_mt(int f3_mt) {
		this.f3_mt = f3_mt;
	}

	public int getF4_mt() {
		return f4_mt;
	}

	public void setF4_mt(int f4_mt) {
		this.f4_mt = f4_mt;
	}

	public int getF5_mt() {
		return f5_mt;
	}

	public void setF5_mt(int f5_mt) {
		this.f5_mt = f5_mt;
	}
	
}
