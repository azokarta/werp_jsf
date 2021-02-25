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
@Table(name = "SERV_CRMHISTORY")
@javax.persistence.SequenceGenerator(name="SEQ_SERV_CRMHISTORY_ID",sequenceName="SEQ_SERV_CRMHISTORY_ID", allocationSize = 1)
public class ServCRMHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5459354599288280032L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_SERV_CRMHISTORY_ID")
    private Long id;
	
	@Column(name = "contract_id")
    private Long contractId;
	
	@Column(name = "tov_sn")
    private String tovarSN;
	
	@Column(name = "data")
    private Date actionDate;
	
	@Column(name = "action_id")
    private Long actionId;
	
	@Column(name = "staff_id")
    private Long staffId;

	@Column(name = "info")
    private String info;

	@Column(name = "service_id")
    private Long serviceId;
	
	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
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

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
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
	
}
