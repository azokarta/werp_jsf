package crm.beans.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

@ManagedBean(name = "dailyDemoReportBean")
@ViewScoped
public class DailyDemoReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7851928933275225964L;
	private static final Long transactionId = 740L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			loadItems();
		}
	}

	private String pageHeader = "Ежедневный демо-отчет для директоров";
	private List<CrmDocDemo> items;
	private List<Salary> dealers;
	private List<Salary> managers;
	private CrmDocDemoSearch searchModel = new CrmDocDemoSearch();
	private Long managerId;
	private Date demoDate = new Date();
	private int totalCount = 0;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	private void prepareSearchModel() {
		searchModel.setFromDate(getDemoDate());
		searchModel.setToDate(getDemoDate());
		// searchModel.setResultId(DemoConstants.RESULT_SOLD);
		if (userData.isSys_admin() || userData.isMain()) {

		} else {
			searchModel.setBukrs(userData.getBukrs());
			List<Branch> userBranches = userData.getUserBranches();
			if (userBranches.size() == 1) {
				searchModel.setBranchId(userBranches.get(0).getBranch_id());
				loadManagers();
			}
			if (Validation.isEmptyLong(searchModel.getBranchId())) {
				if (Validation.isEmptyCollection(userData.getUserBranches())) {
					searchModel.setBranchId(-1L);
				} else {
					List<String> branchIds = new ArrayList<>();
					for (Branch br : userBranches) {
						branchIds.add(br.getBranch_id().toString());
					}
					searchModel.setBranchIds(branchIds);
				}
			} else {
				if (!Validation.isEmptyCollection(getDealers())) {
					List<String> dealerIds = new ArrayList<>();
					for (Salary sal : getDealers()) {
						dealerIds.add(sal.getStaff_id().toString());
					}

					searchModel.setDealerIds(dealerIds);
				}
			}
		}
	}

	public Date getDemoDate() {
		return demoDate;
	}

	public void setDemoDate(Date demoDate) {
		this.demoDate = demoDate;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public List<Salary> getDealers() {
		return dealers;
	}

	public void setDealers(List<Salary> dealers) {
		this.dealers = dealers;
	}

	public List<Salary> getManagers() {
		return managers;
	}

	public void setManagers(List<Salary> managers) {
		this.managers = managers;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

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

	public void loadItems() {
		if (getDemoDate() == null) {
			GeneralUtil.addErrorMessage("Введите дату");
		} else {
			prepareSearchModel();
			CrmDocDemoDao dao = (CrmDocDemoDao) appContext.getContext().getBean("crmDocDemoDao");
			items = dao.findAll(getSearchModel());
			setTotalCount(items.size());
		}
	}

	public void onBranchChange(AjaxBehaviorEvent e) {
		loadManagers();
	}

	private void loadManagers() {
		PyramidDao pDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		managers = pDao.findAllManagersByBranchId(getSearchModel().getBukrs(), getSearchModel().getBranchId());
	}

	public void onManagerChange(AjaxBehaviorEvent e) {
		PyramidDao pDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		dealers = pDao.findAllDealersByBranchManagerId(getSearchModel().getBukrs(), getSearchModel().getBranchId(),
				getManagerId());

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
