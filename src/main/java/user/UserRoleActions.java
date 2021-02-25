package user;

public class UserRoleActions {
	private Long user_id;
	private Long role_id;
	private String role_text;
	private Long action_id;
	private String action_text;
	private Long transaction_id;
	private String transaction_code;
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public Long getRole_id() {
		return role_id;
	}
	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}
	public String getRole_text() {
		return role_text;
	}
	public void setRole_text(String role_text) {
		this.role_text = role_text;
	}
	public Long getAction_id() {
		return action_id;
	}
	public void setAction_id(Long action_id) {
		this.action_id = action_id;
	}
	public String getAction_text() {
		return action_text;
	}
	public void setAction_text(String action_text) {
		this.action_text = action_text;
	}
	public Long getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getTransaction_code() {
		return transaction_code;
	}
	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}
	
}
