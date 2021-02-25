//package general.tables;
//
//import java.io.Serializable;
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "CON_CANCEL_REQUEST")
//@javax.persistence.SequenceGenerator(name="SEQ_CON_CANCEL_REQUEST_ID",sequenceName="seq_contract_history_id", allocationSize = 1)
//public class ConCancelRequest implements Serializable {
//	
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -3722463749608225641L;
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_CON_CANCEL_REQUEST_ID")
//	private Long id;
//	
//	@Column(name = "CONTRACT_ID")
//	private Long contract_id;
//	
//	@Column(name = "REQ_DATE")
//	private Date reqDate;
//	
//	@Column(name = "REQ_USERID")
//	private Long reqUserId;
//	
//	@Column(name = "REQ_SUMM")
//	private double reqSumm;
//	
//	@Column(name = "WAERS")
//	private String waers;
//	
//	@Column(name = "STATUS")
//	private int status;
//	
//	@Column(name = "RES_USERID")
//	private Long resUserId;
//	
//	@Column(name = "RES_DATE")
//	private Date resDate;
//	
//	@Column(name = "BUKRS")
//	private String bukrs;
//
//	public String getBukrs() {
//		return bukrs;
//	}
//
//	public void setBukrs(String bukrs) {
//		this.bukrs = bukrs;
//	}
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public Long getContract_id() {
//		return contract_id;
//	}
//
//	public void setContract_id(Long contract_id) {
//		this.contract_id = contract_id;
//	}
//
//	public Date getReqDate() {
//		return reqDate;
//	}
//
//	public void setReqDate(Date reqDate) {
//		this.reqDate = reqDate;
//	}
//
//	public Long getReqUserId() {
//		return reqUserId;
//	}
//
//	public void setReqUserId(Long reqUserId) {
//		this.reqUserId = reqUserId;
//	}
//
//	public double getReqSumm() {
//		return reqSumm;
//	}
//
//	public void setReqSumm(double reqSumm) {
//		this.reqSumm = reqSumm;
//	}
//
//	public String getWaers() {
//		return waers;
//	}
//
//	public void setWaers(String waers) {
//		this.waers = waers;
//	}
//
//	public int getStatus() {
//		return status;
//	}
//
//	public void setStatus(int status) {
//		this.status = status;
//	}
//
//	public Long getResUserId() {
//		return resUserId;
//	}
//
//	public void setResUserId(Long resUserId) {
//		this.resUserId = resUserId;
//	}
//
//	public Date getResDate() {
//		return resDate;
//	}
//
//	public void setResDate(Date resDate) {
//		this.resDate = resDate;
//	}
//
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
//	
//
//}
