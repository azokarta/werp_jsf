package dit.user;

import f4.BranchF4;
import f4.BukrsF4;
import f4.RoleF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.RoleDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.dao.UserRoleDao;
import general.services.UserService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Role;
import general.tables.Staff;
import general.tables.User;
import general.tables.UserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@ManagedBean(name = "ditUserListBean")
@ViewScoped
public class DitUserList implements Serializable {

	private static final long serialVersionUID = -6985324821320596712L;
	private static final Long TRANSACTION_ID = 149L;

	@PostConstruct
	public void init() {
		PermissionController.canRead(userData, TRANSACTION_ID);
//		if (!userData.isSys_admin()) {
//			GeneralUtil.doRedirect("/no_permission.xhtml");
//		}

		setUserList();
		for (Bukrs b : bukrsF4Bean.getBukrs_list()) {
			bukrsList.add(b);
		}

		if (!GeneralUtil.isAjaxRequest()) {
			prepareRoleMap();
			prepareBranchList();
		}
	}

	private Map<Long, Role> roleMap = new HashMap<Long, Role>();

	private void prepareRoleMap() {
		RoleDao d = (RoleDao) appContext.getContext().getBean("roleDao");
		roleList = d.findAll(" 1 = 1 ORDER BY text45 ASC ");
		for (Role r : roleList) {
			roleMap.put(r.getRole_id(), r);
		}
	}

	private User selected;
	private List<User> userList = null;

	public User getSelected() {
		return selected;
	}

	public void setUser(User selected) {
		this.selected = selected;
		loadSelectedStaff();
	}

	public List<User> getUserList() {
		return userList;
	}

	private List<Role> selectedRoles = new ArrayList<Role>();

	public List<Role> getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(List<Role> selectedRoles) {
		// System.out.println("DDD: " + selectedRoles.size());
		this.selectedRoles = selectedRoles;
	}

	private Integer rowCount = 0;

	public Integer getRowCount() {
		return rowCount;
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}

	private void setUserList() {
		String condition = "";
		if (this.getUserSearch().getUsername().length() > 0) {
			condition += String.format(" username LIKE '%s'", "%" + this.getUserSearch().getUsername() + "%");
		}
		if (this.getUserSearch().getEmail().length() > 0) {
			condition += (condition.length() > 0 ? " AND " : "")
					+ String.format(" email LIKE '%s'", "%" + this.getUserSearch().getEmail() + "%");
		}
		UserDao uDao = (UserDao) appContext.getContext().getBean("userDao");
		this.userList = uDao.findAll(condition);
		setRowCount(userList.size());
	}

	private UserSearch userSearch = new UserSearch();

	public UserSearch getUserSearch() {
		return userSearch;
	}

	public void setUserSearch(UserSearch userSearch) {
		this.userSearch = userSearch;
	}

	public User prepareCreate() {
		selected = new User();
		selectedRoles = new ArrayList<Role>();
		return selected;
	}

	// private void loadUserRoles() {
	// if (selected != null && !Validation.isEmptyLong(selected.getUser_id())) {
	// UserRoleDao d = (UserRoleDao) appContext.getContext().getBean(
	// "userRoleDao");
	// for (UserRole ur : d.findUserRoles(selected.getUser_id())) {
	// Role r = roleMap.get(ur.getRoleId());
	// if (r != null) {
	// selectedRoles.add(r);
	// }
	// }
	// }
	// }

	private void setChilds(Long parentId, List<Branch> list, int deep, List<Branch> target) {
		for (Branch b : list) {
			if (!Validation.isEmptyLong(b.getParent_branch_id()) && b.getParent_branch_id().equals(parentId)) {
				StringBuffer sb = new StringBuffer();
				for (int k = 0; k < deep; k++) {
					sb.append(" - ");
				}
				b.setText45(sb.toString() + b.getText45());
				target.add(b);
				setChilds(b.getBranch_id(), list, deep + 1, target);
			}
		}
	}

	private Map<String, List<Branch>> branchMap = new HashMap<String, List<Branch>>();

	public List<Branch> getBranchList() {
		if (selected != null && !Validation.isEmptyString(selected.getBukrs())) {
			return branchMap.get(selected.getBukrs());
		}

		return null;
	}

