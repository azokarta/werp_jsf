package general.tables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SERV_CRMSCHEDULE")
@javax.persistence.SequenceGenerator(name="SEQ_SERV_CRMSCHEDULE_ID",sequenceName="SEQ_SERV_CRMSCHEDULE_ID", allocationSize = 1)
public class ServCRMSchedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SERV_CRMSCHEDULE_ID")
    private Long id;
	
	@Column(name = "bukrs")
    private String bukrs;
	
	@Column(name = "branch")
    private Long branchId;

	@Column(name = "contract_id")
    private Long contractId;
	
	@Column(name = "tov_sn")
    private String tovarSN;
	
	@Column(name = "data")
    private Date scheduledDate;
	
	@Column(name = "staff_id")
    private Long staffId;

	@Column(name = "info")
    private String info;

	@Column(name = "status")
    private Long status;
	
	@Column(name = "created_date")
    private Date createdDate;
	
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public static final int STATUS_NEW = 1;
	public static final int STATUS_DONE = 2;
	public static final int STATUS_CANCELLED = 3;
	
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getTovarSN() {
		return tovarSN;
	}

	public void setTovarSN(String tovarSN) {
		this.tovarSN = tovarSN;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}
	
}
