package crm.beans.call;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import crm.constants.CallConstants;
import crm.dao.CrmCallDao;
import crm.tables.CrmCall;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.PyramidDao;
import general.tables.Branch;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "crmCallListBean")
@ViewScoped
public class CallListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5103579909393266530L;

	private final Long transactionId = 760L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			loadItems();
		}
	}

	private void prepareSearchModel() {
		branchIds = new ArrayList<>();
		if (userData.isSys_admin() || userData.isMain()) {
			if (!Validation.isEmptyLong(branchId)) {
				branchIds.add(branchId.toString());
			}
		} else {
			setBukrs(userData.getBukrs());
			List<Branch> userBranches = userData.getUserBranches();
			if (userBranches.size() == 1) {
				setBranchId(userBranches.get(0).getBranch_id());
				loadManagers();
			}

			if (Validation.isEmptyLong(branchId)) {
				if (Validation.isEmptyCollection(userData.getUserBranches())) {
					setBranchId(-1L);
				} else {

					for (Branch br : userBranches) {
						branchIds.add(br.getBranch_id().toString());
					}
				}
			} else {
				branchIds.add(branchId.toString());
			}
		}
	}

	private String pageHeader = "Список звонков";
	private List<CrmCall> items;
	private String bukrs;
	private Long branchId;
	private Long managerId;
	private List<Salary> managers;
	private List<String> branchIds;
	private int totalCount;
	private Date fromDate;
	private Date toDate;
	private Map<Integer, String> results = CallConstants.getResults();
	private Integer resultId;

	public Integer getResultId() {
		return resultId;
	}

	public void setResultId(Integer resultId) {
		this.resultId = resultId;
	}

	public Map<Integer, String> getResults() {
		return results;
	}

	public void setResults(Map<Integer, String> results) {
		this.results = results;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<Salary> getManagers() {
		return managers;
	}

	public void setManagers(List<Salary> managers) {
		this.managers = managers;
	}

	public List<CrmCall> getItems() {
		return items;
	}

	public void setItems(List<CrmCall> items) {
		this.items = items;
	}

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

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public void loadItems() {
		prepareSearchModel();
		CrmCallDao ccDao = (CrmCallDao) appContext.getContext().getBean("crmCallDao");
		items = ccDao.findAllWithDetails(bukrs, branchIds, managerId, fromDate, toDate, resultId);
		setTotalCount(items.size());
	}

	private void loadManagers() {
		PyramidDao pDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		managers = pDao.findAllManagersByBranchId(bukrs, branchId);
	}

	public void onBranchChange(AjaxBehaviorEvent e) {
		loadManagers();
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
