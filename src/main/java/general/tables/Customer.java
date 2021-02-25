package general.tables;

import general.Validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;
@Entity
@Table(name = "customer")
@javax.persistence.SequenceGenerator(name="seq_customer_id",sequenceName="seq_customer_id", allocationSize = 1)
public class Customer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7805142809322877703L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_customer_id")
    private Long customer_id;
	private Integer fiz_yur;     
    private String iin_bin;
    private String mobile;
    
    public Customer() {
    	this.fiz_yur = 2;
    	this.address = "";
    	this.address_reg = "";
    	this.address_work = "";
    	this.birthday = null;
    	this.buh = "";
    	this.country_id = 0L;
    	this.created_by = 0L;
    	this.director = "";
    	this.email = "";
    	this.firstname = "";
    	this.iin_bin = "";
    	this.lastname = "";
    	this.middlename = "";
    	this.mobile = "";
    	this.mobile2 = "";
    	this.name = "";
    	this.passport_given_by = "";
    	this.passport_given_date = null;
    	this.passport_id = "";
    	this.staff_id = 0L;
    	this.telephone = "";
    	this.updated_by = 0L;
    	this.updated_date = null;
    	this.old_id = 0L;
	}
    public Customer(int a_fiz_yur) {
    	this.fiz_yur = a_fiz_yur;
    	this.address = "";
    	this.address_reg = "";
    	this.address_work = "";
    	this.birthday = null;
    	this.buh = "";
    	this.country_id = 0L;
    	this.created_by = 0L;
    	this.director = "";
    	this.email = "";
    	this.firstname = "";
    	this.iin_bin = "";
    	this.lastname = "";
    	this.middlename = "";
    	this.mobile = "";
    	this.mobile2 = "";
    	this.name = "";
    	this.passport_given_by = "";
    	this.passport_given_date = null;
    	this.passport_id = "";
    	this.staff_id = 0L;
    	this.telephone = "";
    	this.updated_by = 0L;
    	this.updated_date = null;
    	this.old_id = 0L;
	}
    
	@Column(name = "name", nullable = true)
    private String name;
    
	@Column(name = "firstname", nullable = true)
    private String firstname;
    
    @Column(name = "middlename", nullable = true)
    private String middlename;
    
    @Column(name = "lastname", nullable = true)
    private String lastname;
    
    @Column(name = "passport_id", nullable = true)
    private String passport_id;
    
    @Column(name = "passport_given_by", nullable = true)
    private String passport_given_by;
    
    @Column(name = "passport_given_date", nullable = true)
    private Date passport_given_date;
    
    @Column(name = "birthday", nullable = true)
    private Date birthday;
    
    @Column(name = "director", nullable = true)
    private String director;
    
    @Column(name = "buh", nullable = true)
    private String buh;
    
    @Column(name = "telephone", nullable = true)
    private String telephone;
    
    @Column(name = "full_phone", nullable = true)
    private String fullPhone;
     
    
    
    @Column(name = "mobile2", nullable = true)
    private String mobile2;
    
    @Column(name = "email", nullable = true)
    private String email;
    
    
    
    @Column(name = "address", nullable = true)
    private String address;
    
    @Column(name = "address_reg", nullable = true)
    private String address_reg;
    
    @Column(name = "address_work", nullable = true)
    private String address_work;
    
    @Column(name = "staff_id", nullable = true)
    private Long staff_id;
    
    private Long country_id;
    
    @Column(name = "created_by", nullable = true)
    private Long created_by;
    
    @Column(name = "created_date", nullable = true)
    private Date created_date;
    
    @Column(name = "updated_by", nullable = true)
    private Long updated_by;
    
    @Column(name = "updated_date", nullable = true)
    private Date updated_date;
    
    private Long old_id;
    
    
    public Long getOld_id() {
		return old_id;
	}
	public void setOld_id(Long old_id) {
		this.old_id = old_id;
	}
	public void setId(Long id) {
    	customer_id = id;
    }

    public Long getId() {
        return customer_id;
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFiz_yur() {
		return fiz_yur;
	}

	public void setFiz_yur(Integer fiz_yur) {
		this.fiz_yur = fiz_yur;
	}

	public String getIin_bin() {
		return iin_bin;
	}

	public void setIin_bin(String iin_bin) {
		this.iin_bin = iin_bin;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getBuh() {
		return buh;
	}

	public void setBuh(String buh) {
		this.buh = buh;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public Long getStaff_id() {
		return staff_id;
	}
 
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	
	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
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
 

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getPassport_id() {
		return passport_id;
	}

	public void setPassport_id(String passport_id) {
		this.passport_id = passport_id;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
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

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getAddress_reg() {
		return address_reg;
	}

	public void setAddress_reg(String address_reg) {
		this.address_reg = address_reg;
	}

	public String getAddress_work() {
		return address_work;
	}

	public void setAddress_work(String address_work) {
		this.address_work = address_work;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getFullPhone() {
		return fullPhone;
	}
	public void setFullPhone(String fullPhone) {
		this.fullPhone = fullPhone;
	}
	public String getFIO() {
		String fio = ""; 
		if (this.getFiz_yur()!= null && this.getFiz_yur()==2)
		{
			fio = Validation.returnFioInitials(this.getFirstname(), this.getLastname(), this.getMiddlename());
		}
		else if (this.getFiz_yur()!= null && this.getFiz_yur()==1)
		{
			fio = this.getName();
		}
		 
		return fio;
	}
	
	public String getFullFIO() {
		String fio = ""; 
		if (this.getFiz_yur()!= null && this.getFiz_yur()==2)
		{
			if (!Validation.isEmptyString(this.getName())) {
				fio = this.getName();
			}	
			if (!Validation.isEmptyString(this.getLastname())) {
				if (fio.length() > 0) fio += " ";
				fio += this.getLastname();
			}			
			if (!Validation.isEmptyString(this.getFirstname())) {
				fio += " " + this.getFirstname();
			}
			if (!Validation.isEmptyString(this.getMiddlename())) {
				fio += " " + this.getMiddlename();
			}
		}
		else if (this.getFiz_yur()!= null && this.getFiz_yur()==1)
		{
			fio = this.getName();
		}
		 
		return fio;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	
}
