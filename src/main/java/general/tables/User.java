package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_table")
@javax.persistence.SequenceGenerator(name = "seq_user_table_id", sequenceName = "seq_user_table_id", allocationSize = 1)
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4881667414007429557L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_table_id")
	private Long user_id;

	@Column(name = "username", nullable = true)
	private String username;

	@Column(name = "bukrs", nullable = true)
	private String bukrs;

	@Column(name = "email", nullable = true)
	private String email;

	@Column(name = "password", nullable = true)
	private String password;

	@Column(name = "create_time", nullable = true)
	private Date create_time;

	@Column(name = "branch_id", nullable = true)
	private Long branch_id;

	@Column(name = "role_id")
	private Long role_id;

	@Column(name = "salt")
	private String salt;

	@Column(name = "hash")
	private String hash;

	private Long staff_id;

	@Column(name = "is_root")
	private int is_root;

	private int is_agree;
	private Date agreement_date;

	public int getIs_agree() {
		return is_agree;
	}

	public void setIs_agree(int is_agree) {
		this.is_agree = is_agree;
	}

	public Date getAgreement_date() {
		return agreement_date;
	}

	public void setAgreement_date(Date agreement_date) {
		this.agreement_date = agreement_date;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	private int is_active;

	public int getIs_active() {
		return is_active;
	}

	public void setIs_active(int is_active) {
		this.is_active = is_active;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getRole_id() {
		return role_id;
	}

	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public User() {
		this.is_root = 0;
		this.is_agree = 0;
	}

	@OneToOne(targetEntity = Staff.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id", referencedColumnName = "staff_id", updatable = false, insertable = false)
	private Staff staff;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public int getIs_root() {
		return is_root;
	}

	public void setIs_root(int is_root) {
		this.is_root = is_root;
	}
}
