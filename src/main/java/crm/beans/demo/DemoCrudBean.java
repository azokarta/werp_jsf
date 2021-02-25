package crm.beans.demo;

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

import crm.constants.CommonConstants;
import crm.constants.DemoConstants;
import crm.constants.ReasonConstants;
import crm.dao.CrmReasonDao;
import crm.services.CrmDocDemoService;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import crm.tables.CrmDocVisit;
import crm.tables.CrmReason;
import crm.tables.search.CrmDocDemoSearch;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "crmDemoCrudBean")
@ViewScoped
public class DemoCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1028413783780467082L;

	@PostConstruct
	public void init() {

	}

	private CrmDocDemoSearch searchModel = new CrmDocDemoSearch();

	private String dialogHeader;
	private CrmDocVisit selectedVisit;
	private CrmDocDemo selected;
	private Map<Integer, String> results = DemoConstants.getResults1();
	private Map<Integer, String> locations = CommonConstants.getLocations();
	private List<CrmReason> reasons;
	private Date recallDate;
	private boolean disabledBukrs;
	private boolean disabledBranch;
	private boolean disabledManager;
	private Long selectedManagerId;
	private List<Salary> managers;
	private List<Salary> dealers;

	public boolean isDisabledBukrs() {
		return disabledBukrs;
	}

	public void setDisabledBukrs(boolean disabledBukrs) {
		this.disabledBukrs = disabledBukrs;
	}

	public boolean isDisabledBranch() {
		return disabledBranch;
	}

	public void setDisabledBranch(boolean disabledBranch) {
		this.disabledBranch = disabledBranch;
	}

	public boolean isDisabledManager() {
		return disabledManager;
	}

	public void setDisabledManager(boolean disabledManager) {
		this.disabledManager = disabledManager;
	}

	public Long getSelectedManagerId() {
		return selectedManagerId;
	}

	public void setSelectedManagerId(Long selectedManagerId) {
		this.selectedManagerId = selectedManagerId;
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

	public CrmDocDemoSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CrmDocDemoSearch searchModel) {
		this.searchModel = searchModel;
	}

	public Date getRecallDate() {
		return recallDate;
	}

	public void setRecallDate(Date recallDate) {
		this.recallDate = recallDate;
	}

	public List<CrmReason> getReasons() {
		return reasons;
	}

	public void setReasons(List<CrmReason> reasons) {
		this.reasons = reasons;
	}

	public Map<Integer, String> getResults() {
		return results;
	}

	public void setResults(Map<Integer, String> results) {
		this.results = results;
	}

	public Map<Integer, String> getLocations() {
		return locations;
	}

	public void setLocations(Map<Integer, String> locations) {
		this.locations = locations;
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public CrmDocDemo getSelected() {
		return selected;
	}

	public void setSelected(CrmDocDemo selected) {
		this.selected = selected;
		setDialogHeader("Редактирование демокарты");
	}

	public CrmDocVisit getSelectedVisit() {
		return selectedVisit;
	}

	public void setSelectedVisit(CrmDocVisit selectedVisit) {
		this.selectedVisit = selectedVisit;
	}

	public void prepareCreate() {
		selected = new CrmDocDemo();
		selected.setBukrs(getSearchModel().getBukrs());
		selected.setBranchId(getSearchModel().getBranchId());
		selected.setDealerId(getSearchModel().getDealerId());
		setDialogHeader("Добавление демокарты");
	}

	public void prepareCreate(CrmDocDemo parentDemo) {
		selected = new CrmDocDemo();
		//selected.setParentDemo(parentDemo);
		selected.setParentId(parentDemo.getId());
		selected.setBukrs(parentDemo.getBukrs());
		selected.setBranchId(parentDemo.getBranchId());
		selected.setDealerId(parentDemo.getDealerId());
		//selected.setAppointedBy(DemoConstants.APPOINTED_BY_CLIENT);
	}

	public void prepareCreate(CrmDocReco parentReco) {
		selected = new CrmDocDemo();
		selected.setParentReco(parentReco);
		selected.setRecoId(parentReco.getId());
		selected.setClientName(parentReco.getClientName());
		selected.setAddress(parentReco.getAddress());
		selected.setBukrs(parentReco.getBukrs());
		selected.setBranchId(parentReco.getBranchId());
		selected.setDealerId(parentReco.getResponsibleId());
		if (parentReco.getCallerIsDealer() == 1) {
			//selected.setAppointedBy(DemoConstants.APPOINTED_BY_DEALER);
		} else {
			//selected.setAppointedBy(DemoConstants.APPOINTED_BY_DEMOSEC);
		}

		setFromSelectedToSearchModel();
		prepareSearchData();
	}

	private void setFromSelectedToSearchModel() {
		if (selected != null) {
			getSearchModel().setBukrs(selected.getBukrs());
			getSearchModel().setBranchId(selected.getBranchId());
			getSearchModel().setDealerId(selected.getDealerId());
		}
	}

	public void save() {
		try {
			if (DemoConstants.RESULT_MOVED.equals(selected.getResultId())) {
				if (getRecallDate() == null) {
					throw new DAOException("Выберите дату перезвона");
				} else {
					if (selected.getParentReco() != null) {
						selected.getParentReco().setCallDate(getRecallDate());
					}
				}
			}
			getService().save(selected, userData.getUserid());
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			setSelected(null);
			GeneralUtil.hideDialog("DemoCreateUpdateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public CrmDocDemoService getService() {
		return (CrmDocDemoService) appContext.getContext().getBean("crmDocDemoService");
	}

	public void onResultChange() {
		reasons = new ArrayList<>();
		if (selected != null) {
			if (DemoConstants.RESULT_CANCELLED.equals(selected.getResultId())) {
				reasons = getReasonDao().findAllByType(ReasonConstants.TYPE_DEMO_CANCEL);
			} else if (DemoConstants.RESULT_MOVED.equals(selected.getResultId())) {
				reasons = getReasonDao().findAllByType(ReasonConstants.TYPE_DEMO_REFUSE);
			}
		}
	}

	private CrmReasonDao getReasonDao() {
		return (CrmReasonDao) appContext.getContext().getBean("crmReasonDao");
	}

	public void prepareSearchData() {
		if (!userData.isSys_admin() && !userData.isMain()) {
			searchModel.setBukrs(userData.getBukrs());
			setDisabledBukrs(true);
			if (Validation.isEmptyCollection(userData.getUserBranches())) {
				searchModel.setBranchId(-1L);
			} else {
				if (userData.getUserBranches().size() == 1) {
					searchModel.setBranchId(userData.getUserBranches().get(0).getBranch_id());
					setDisabledBranch(true);
					loadUserManager();
					setDisabledManager(true);
				} else {
					searchModel.setBranchIds(userData.getUserBranchIdsAsStringList());
				}
			}
		}

		loadManagers();
		loadDealers();
		List<String> dealerIds = new ArrayList<>();
		for (Salary sal : dealers) {
			dealerIds.add(sal.getStaff_id().toString());
		}

		searchModel.setDealerIds(dealerIds);
	}

	private void loadUserManager() {
		List<Salary> salaries = getSalaryDao().findAllCurrent(" staff_id = " + userData.getStaff().getStaff_id());
		PyramidDao pyramidDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		Pyramid p = pyramidDao.findParentPyramid(userData.getBukrs(), userData.getBranch_id(),
				userData.getStaff().getStaff_id(), salaries.get(0).getPosition_id());
		if (p == null) {
			selectedManagerId = userData.getStaff().getStaff_id();
		} else {
			selectedManagerId = p.getStaff_id();
		}
	}

	public void loadManagers() {
		String cond = "";
		if (Validation.isEmptyString(searchModel.getBukrs())) {
			if (!userData.isMain() && !userData.isSys_admin()) {
				cond = " s1.bukrs = " + userData.getBukrs();
			}
		} else {
			cond = " s1.bukrs = " + searchModel.getBukrs();
		}

		if (Validation.isEmptyCollection(searchModel.getBranchIds())) {
			if (Validation.isEmptyLong(searchModel.getBranchId()) && !userData.isMain() && !userData.isSys_admin()) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id IN("
						+ String.join(",", userData.getUserBranchIdsAsStringArray()) + ") ";
			}
		} else {
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id IN("
					+ String.join(",", searchModel.getBranchIds()) + ") ";
		}

		if (!Validation.isEmptyLong(searchModel.getBranchId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + searchModel.getBranchId();
		}
		managers = new ArrayList<>();
		if (!Validation.isEmptyLong(searchModel.getBranchId())) {
			cond += " AND s1.position_id = " + Position.MANAGER_POSITION_ID;
			managers = getSalaryDao().findAllCurrentWithStaff(cond);
		}
	}

	public void loadDealers() {
		dealers = new ArrayList<>();

		if (!Validation.isEmptyString(searchModel.getBukrs()) && !Validation.isEmptyLong(searchModel.getBranchId())
				&& !Validation.isEmptyLong(selectedManagerId)) {
			dealers = getPyramidDao().findAllDealersByBranchManagerId(searchModel.getBukrs(), searchModel.getBranchId(),
					selectedManagerId);
		}
	}

	private PyramidDao getPyramidDao() {
		return (PyramidDao) appContext.getContext().getBean("pyramidDao");
	}

	private SalaryDao getSalaryDao() {
		return (SalaryDao) appContext.getContext().getBean("salaryDao");
	}

	/**** Search Form Elements Change Listeners ****/
	public void onBukrsChange(AjaxBehaviorEvent e) {
	}

	public void onBranchChange(AjaxBehaviorEvent e) {
		loadManagers();
	}

	public void onManagerChange(AjaxBehaviorEvent e) {
		loadDealers();
	}

	public void onDealerChange(AjaxBehaviorEvent e) {
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
