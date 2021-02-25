//package general.tables;
//
//import java.lang.Long;
//import java.lang.String;
//import java.util.Date;
//
//import javax.persistence.*;
//
///**
// * Entity implementation class for Entity: AssetCatalog
// *
// */
//@Entity
//@Table(name="fixed_asset")
//@javax.persistence.SequenceGenerator(name="seq_fixed_asset_id",sequenceName="seq_fixed_asset_id", allocationSize = 1)
//
//public class FixedAsset{
//
//    public FixedAsset() {
//        this.amort_amount = 0D;
//        this.asset_catalog_id = 0L;
//        this.asset_life = 0;
//        this.branch_id = 0L;
//        this.comment = "";
//        this.counter = 0;
//        this.created_by = 0L;
//        this.created_date = null;
//        this.fa_name = "";
//        this.fa_sn = "";
//        this.init_amount = 0D;
//        this.left_amount = 0D;
//        this.operation_date = null;
//        this.staff_id = 0L;
//        this.updated_by = 0L;
//        this.updated_date = null;
//    }
//
//    
//	   
//	@Id
//	@Column(name="fa_id")
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_fixed_asset_id")
//	private Long fa_id;
//
//	@Column(name="fa_sn")
//	private String fa_sn;
//	
//	@Column(name="fa_name")
//	private String fa_name;
//
//	public String getFa_name() {
//		return fa_name;
//	}
//
//	public void setFa_name(String fa_name) {
//		this.fa_name = fa_name;
//	}
//
//	public Long getFa_id() {
//		return fa_id;
//	}
//
//	public void setFa_id(Long fa_id) {
//		this.fa_id = fa_id;
//	}
//
//	public String getFa_sn() {
//		return fa_sn;
//	}
//
//	public void setFa_sn(String fa_sn) {
//		this.fa_sn = fa_sn;
//	}
//
//	public double getInit_amount() {
//		return init_amount;
//	}
//
//	public void setInit_amount(double init_amount) {
//		this.init_amount = init_amount;
//	}
//
//	public double getLeft_amount() {
//		return left_amount;
//	}
//
//	public void setLeft_amount(double left_amount) {
//		this.left_amount = left_amount;
//	}
//
//	public double getAmort_amount() {
//		return amort_amount;
//	}
//
//	public void setAmort_amount(double amort_amount) {
//		this.amort_amount = amort_amount;
//	}
//
//	public Long getAsset_catalog_id() {
//		return asset_catalog_id;
//	}
//
//	public void setAsset_catalog_id(Long asset_catalog_id) {
//		this.asset_catalog_id = asset_catalog_id;
//	}
//
//	public int getAsset_life() {
//		return asset_life;
//	}
//
//	public void setAsset_life(int asset_life) {
//		this.asset_life = asset_life;
//	}
//
//	public Long getStaff_id() {
//		return staff_id;
//	}
//
//	public void setStaff_id(Long staff_id) {
//		this.staff_id = staff_id;
//	}
//
//	public Date getOperation_date() {
//		return operation_date;
//	}
//
//	public void setOperation_date(Date operation_date) {
//		this.operation_date = operation_date;
//	}
//
//	public Long getBranch_id() {
//		return branch_id;
//	}
//
//	public void setBranch_id(Long branch_id) {
//		this.branch_id = branch_id;
//	}
//
//	public String getComment() {
//		return comment;
//	}
//
//	public void setComment(String comment) {
//		this.comment = comment;
//	}
//
//	public Long getCreated_by() {
//		return created_by;
//	}
//
//	public void setCreated_by(Long created_by) {
//		this.created_by = created_by;
//	}
//
//	public Date getCreated_date() {
//		return created_date;
//	}
//
//	public void setCreated_date(Date created_date) {
//		this.created_date = created_date;
//	}
//
//	public Long getUpdated_by() {
//		return updated_by;
//	}
//
//	public void setUpdated_by(Long updated_by) {
//		this.updated_by = updated_by;
//	}
//
//	public Date getUpdated_date() {
//		return updated_date;
//	}
//
//	public void setUpdated_date(Date updated_date) {
//		this.updated_date = updated_date;
//	}
//
//	@Column(name="init_amount")
//	private double init_amount;
//	
//	@Column(name="left_amount")
//	private double left_amount;
//	
//	@Column(name="amort_amount")
//	private double amort_amount;
//	
//	@Column(name="asset_catalog_id")
//	private Long asset_catalog_id;
//	
//	@Column(name="asset_life")
//	private int asset_life;
//	
//	@Column(name="staff_id")
//	private Long staff_id;
//	
//	@Column(name="operation_date")
//	private Date operation_date;
//	
//	@Column(name="branch_id")
//	private Long branch_id;
//	
//	@Column(name="comment2")
//	private String comment;
//	
//	@Column(name="bukrs")
//	private String bukrs;
//	
//	@Column(name = "created_by", nullable = true)
//	private Long created_by;
//	
//	@Column(name = "created_date", nullable = true)
//	private Date created_date;
//	
//	@Column(name = "updated_by", nullable = true)
//	private Long updated_by;
//	
//	@Column(name = "updated_date", nullable = true)
//	private Date updated_date;
//	
//	@Column(name="counter")
//	private int counter;
//
//	public int getCounter() {
//		return counter;
//	}
//
//	public void setCounter(int counter) {
//		this.counter = counter;
//	}
//
//	public String getBukrs() {
//		return bukrs;
//	}
//
//	public void setBukrs(String bukrs) {
//		this.bukrs = bukrs;
//	}
//	
//}
