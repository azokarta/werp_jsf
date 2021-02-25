package dit.message;

public class UserStaff {
	private int id;
	private Long user_id;
	private String username;
	private String userFio;
	private Long group_id;
	private String filteredname;
	private String fullName;
	private Long position_id;
	
	public Long getPosition_id() {
		return position_id;
	}
	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserFio() {
		return userFio;
	}
	public void setUserFio(String userFio) {
		this.userFio = userFio;
	}
	public Long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public String getFilteredname() {
		return filteredname;
	}
	public void setFilteredname(String filteredname) {
		this.filteredname = filteredname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
