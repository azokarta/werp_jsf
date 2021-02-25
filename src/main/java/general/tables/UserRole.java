package general.tables;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the USER_ROLE database table.
 * 
 */
@Entity
@Table(name = "USER_ROLE")
@NamedQuery(name = "UserRole.findAll", query = "SELECT u FROM UserRole u")
public class UserRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4887601872843471876L;

	@Id
	@SequenceGenerator(name = "USER_ROLE_ID_GENERATOR", sequenceName = "SEQ_USER_ROLE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ROLE_ID_GENERATOR")
	@Column(nullable = false)
	private Long id;

	@Column(name = "ROLE_ID", nullable = false, precision = 38)
	private Long roleId;

	@Column(name = "USER_ID", nullable = false, precision = 38)
	private Long userId;

	public UserRole() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}