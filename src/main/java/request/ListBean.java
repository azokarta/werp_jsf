package request;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.BranchDao;
import general.dao.MyDocsDao;
import general.dao.RequestDao;
import general.dao.UserDao;
import general.tables.Branch;
import general.tables.MyDocs;
import general.tables.Request;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "requestListBean")
@ViewScoped
public class ListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8784543239090628190L;
	private static final Long TRANSACTION_ID = 84L;
	private static final String transactionCode = "REQ";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, TRANSACTION_ID);
			Integer i;
			try {
				i = new Integer(GeneralUtil.getRequestParameter("i"));
			} catch (NumberFormatException e) {
				i = 0;
			}

			if (i == 1) {
				status = Request.STATUS_4;
			} else {
				status = Request.STATUS_1;
			}

			prepareUserBranches();
			loadStaffMap();
			loadItems();

		}
	}

	private Integer status;

	private List<Request> items = new ArrayList<Request>();

	public List<Request> getItems() {
		return items;
	}

	private void loadItems() {
		RequestDao rd = (RequestDao)appContext.getContext().getBean("requestDao");
		items = rd.findAll(getPreparedCondition() + " ORDER BY id DESC");
		for (Request r : items) {
			Staff stf = stfMap.get(r.getCreated_by());
			if (stf == null) {
				r.setCreator(new Staff());
			} else {
				r.setCreator(stf);
			}
		}
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			stfMap.put(u.getUser_id(), u.getStaff());
		}
	}

	private String bukrs;
	private Long branchId;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	private void prepareUserBranches() {
		userBranches.clear();

		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		userBranches = brDao.findChilds(userData.getBranch_id());
	}

	public String getPreparedCondition() {
		String[] userBranchIds = new String[userBranches.size()];
		for (int k = 0; k < userBranches.size(); k++) {
			userBranchIds[k] = userBranches.get(k).getBranch_id().toString();
		}
		String cond = " status_id = " + status;
		if (!userData.isSys_admin() && !userData.isMain()) {
			if (userBranchIds.length > 0) {
				cond += String.format(" AND (branch_id IN(%s) OR res_branch_id IN(%s)) ",
						"'" + String.join("','", userBranchIds) + "'",
						"'" + String.join("','", userBranchIds) + "'");
			} else {
				cond += " AND md.branch_id = -1 ";
			}

		}

		return cond.length() > 0 ? cond : " 1 = 1 ";
	}
}