	public void setBranches(String bukrs) {
		List<Branch> out = new ArrayList<Branch>();
		for (Branch b : branchList) {
			if (b.getBukrs().equals(bukrs) && Validation.isEmptyLong(b.getParent_branch_id())) {
				out.add(b);
				setChilds(b.getBranch_id(), branchList, 1, out);
			}
		}

		branchMap.put(bukrs, out);
	}

	public void setSelected(User selected) {
		this.selected = selected;
		loadSelectedStaff();
		selectedRoles = new ArrayList<Role>();

		if (selected != null && !Validation.isEmptyLong(selected.getUser_id())) {
			UserRoleDao urDao = (UserRoleDao) appContext.getContext().getBean("userRoleDao");
			for (UserRole ur : urDao.findUserRoles(selected.getUser_id())) {
				Role r = roleMap.get(ur.getRoleId());
				if (r != null) {
					selectedRoles.add(r);
				}
			}
		}
	}

	private void loadSelectedStaff() {
		selectedStaff = null;
		if (selected != null && !Validation.isEmptyLong(selected.getStaff_id())) {
			StaffDao stfDao = (StaffDao) appContext.getContext().getBean("staffDao");
			selectedStaff = stfDao.find(selected.getStaff_id());
		}
	}

	public class UserSearch {
		private String username = "";
		private String email = "";

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
	}

	List<Bukrs> bukrsList = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrsList() {
		return bukrsList;
	}

	List<Role> roleList;

	public List<Role> getRoleList() {
		return roleList;
	}

	List<Branch> branchList;

	private void prepareBranchList() {
		BranchDao d = (BranchDao) appContext.getContext().getBean("branchDao");
		branchList = d.findAll();
		for (Bukrs b : bukrsF4Bean.getBukrs_list()) {
			setBranches(b.getBukrs());
		}
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;

	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}

	public void setBukrsF4Bean(BukrsF4 b) {
		this.bukrsF4Bean = b;
	}

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

	public void Save() {
		try {
			if(!PermissionController.canCreate(userData, TRANSACTION_ID)){
				throw new DAOException("Нет доступа!");
			}
			
			if (selectedStaff == null) {
				throw new DAOException("Выберите сотрудника");
			}
			if (selected != null) {
				selected.setStaff_id(selectedStaff.getStaff_id());
				if (selected.getUser_id() != null) {
					
					//if NOT admin, can't update own data
					if(selected.getUser_id().equals(userData.getUserid()) && !userData.isSys_admin()){
						throw new DAOException("No permission!!!");
					}
					Update();
				} else {
					Create();
				}
				this.setUserList();
				GeneralUtil.addSuccessMessage("User saved successfully");
				GeneralUtil.hideDialog("UserCreateDialog");
			}
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Create() {
		UserService userSer = (UserService) appContext.getContext().getBean("userService");
		userSer.createUser(selected, selectedRoles);
		selected = new User();
	}

	public void Update() {
		UserService userServ = (UserService) appContext.getContext().getBean("userService");
		userServ.updateUser(selected, selectedRoles);
	}

	public void Reset() {
		this.selected = null;
	}

	public void Search() {
		this.setUserList();
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public void removeStaff() {
		selectedStaff = null;
	}

	public void assignSelectedStaff(Staff stf) {
		selectedStaff = stf;
	}

	@ManagedProperty(value = "#{userinfo}")
	private user.User userData;

	public void setUserData(user.User userData) {
		this.userData = userData;
	}

	Converter roleConvertor = new Converter() {

		@Override
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			// System.out.println("STRINGGGGG: " + value);
			// return ((Long)value)+"";
			return ((Role) value).getRole_id().toString();
		}

		@Override
		public Object getAsObject(FacesContext context, UIComponent component, String value) {
			Long roleId = new Long(value);
			return roleMap.get(roleId);
		}
	};

	public Converter getRoleConvertor() {
		return roleConvertor;
	}

	public void setRoleConvertor(Converter roleConvertor) {
		this.roleConvertor = roleConvertor;
	}

}
