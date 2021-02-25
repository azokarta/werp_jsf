package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author onlasyn
 */
@Entity
@Table(name = "staff_official_data")
@javax.persistence.SequenceGenerator(name = "seq_staff_official_data_id", sequenceName = "seq_staff_official_data_id", allocationSize = 1)
public class StaffOfficialData implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "sod_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_staff_official_data_id")
	private Long sod_id;

	public Long getId() {
		return getSod_id();
	}
	
	public void setSod_id(Long sod_id) {
		this.sod_id = sod_id;
	}

	@Column(name = "salary")
	private double salary;

	@Column(name = "currency")
	private String currency;

	@Column(name = "sub_company_id")
	private Long sub_company_id;

	@Column(name = "salary_type")
	private String salary_type;

	@Column(name = "begin_date")
	private Date begin_date;

	@Column(name = "end_date")
	private Date end_date;

	@Column(name = "created_date")
	private Date created_date;

	@Column(name = "updated_date")
	private Date updated_date;

	@Column(name = "created_by")
	private Long created_by;

	@Column(name = "updated_by")
	private Long updated_by;

	@Column(name = "pension")
	private double pension;

	@Column(name = "ipn")
	private double ipn;

	@Column(name = "post")
	private String post;

	@Column(name = "comment2")
	private String comment;

	@Column(name = "staff_id")
	private Long staff_id;

	@Column(name = "social_contribution")
	private Double social_contribution;
	
	@Column(name="osms")
	private Double osms;
	

	
	@Column(name="osmsFromStaff")
	private Double osmsFromStaff;
	
	

	public Double getOsmsFromStaff() {
		return osmsFromStaff;
	}

	public void setOsmsFromStaff(Double osmsFromStaff) {
		this.osmsFromStaff = osmsFromStaff;
	}

	private int is_deleted;

	@Column(name = "bukrs")
	private String bukrs;

	public String getBukrs() {
		return bukrs;
	}

	public int getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(int is_deleted) {
		this.is_deleted = is_deleted;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getSub_company_id() {
		return sub_company_id;
	}

	public void setSub_company_id(Long sub_company_id) {
		this.sub_company_id = sub_company_id;
	}

	public String getSalary_type() {
		return salary_type;
	}

	public void setSalary_type(String salary_type) {
		this.salary_type = salary_type;
	}

	public Date getBegin_date() {
		return begin_date;
	}

	public void setBegin_date(Date begin_date) {
		this.begin_date = begin_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public double getPension() {
		return pension;
	}

	public void setPension(double pension) {
		this.pension = pension;
	}

	public double getIpn() {
		return ipn;
	}

	public void setIpn(double ipn) {
		this.ipn = ipn;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getSod_id() {
		return sod_id;
	}

	public Double getSocial_contribution() {
		return social_contribution;
	}

	public void setSocial_contribution(Double social_contribution) {
		this.social_contribution = social_contribution;
	}
	
	public Double getOsms() {
		return osms;
	}

	public void setOsms(Double osms) {
		this.osms = osms;
	}

	public StaffOfficialData() {
		this.begin_date = null;
		this.comment = "";
		this.created_by = 0L;
		this.created_date = null;
		this.currency = "";
		this.end_date = null;
		this.ipn = 0D;
		this.pension = 0D;
		this.post = "";
		this.salary = 0D;
		this.salary_type = "";
		this.staff_id = 0L;
		this.sub_company_id = 0L;
		this.updated_by = 0L;
		this.updated_date = null;
		this.social_contribution = 0D;
		this.is_deleted = 0;
		this.osms = 0D;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (getSod_id() != null ? getSod_id().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof StaffOfficialData)) {
			return false;
		}
		StaffOfficialData other = (StaffOfficialData) object;
		if ((this.getSod_id() == null && other.getSod_id() != null)
				|| (this.getSod_id() != null && !this.sod_id
						.equals(other.sod_id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "general.tables.StaffOfficialData[ id=" + getSod_id() + " ]";
	}

	@Transient
	private SubCompany subCompany;

	public SubCompany getSubCompany() {
		return subCompany;
	}

	public void setSubCompany(SubCompany subCompany) {
		this.subCompany = subCompany;
	}

}
