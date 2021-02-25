package dit.role;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.MenuDao;
import general.dao.RoleDao;
import general.dao.Role_actionDao;
import general.dao.TransactionDao;
import general.services.RoleService;
import general.tables.Menu;
import general.tables.Role;
import general.tables.Role_action;
import general.tables.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "ditRoleCrudBean")
@ViewScoped
public class CrudBean {

	private static final Long TRANSACTION_ID = 129L;
	
	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, TRANSACTION_ID);
			prepareTransactions();
			prepareActiontype();
			try {
				roleId = new Long(GeneralUtil.getRequestParameter("roleId"));
			} catch (NumberFormatException e) {
				roleId = 0L;
			}

			prepareMenuMap();
		}
	}

	private void prepareMenuMap() {
		MenuDao mDao = appContext.getContext().getBean("menuDao", MenuDao.class);
		menuMap = mDao.getMenuMappedByTransactionId();

	}

	public String getMenuInfo(Long trId) {
		String[] ss = null;
		if (menuMap.containsKey(trId)) {
			ss = new String[menuMap.get(trId).size()];
			int i = 0;
			for (Menu m : menuMap.get(trId)) {
				String s = m.getTreeName();
				if(s == "null"){
					s=null;
				}
				ss[i] = (Validation.isEmptyString(s) ? "" : s + "/ ") + m.getParentName() + "/ " + m.getName();
				i++;
			}
		}

		if (ss != null && ss.length > 0) {
			return String.join(", ", ss);
		}
		return null;
	}

	private Map<Long, List<Menu>> menuMap = new HashMap<>();

	public Map<Long, List<Menu>> getMenuMap() {
		return menuMap;
	}

	public void setMenuMap(Map<Long, List<Menu>> menuMap) {
		this.menuMap = menuMap;
	}

	private Long roleId;

	List<Transaction> transactionList = new ArrayList<Transaction>();
	Map<Long, Transaction> transactionMap = new HashMap<Long, Transaction>();

	private void prepareTransactions() {
		TransactionDao td = (TransactionDao) appContext.getContext().getBean("transactionDao");
		transactionList = td.findAll("");
		for (Transaction tr : transactionList) {
			transactionMap.put(tr.getTransaction_id(), tr);
		}
	}

	private Role role;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			loadRole();
			prepareItems();
			setPageHeader();
		}
	}

	private void loadRole() {
		if (mode.equals("create")) {
			role = new Role();
		} else {
			RoleDao rd = (RoleDao) appContext.getContext().getBean("roleDao");
			role = rd.find(roleId);
		}
	}

	private List<Role_action> items = new ArrayList<Role_action>();

	public List<Role_action> getItems() {
		return items;
	}

	private void prepareItems() {
		items = new ArrayList<Role_action>();
		if (mode.equals("create")) {
			for (Transaction tr : transactionList) {
				Role_action ra = new Role_action();
				ra.setAction_id(0);
				ra.setActionName(transactionMap.get(tr.getTransaction_id()).getName_ru());
				ra.setRole_id(null);
				ra.setTransaction_id(tr.getTransaction_id());
				ra.setTransactionName(transactionMap.get(tr.getTransaction_id()).getName_ru());
				items.add(ra);
			}
		} else {

			Role_actionDao rad = (Role_actionDao) appContext.getContext().getBean("role_actionDao");
			List<Role_action> temp = rad.getByRoleId(role.getRole_id());
			Map<Long, Role_action> roleTrMap = new HashMap<Long, Role_action>();
			for (Role_action ra : temp) {
				roleTrMap.put(ra.getTransaction_id(), ra);
			}

			for (Transaction tr : transactionList) {
				Role_action ra = roleTrMap.get(tr.getTransaction_id());
				if (ra == null) {
					if (mode.equals("view")) {
						continue;
					}

					Role_action raNew = new Role_action();
					raNew.setAction_id(0);
					raNew.setRole_id(role.getRole_id());
					raNew.setTransaction_id(tr.getTransaction_id());
					raNew.setTransactionName(tr.getName_ru());
					items.add(raNew);
				} else {
					ra.setTransactionName(tr.getName_ru());
					items.add(ra);
				}
			}
		}
	}

	List<ActionType> actionTypeList = new ArrayList<ActionType>();

	private void prepareActiontype() {
		actionTypeList = new ArrayList<ActionType>();
		for (int i = 1; i < 4; i++) {
			ActionType at = new ActionType();
			switch (i) {
			case PermissionController.CAN_READ:
				at.setId(i);
				at.setTitle("READ");
				break;
			case PermissionController.CAN_WRITE:
				at.setId(i);
				at.setTitle("WRITE");
				break;
			case PermissionController.CAN_ALL:
				at.setId(i);
				at.setTitle("ALL");
				break;
			default:
				break;
			}
			if (at.getId() > 0) {
				actionTypeList.add(at);
			}
		}
	}

	public List<ActionType> getActionTypeList() {
		return actionTypeList;
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		if (mode.equals("create")) {
			pageHeader = "Содание роли";
		} else if (mode.equals("update")) {
			pageHeader = "Редактирование роли";
		} else {
			pageHeader = "Просмотр роли";
		}
	}

	public void Save() {
		try {
			if(!PermissionController.canCreate(userData, TRANSACTION_ID)){
				throw new DAOException("Нет доступа!");
			}
			
			if (!userData.isSys_admin()) {
				if(userData.getL_ra_map().containsKey(role.getRole_id())){
					throw new DAOException("Не доступа");
				}
			}
			if (mode.equals("create")) {
				role.setRole_id(null);
				Create();
			} else if (mode.equals("update")) {
				Update();
			}

			GeneralUtil.doRedirect("/dit/role/View.xhtml?roleId=" + role.getRole_id());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		RoleService rService = (RoleService) appContext.getContext().getBean("roleService");
		rService.createRole(role, items);
	}

	private void Update() {
		RoleService rService = (RoleService) appContext.getContext().getBean("roleService");
		rService.updateRole(role, items);
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private user.User userData;

	public user.User getUserData() {
		return userData;
	}

	public void setUserData(user.User userData) {
		this.userData = userData;
	}
}
