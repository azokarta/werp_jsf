package crm.beans.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;


import crm.constants.DemoConstants;
import crm.dao.CrmDocDemoDao;
import crm.tables.CrmDocDemo;
import crm.tables.search.CrmDocDemoSearch;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.PyramidDao;
import general.tables.Branch;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "crmDemoListBean")
@ViewScoped
public class DemoListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5226748144595108695L;
	private final Long transactionId = 763L;

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

	private CrmDocDemoSearch searchModel = new CrmDocDemoSearch();
	private String pageHeader = "Список демонстрации";
	private List<CrmDocDemo> items;
	private Long managerId;
	private List<Salary> managers;
	private List<Salary> dealers;
	private int totalCount;
	private Map<Integer, String> results = DemoConstants.getAllResults();

	public CrmDocDemoSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CrmDocDemoSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<CrmDocDemo> getItems() {
		return items;
	}

	public void setItems(List<CrmDocDemo> items) {
		this.items = items;
	}

	public Map<Integer, String> getResults() {
		return results;
	}

	public void setResults(Map<Integer, String> results) {
		this.results = results;
	}

	public List<Salary> getDealers() {
		return dealers;
	}

	public void setDealers(List<Salary> dealers) {
		this.dealers = dealers;
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
		CrmDocDemoDao dDao = (CrmDocDemoDao) appContext.getContext().getBean("crmDocDemoDao");
		items = dDao.findAll(searchModel);
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

		searchModel.setDealerIds(temp);
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