package general.tables;

import general.Validation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "staff")
@javax.persistence.SequenceGenerator(name = "seq_staff_id", sequenceName = "seq_staff_id", allocationSize = 1)
public class Staff implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2497874597930341027L;

	public Staff() {
		this.account = 0D;
		this.address = "";
		this.branch_id = 0L;
		this.customer_id = 0L;
		this.email = "";
		this.email2 = "";
		this.firstname = "";
		this.middlename = "";
		this.lastname = "";
		this.homephone = "";
		this.iin_bin = "";
		this.mobile = "";
		this.passport_given_by = "";
		this.passport_id = "";
		this.staff_id = 0L;
		this.workphone = "";
		this.department_id = 0L;
		this.birthday = null;
		this.passport_given_date = null;
		this.has_salary = 0;
		this.dismissed = 0;
		setImage_id(0L);
		this.onDismiss = 0;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_staff_id")
	private Long staff_id;
	private String iin_bin;

	@Column(name = "account")
	private double account;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "middlename", nullable = true)
	private String middlename;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "birthday")
	private Date birthday;

	@Column(name = "passport_id", nullable = true)
	private String passport_id;

	@Column(name = "passport_given_by", nullable = true)
	private String passport_given_by;

	@Column(name = "passport_given_date", nullable = true)
	private Date passport_given_date;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "homephone", nullable = true)
	private String homephone;

	@Column(name = "workphone", nullable = true)
	private String workphone;

	@Column(name = "email", nullable = true)
	private String email;

	@Column(name = "email2", nullable = true)
	private String email2;

	@Column(name = "address", nullable = true)
	private String address;

	@Column(name = "created_by", nullable = true)
	private Long created_by;

	@Column(name = "created_date", nullable = true)
	private Date created_date;

	@Column(name = "updated_by", nullable = true)
	private Long updated_by;

	@Column(name = "updated_date", nullable = true)
	private Date updated_date;

	@Column(name = "customer_id")
	private Long customer_id;

	@Column(name = "position_id")
	private Long position_id;

	@Column(name = "on_dismiss")
	private int onDismiss;
	
	@Column(name="TS_STAFF_ID")
	private Long tsStaffId;
	
	@Column(name="TS_IS_ACTIVE")
	private int tsIsActive = 0;
	
	@Column(name="TS_DATE")
	private Date tsDate;
	
	@Column(name="sub_company_id")
	private Long subCompanyId;

	public int getOnDismiss() {
		return onDismiss;
	}

	public void setOnDismiss(int onDismiss) {
		this.onDismiss = onDismiss;
	}

	public static final int POS_MASTER_FILTER = 13;
	public static final int POS_MASTER_FILTER_OVERDUE = 14;
	public static final int POS_MASTER_VAC = 16;
	public static final int POS_MASTER_WAT = 17;
	public static final int POS_OPER_FILTER = 18;
	public static final int POS_OPER_VAC = 19;

	@Column(name = "branch_id")
	private Long branch_id;

	@Column(name = "department_id")
	private Long department_id;

	private Long image_id;

	public Long getImage_id() {
		return image_id;
	}

	public void setImage_id(Long image_id) {
		this.image_id = image_id;
	}

	private String passport_id2;
	private Date passport_validity;
	private Date passport_given_date2;
	private Date passport_validity2;
	private String passport_given_by2;
	private String mobile1;
	private String mobile2;
	private String corp_email;
	private Long country_id;
	private Long state_id;
	private Long city_id;
	private Long fact_country_id;
	private Long fact_state_id;
	private Long fact_city_id;
	private String fact_address;
	private String gender;
	private int has_salary;
	
	

	public Long getTsStaffId() {
		return tsStaffId;
	}

	public void setTsStaffId(Long tsStaffId) {
		this.tsStaffId = tsStaffId;
	}

	public int getTsIsActive() {
		return tsIsActive;
	}

	public void setTsIsActive(int tsIsActive) {
		this.tsIsActive = tsIsActive;
	}

	public int getHas_salary() {
		return has_salary;
	}

	public void setHas_salary(int has_salary) {
		this.has_salary = has_salary;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

	public Long getState_id() {
		return state_id;
	}

	public void setState_id(Long state_id) {
		this.state_id = state_id;
	}

	public Long getCity_id() {
		return city_id;
	}

	public void setCity_id(Long city_id) {
		this.city_id = city_id;
	}

	public Date getPassport_validity() {
		return passport_validity;
	}

	public void setPassport_validity(Date passport_validity) {
		this.passport_validity = passport_validity;
	}

	public Date getPassport_validity2() {
		return passport_validity2;
	}

	public void setPassport_validity2(Date passport_validity2) {
		this.passport_validity2 = passport_validity2;
	}

	public Date getPassport_given_date2() {
		return passport_given_date2;
	}

	public void setPassport_given_date2(Date passport_given_date2) {
		this.passport_given_date2 = passport_given_date2;
	}

	public String getPassport_given_by2() {
		return passport_given_by2;
	}

	public void setPassport_given_by2(String passport_given_by2) {
		this.passport_given_by2 = passport_given_by2;
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getCorp_email() {
		return corp_email;
	}

	public void setCorp_email(String corp_email) {
		this.corp_email = corp_email;
	}

	public String getPassport_id2() {
		return passport_id2;
	}

	public void setPassport_id2(String passport_id2) {
		this.passport_id2 = passport_id2;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public String getIin_bin() {
		return iin_bin;
	}

	public void setIin_bin(String iin_bin) {
		this.iin_bin = iin_bin;
	}

	public double getAccount() {
		return account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHomephone() {
		return homephone;
	}

	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	public String getWorkphone() {
		return workphone;
	}

	public void setWorkphone(String workphone) {
		this.workphone = workphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
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

	public String getPassport_id() {
		return passport_id;
	}

	public void setPassport_id(String passport_id) {
		this.passport_id = passport_id;
	}

	public String getPassport_given_by() {
		return passport_given_by;
	}

	public void setPassport_given_by(String passport_given_by) {
		this.passport_given_by = passport_given_by;
	}

	public Date getPassport_given_date() {
		return passport_given_date;
	}

	public void setPassport_given_date(Date passport_given_date) {
		this.passport_given_date = passport_given_date;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getLF() {
		return this.getLastname() + " " + this.getFirstname();
	}

	/**
	 * @return the department_id
	 */
	public Long getDepartment_id() {
		return department_id;
	}

	/**
	 * @param department_id
	 *            the department_id to set
	 */
	public void setDepartment_id(Long department_id) {
		this.department_id = department_id;
	}

	public Long getFact_country_id() {
		return fact_country_id;
	}

	public void setFact_country_id(Long fact_country_id) {
		this.fact_country_id = fact_country_id;
	}

	public Long getFact_state_id() {
		return fact_state_id;
	}

	public void setFact_state_id(Long fact_state_id) {
		this.fact_state_id = fact_state_id;
	}

	public Long getFact_city_id() {
		return fact_city_id;
	}

	public void setFact_city_id(Long fact_city_id) {
		this.fact_city_id = fact_city_id;
	}

	public String getFact_address() {
		return fact_address;
	}

	public void setFact_address(String fact_address) {
		this.fact_address = fact_address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	

	public Date getTsDate() {
		return tsDate;
	}

	public void setTsDate(Date tsDate) {
		this.tsDate = tsDate;
	}

	public String getFullFIO() {
		String fio = "";
		if (!Validation.isEmptyString(this.getLastname())) {
			fio += this.getLastname();
		}
		if (!Validation.isEmptyString(this.getFirstname())) {
			fio += " " + this.getFirstname();
		}
		if (!Validation.isEmptyString(this.getMiddlename())) {
			fio += " " + this.getMiddlename();
		}

		return fio;
	}

	private int dismissed;

	public int getDismissed() {
		return dismissed;
	}

	public void setDismissed(int dismissed) {
		this.dismissed = dismissed;
	}

	@OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
	Set<StaffExpence> expences = new HashSet<StaffExpence>();

	public Set<StaffExpence> getExpences() {
		return expences;
	}

	public void setExpences(Set<StaffExpence> expences) {
		this.expences = expences;
	}

	@OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
	Set<StaffEducation> educations = new HashSet<StaffEducation>();

	public Set<StaffEducation> getEducations() {
		return educations;
	}

	public void setEducations(Set<StaffEducation> educations) {
		this.educations = educations;
	}
	
	public Long getSubCompanyId() {
		return subCompanyId;
	}

	public void setSubCompanyId(Long subCompanyId) {
		this.subCompanyId = subCompanyId;
	}

	/******************/
	@Transient
	private List<Salary> salaries;

	public List<Salary> getSalaries() {
		return salaries;
	}

	public void setSalaries(List<Salary> salaries) {
		this.salaries = salaries;
	}

	public List<Salary> getCurrentSalaries() {
		if (salaries == null) {
			return null;
		}
		List<Salary> out = new ArrayList<Salary>();
		for (Salary s : salaries) {
			if (s.isCurrentSalary()) {
				out.add(s);
			}
		}

		return out;
	}

	@Transient
	private List<UpdFile> fileList = new ArrayList<UpdFile>();

	public List<UpdFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<UpdFile> fileList) {
		this.fileList = fileList;
	}

	@Transient
	private UpdFile image;

	public UpdFile getImage() {
		return image;
	}

	public void setImage(UpdFile image) {
		this.image = image;
	}

	@Transient
	private Staff scout;

	public Staff getScout() {
		return scout;
	}

	public void setScout(Staff scout) {
		this.scout = scout;
	}
	
}
