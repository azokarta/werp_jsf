package crm.beans.reco;

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
import crm.constants.RecoConstants;
import crm.dao.CrmDocRecoDao;
import crm.tables.CrmDocReco;
import crm.tables.search.CrmDocRecoSearch;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.PyramidDao;
import general.tables.Branch;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "crmRecoListBean")
@ViewScoped
public class CrmRecoListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Long transactionId = 762L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			loadItems();
		}
	}

	private void prepareSearchModel() {
		if (userData.isSys_admin() || userData.isMain()) {

		} else {
			searchModel.setBukrs(userData.getBukrs());
			List<Branch> userBranches = userData.getUserBranches();
			if (Validation.isEmptyCollection(userBranches)) {
				searchModel.setBranchId(-1L);
			} else if (userBranches.size() == 1) {
				searchModel.setBranchId(userBranches.get(0).getBranch_id());
				loadManagers();
			}

			if (Validation.isEmptyLong(searchModel.getBranchId())) {
				List<String> temp = new ArrayList<>();
				for (Branch br : userBranches) {
					temp.add(br.getBranch_id().toString());
				}
				searchModel.setBranchIds(temp);
			}
		}
	}

	private CrmDocRecoSearch searchModel = new CrmDocRecoSearch();
	private String pageHeader = "Список рекомендации";
	private List<CrmDocReco> items;
	private Long managerId;
	private List<Salary> managers;
	private List<Salary> dealers;
	private int totalCount;
	private Map<Integer, String> statuses = RecoConstants.getStatuses();

	public List<Salary> getDealers() {
		return dealers;
	}

	public void setDealers(List<Salary> dealers) {
		this.dealers = dealers;
	}

	public Map<Integer, String> getStatuses() {
		return statuses;
	}

	public void setStatuses(Map<Integer, String> statuses) {
		this.statuses = statuses;
	}

	public CrmDocRecoSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CrmDocRecoSearch searchModel) {
		this.searchModel = searchModel;
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

	public List<CrmDocReco> getItems() {
		return items;
	}

	public void setItems(List<CrmDocReco> items) {
		this.items = items;
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
		searchModel.setCallerIsDealer(-1);
		CrmDocRecoDao rDao = (CrmDocRecoDao) appContext.getContext().getBean("crmDocRecoDao");
		items = rDao.findAllWithDetailsByCondition(searchModel);
		setTotalCount(items.size());
	}

	private void loadManagers() {
		PyramidDao pDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		managers = pDao.findAllManagersByBranchId(searchModel.getBukrs(), searchModel.getBranchId());
	}

	private void loadDealers() {
		PyramidDao pDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		dealers = pDao.findAllDealersByBranchManagerId(searchModel.getBukrs(), searchModel.getBranchId(), managerId);
		System.out.println(dealers.size());
		List<String> temp = new ArrayList<>();
		for (Salary sal : dealers) {
			temp.add(sal.getStaff_id().toString());
		}

		searchModel.setResponsibleIds(temp);
	}

	public void onBranchChange(AjaxBehaviorEvent e) {
		loadManagers();
	}

	public void onManagerChange(AjaxBehaviorEvent e) {
		loadDealers();
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
