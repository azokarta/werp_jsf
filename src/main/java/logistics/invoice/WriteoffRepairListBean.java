package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.UserDao;
import general.dao.WriteoffRepairDao;
import general.tables.Branch;
import general.tables.Staff;
import general.tables.WriteoffRepair;

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

@ManagedBean(name = "logWriteoffRepairListBean")
@ViewScoped
public class WriteoffRepairListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3661444719375266167L;
	private static final Long transactionId = 378L;
	private static final String transactionCode = "LG_WOFF_REP";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			try {
				i = new Integer(GeneralUtil.getRequestParameter("i"));
			} catch (NumberFormatException e) {
				i = 0;
			}

			setPageHeader();
			prepareStaffUserMap();
			setUserBranches();
			loadItems();
		}
	}

	private Integer i = 0;
	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		MessageProvider mp = new MessageProvider();
		pageHeader = mp.getValue("logistics.writeoff_repair");
		if (i == 1) {
			pageHeader += " / " + mp.getValue("logistics.closed_docs");
		} else {
			pageHeader += " / " + mp.getValue("logistics.new_docs");
		}
	}

	private List<WriteoffRepair> items = new ArrayList<WriteoffRepair>();

	public void loadItems() {
		String cond = "";
		if (i == 1) {
			cond = " status_id = " + WriteoffRepair.STATUS_DONE;
		} else {
			cond = " status_id = " + WriteoffRepair.STATUS_NEW;
		}

		if (!userData.isMain() && !userData.isSys_admin()) {
			String[] ids = new String[userBranches.size()];
			for (int k = 0; k < userBranches.size(); k++) {
				ids[k] = userBranches.get(k).getBranch_id().toString();
			}
			if (ids.length > 0) {
				cond += String.format(" AND branch_id IN(%s) ", String.join(",", ids));
			} else {
				cond += " AND branch_id = -1 ";
			}
		}
		
		cond += " ORDER BY id DESC ";

		WriteoffRepairDao d = (WriteoffRepairDao) appContext.getContext().getBean("writeoffRepairDao");
		items = d.findAll(cond);
		for (WriteoffRepair wr : items) {
			Staff stf = stfUserMap.get(wr.getCreatedBy());
			if (stf != null) {
				wr.setCreator(stf);
			}
		}
	}

	private List<Branch> userBranches = new ArrayList<Branch>();
	private Map<Long, Staff> stfUserMap = new HashMap<Long, Staff>();

	private void prepareStaffUserMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			if (!Validation.isEmptyLong(u.getStaff_id()) && u.getStaff() != null
					&& !Validation.isEmptyLong(u.getStaff().getStaff_id())) {
				stfUserMap.put(u.getUser_id(), u.getStaff());
			}
		}
	}

	private void setUserBranches() {
		userBranches = new ArrayList<Branch>();
		BranchDao d = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = d.findChilds(userData.getBranch_id());
		// System.out.println("SIZE: " + userBranches.size() + " " +
		// userBranches.get(0).getText45());
	}

	public List<WriteoffRepair> getItems() {
		return items;
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
}
