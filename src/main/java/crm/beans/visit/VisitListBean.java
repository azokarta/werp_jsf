package crm.beans.visit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.TabChangeEvent;

import crm.dao.CrmDocVisitDao;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocVisit;
import crm.tables.search.CrmDocVisitSearch;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "crmVisitListBean")
@ViewScoped
public class VisitListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9108942153130998079L;
	private final static int VIEW_MODE_LIST = 1;
	private final static int VIEW_MODE_CREATE_RECO_ITEMS = 2;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			visitCrudBean.prepareSearchData();
			loadItems();
		}
	}

	private String pageHeader = "Список визитов";
	private String currTabId;
	private List<CrmDocVisit> items;
	private boolean disabledBukrs;
	private boolean disabledBranch;
	private boolean disabledManager;
	private Long selectedManagerId;
	private List<Salary> managers;
	private List<Salary> dealers;
	private int currentViewMode = VIEW_MODE_LIST;

	public boolean showListView() {
		return currentViewMode == VIEW_MODE_LIST;
	}

	public int getCurrentViewMode() {
		return currentViewMode;
	}

	public void setCurrentViewMode(int currentViewMode) {
		this.currentViewMode = currentViewMode;
	}

	public boolean showCreateRecoView() {
		return currentViewMode == VIEW_MODE_CREATE_RECO_ITEMS;
	}

	public void hideRecoItemsCreateForm() {
		setCurrentViewMode(VIEW_MODE_LIST);
		GeneralUtil.updateFormElement("CreateRecoItemsPanel");
		GeneralUtil.updateFormElement("form");
	}

	public void prepareRecoCreatingItems(CrmDocVisit selectedVisit) {
		// recoCrudBean.setCreatingItemsBukrs(selectedDemo.getBukrs());
		// recoCrudBean.setCreatingItemsBranchId(selectedDemo.getBranchId());
		// recoCrudBean.setCreatingItemsResponsibleId(selectedDemo.getDealerId());

		

		//recoCrudBean.setOwners(d);
		setCurrentViewMode(VIEW_MODE_CREATE_RECO_ITEMS);
	}

	public void saveRecoItems() {
		try {
			recoCrudBean.getService().save(recoCrudBean.getCreatingItems(), userData.getUserid());
			hideRecoItemsCreateForm();
			GeneralUtil.addSuccessMessage("Данные сохранены успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void prepareCreate() {

	}

	public List<Salary> getManagers() {
		return managers;
	}

	public void setManagers(List<Salary> managers) {
		this.managers = managers;
	}

	public List<Salary> getDealers() {
		return dealers;
	}

	public void setDealers(List<Salary> dealers) {
		this.dealers = dealers;
	}

	public Long getSelectedManagerId() {
		return selectedManagerId;
	}

	public void setSelectedManagerId(Long selectedManagerId) {
		this.selectedManagerId = selectedManagerId;
	}

	public boolean isDisabledBranch() {
		return disabledBranch;
	}

	public void setDisabledBranch(boolean disabledBranch) {
		this.disabledBranch = disabledBranch;
	}

	public boolean isDisabledBukrs() {
		return disabledBukrs;
	}

	public void setDisabledBukrs(boolean disabledBukrs) {
		this.disabledBukrs = disabledBukrs;
	}

	public boolean isDisabledManager() {
		return disabledManager;
	}

	public void setDisabledManager(boolean disabledManager) {
		this.disabledManager = disabledManager;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public String getCurrTabId() {
		return currTabId;
	}

	public void setCurrTabId(String currTabId) {
		this.currTabId = currTabId;
	}

	public List<CrmDocVisit> getItems() {
		return items;
	}

	public void setItems(List<CrmDocVisit> items) {
		this.items = items;
	}

	public void loadItems() {
		items = getVisitDao().findAll(visitCrudBean.getSearchModel());
	}

	public void onTabChange(TabChangeEvent e) {
		currTabId = (String) e.getTab().getAttributes().get("id");
		loadItems();
	}

	private CrmDocVisitDao getVisitDao() {
		return (CrmDocVisitDao) appContext.getContext().getBean("crmDocVisitDao");
	}

	@ManagedProperty(value = "#{crmVisitCrudBean}")
	private VisitCrudBean visitCrudBean;

	public VisitCrudBean getVisitCrudBean() {
		return visitCrudBean;
	}

	public void setVisitCrudBean(VisitCrudBean visitCrudBean) {
		this.visitCrudBean = visitCrudBean;
	}

	@ManagedProperty(value = "#{crmRecoCrudBean}")
	private crm.beans.reco.RecoCrudBean recoCrudBean;

	public crm.beans.reco.RecoCrudBean getRecoCrudBean() {
		return recoCrudBean;
	}

	public void setRecoCrudBean(crm.beans.reco.RecoCrudBean recoCrudBean) {
		this.recoCrudBean = recoCrudBean;
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