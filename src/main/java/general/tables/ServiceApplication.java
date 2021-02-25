package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "service_application")
@javax.persistence.SequenceGenerator(name="seq_service_application_id",sequenceName="seq_service_application_id",allocationSize=1)
public class ServiceApplication{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_service_application_id")
    private Long id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "contract_number")
	private Long contract_number;

	@Column(name = "customer_id")
	private Long customer_id;

	@Column(name = "adate")
	private Date adate;
	
	@Column(name = "app_number")
	private Long app_number;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "info")
	private String info;
	
	@Column(name = "app_type")
	private Long app_type;
	//1 - CEBSERVICE, 2 - CEBFILTERS, 3 - ROBSERVICE, 4 - COMPLAINT, 5 - OTHER

	@Column(name = "created_by")
	private Long created_by;

	@Column(name = "app_status")
	private Long app_status;
	// app_status: 1 - NEW, 2 - OPERATING, 3 - PROCESSED, 4 - CANCELLED  
	
	@Column(name = "updated_by")
	private Long updated_by;

	@Column(name = "updated_date")
	private Date updated_date;

	@Column(name = "applicant_name")
	private String applicant_name;
	
	@Column(name = "in_phone_num")
	private String in_phone_num;
	
	public String getIn_phone_num() {
		return in_phone_num;
	}

	public void setIn_phone_num(String in_phone_num) {
		this.in_phone_num = in_phone_num;
	}

	public String getApplicant_name() {
		return applicant_name;
	}

	public void setApplicant_name(String applicant_name) {
		this.applicant_name = applicant_name;
	}

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
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

	public Date getAdate() {
		return adate;
	}

	public void setAdate(Date adate) {
		this.adate = adate;
	}

	public Long getApp_number() {
		return app_number;
	}

	public void setApp_number(Long app_number) {
		this.app_number = app_number;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Long getApp_status() {
		return app_status;
	}

	public void setApp_status(Long app_status) {
		this.app_status = app_status;
	}	

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
	
	public Long getApp_type() {
		return app_type;
	}

	public void setApp_type(Long app_type) {
		this.app_type = app_type;
	}

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}
}
