package dit.user;

import java.util.ArrayList;
import java.util.List;

import f4.BranchF4;
import f4.BukrsF4;
import f4.RoleF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.services.UserService;
import general.tables.Branch;
import general.tables.Role;
import general.tables.User;
import general.tables.Bukrs;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "ditUser03Bean", eager = true)
@ViewScoped
public class DitUser03 {
	private final static String transaction_code = "DITUSER03";
	private final static Long transaction_id = (long) 16;
	
	@PostConstruct
	public void init(){
		PermissionController.canRead(userData, transaction_id);
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
	
	private String username;
	private User user = new User();
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@ManagedProperty(value = "#{roleF4Bean}")
	private RoleF4 p_roleF4Bean;
	public RoleF4 getP_roleF4Bean() {
		return p_roleF4Bean;
	}

	public void setP_roleF4Bean(RoleF4 p_roleF4Bean) {
		this.p_roleF4Bean = p_roleF4Bean;
	}
	
	List<Role> roleList = new ArrayList<Role>();
	public List<Role> getRoleList() {
		return roleList;
	}
	
	List<Branch> branchList = new ArrayList<Branch>();
	
	public void toSearch()
	{
		try
		{
			PermissionController.canRead(userData, transaction_id);
			if(username.isEmpty())
			{
				user = new User();
				throw new DAOException("Please, enter username");
			}
			UserService userServ = (UserService) appContext.getContext()
					.getBean("userService");
			user = userServ.searchByUsername(username);
			this.loadBranchAndRole();
		}
		catch(DAOException e)
		{
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void loadBranchAndRole(){	
    	branchList = new ArrayList<Branch>();
    	roleList = new ArrayList<Role>();
    	
		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (user.getBukrs().equals(wa_branch.getBukrs())){
				branchList.add(wa_branch);
			}			
		} 
		
		for (Role wa_role : p_roleF4Bean.getRole_list()) {
			if (user.getBukrs().equals(wa_role.getBukrs())) {
				roleList.add(wa_role);
			}
		}
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	private String username_search;

	public String getUsername_search() {
		return username_search;
	}

	public void setUsername_search(String username_search) {
		this.username_search = username_search;
	}

	//***************************User session***************************
	@ManagedProperty(value="#{userinfo}")
	private user.User userData;
	public user.User getUserData() {
	  return userData;
	}

	public void setUserData(user.User userData) {
	  this.userData = userData;
	}
	// ******************************************************************

	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;

	public BukrsF4 getP_bukrsF4Bean() {
		return p_bukrsF4Bean;
	}

	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
		this.p_bukrsF4Bean = p_bukrsF4Bean;
	}

	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrs_list() {
		return bukrs_list;
	}

	// ******************************************************************
	// ***************************BranchF4*******************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;

	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}

	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	List<Branch> branch_list = new ArrayList<Branch>();

	public List<Branch> getBranch_list() {
		return branch_list;
	}

	// ******************************************************************
}
