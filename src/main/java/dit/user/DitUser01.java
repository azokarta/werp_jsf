package dit.user;

import java.util.ArrayList;
import java.util.List;

import f4.BranchF4;
import general.tables.Bukrs;
import f4.BukrsF4;
import f4.RoleF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.PermissionController;
import general.dao.DAOException;
import general.services.UserService;
import general.tables.Branch;
import general.tables.User;
import general.tables.Role;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "ditUser01Bean", eager = true)
@ViewScoped
public class DitUser01 {
	private final static String transaction_code = "DITUSER01";
	private final static Long transaction_id = (long) 14;

	User newUser = new User();

	public User getNewUser() {
		return newUser;
	}

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;

	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}

	public void setBukrsF4Bean(BukrsF4 b) {
		this.bukrsF4Bean = b;
	}

	List<Bukrs> bukrsList = new ArrayList<Bukrs>();
	public List<Bukrs> getBukrsList() {
		return bukrsList;
	}


	// ******************************************************************

	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 branchF4Bean;
	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}
	public void setBranchF4Bean(BranchF4 b) {
		this.branchF4Bean = b;
	}
	
	@ManagedProperty(value = "#{roleF4Bean}")
	private RoleF4 roleF4Bean;
	public RoleF4 getRoleF4Bean() {
		return roleF4Bean;
	}
	public void setRoleF4Bean(RoleF4 r) {
		this.roleF4Bean = r;
	}

	List<Branch> branchList = new ArrayList<Branch>();

	public List<Branch> getBranchList() {
		return branchList;
	}

	List<Role> roleList = new ArrayList<Role>();

	public List<Role> getRoleList() {
		return roleList;
	}

	
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private user.User userData;

	public user.User getUserData() {
		return userData;
	}

	public void setUserData(user.User userData) {
		this.userData = userData;
	}

	// ******************************************************************

	@PostConstruct
	public void init() {
		try {
			PermissionController.canWrite(userData, transaction_id);
			for (Bukrs b : bukrsF4Bean.getBukrs_list()) {
				bukrsList.add(b);
			}

		} catch (DAOException ex) {

			MessageController.getInstance().addError(ex.getMessage());
		}
	}

	public void to_save() {
		try {
			PermissionController.canWrite(userData, transaction_id);
			UserService userSer = (UserService) appContext.getContext()
					.getBean("userService");
			//userSer.createUser(newUser);
			newUser = new User();
			GeneralUtil.addSuccessMessage("User saved successfully");
		} catch (Exception ex) {
			newUser.setUser_id(null);
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	
	public void loadBranchAndRole() {
		branchList = new ArrayList<Branch>();
		roleList = new ArrayList<Role>();

		for (Branch wa_branch : branchF4Bean.getBranch_list()) {
			if (newUser.getBukrs().equals(wa_branch.getBukrs())) {
				branchList.add(wa_branch);
			}
		}

		for (Role r : roleF4Bean.getRole_list()) {
			if (newUser.getBukrs().equals(r.getBukrs())) {
				roleList.add(r);
			}
		}
	}
}
