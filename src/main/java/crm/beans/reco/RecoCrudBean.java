package crm.beans.reco;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import crm.constants.RecoConstants;
import crm.dao.CrmDocRecoDao;
import crm.dao.CrmRelativeDao;
import crm.services.CrmDocRecoService;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import crm.tables.CrmDocVisit;
import crm.tables.CrmPhone;
import crm.tables.CrmRelative;
import crm.tables.search.CrmDocRecoSearch;
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

@ManagedBean(name = "crmRecoCrudBean")
@ViewScoped
public class RecoCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5369571668072229252L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
			loadRelatives();
			if (!Validation.isEmptyLong(id)) {
				loadSelected();
			}
		}
	}

	private Long id;
	private String dialogHeader;
	private CrmDocRecoSearch searchModel = new CrmDocRecoSearch();
	private List<CrmDocReco> creatingItems;
	private String creatingItemsBukrs;
	private Long creatingItemsBranchId;
	private Long creatingItemsResponsibleId;
	private List<Staff> owners;
	private String pageHeader;
	private Map<Integer, String> recoStatuses = RecoConstants.getStatuses1();
	private List<Salary> managers;
	private List<Salary> dealers;
	private Long selectedManagerId;
	private boolean disabledBukrs;
	private boolean disabledBranch;
	private boolean disabledManager;
	private List<CrmRelative> relatives;
	private CrmPhone selectedPhone;

	public CrmPhone getSelectedPhone() {
		return selectedPhone;
	}

	public void setSelectedPhone(CrmPhone selectedPhone) {
		this.selectedPhone = selectedPhone;
	}

	public List<CrmRelative> getRelatives() {
		return relatives;
	}

	public void setRelatives(List<CrmRelative> relatives) {
		this.relatives = relatives;
	}

	private void loadRelatives() {
		CrmRelativeDao d = (CrmRelativeDao) appContext.getContext().getBean("crmRelativeDao");
		relatives = d.findAll("");
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

	public Map<Integer, String> getRecoStatuses() {
		return recoStatuses;
	}

	public void setRecoStatuses(Map<Integer, String> recoStatuses) {
		this.recoStatuses = recoStatuses;
	}

	public CrmDocRecoSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CrmDocRecoSearch searchModel) {
		this.searchModel = searchModel;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public List<Staff> getOwners() {
		return owners;
	}

	public void setOwners(List<Staff> owners) {
		this.owners = owners;
	}

	public String getCreatingItemsBukrs() {
		return creatingItemsBukrs;
	}

	public void setCreatingItemsBukrs(String creatingItemsBukrs) {
		this.creatingItemsBukrs = creatingItemsBukrs;
	}

	public Long getCreatingItemsBranchId() {
		return creatingItemsBranchId;
	}

	public void setCreatingItemsBranchId(Long creatingItemsBranchId) {
		this.creatingItemsBranchId = creatingItemsBranchId;
	}

	public Long getCreatingItemsResponsibleId() {
		return creatingItemsResponsibleId;
	}

	public void setCreatingItemsResponsibleId(Long creatingItemsResponsibleId) {
		this.creatingItemsResponsibleId = creatingItemsResponsibleId;
	}

	public List<CrmDocReco> getCreatingItems() {
		return creatingItems;
	}

	public void setCreatingItems(List<CrmDocReco> creatingItems) {
		this.creatingItems = creatingItems;
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	private CrmDocReco selected;

	public CrmDocReco getSelected() {
		return selected;
	}

	public void setSelected(CrmDocReco selected) {
		this.selected = selected;
		setDialogHeader("Редактирование рекомендации");
	}

	public void prepareCreate() {
		selected = new CrmDocReco();
		selected.setBukrs(getSearchModel().getBukrs());
		selected.setBranchId(getSearchModel().getBranchId());
		selected.setResponsibleId(getSearchModel().getResponsibleId());
		setDialogHeader("Добавление рекомендации");
	}

	public void prepareCall(CrmDocReco docReco, CrmPhone crmPhone) {
		this.selected = docReco;
		this.selectedPhone = crmPhone;
	}

	public void prepareCreateItems() {
		setCreatingItems(new ArrayList<>());
	}

	private CrmDocDemo selectedDemo;

	public void prepareCreateItems(CrmDocDemo selectedDemo) {
		setCreatingItems(new ArrayList<>());
		getSearchModel().setBukrs(selectedDemo.getBukrs());
		getSearchModel().setBranchId(selectedDemo.getBranchId());
		getSearchModel().setResponsibleId(selectedDemo.getDealerId());
		this.selectedDemo = selectedDemo;
		this.selectedVisit = null;
		prepareSearchData();
	}

	private CrmDocVisit selectedVisit;

	public void prepareCreateItems(CrmDocVisit selectedVisit) {
		setCreatingItems(new ArrayList<>());
		getSearchModel().setBukrs(selectedVisit.getBukrs());
		getSearchModel().setBranchId(selectedVisit.getBranchId());
		getSearchModel().setResponsibleId(selectedVisit.getVisitorId());
		this.selectedVisit = selectedVisit;
		this.selectedDemo = null;
		prepareSearchData();
	}

	public void save() {
		try {
			getService().save(selected, userData.getUserid());
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("CreateUpdateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void saveItems() {
		try {
			getService().save(creatingItems, userData.getUserid());
			GeneralUtil.addSuccessMessage("Данные сохранены успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void addPhoneRow(CrmDocReco currReco) {
		CrmPhone crmPhone = new CrmPhone();
		currReco.addPhone(crmPhone);
	}

	public void addRow() {
		if (Validation.isEmptyCollection(creatingItems)) {
			creatingItems = new ArrayList<>();
		}

		String error = "";
		if (Validation.isEmptyString(getSearchModel().getBukrs())) {
			error = "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(getSearchModel().getBranchId())) {
			error += "Выберите филиал \n";
		}

		if (Validation.isEmptyLong(getSearchModel().getResponsibleId())) {
			error += "Выберите ответственного сотрудника \n";
		}

		if (Validation.isEmptyString(error)) {
			CrmDocReco cdr = new CrmDocReco();
			cdr.setBukrs(getSearchModel().getBukrs());
			cdr.setBranchId(getSearchModel().getBranchId());
			cdr.setResponsibleId(getSearchModel().getResponsibleId());
			if (selectedDemo != null) {
				cdr.setDemoId(selectedDemo.getId());
			}

			if (selectedVisit != null) {
				cdr.setVisitId(selectedVisit.getId());
			}
			creatingItems.add(cdr);
		} else {
			GeneralUtil.addErrorMessage(error);
		}
	}

	public void removeRow(int index) {
		creatingItems.remove(index);
	}

	public CrmDocRecoService getService() {
		return (CrmDocRecoService) appContext.getContext().getBean("crmDocRecoService");
	}

	private void loadSelected() {
		CrmDocRecoDao dao = (CrmDocRecoDao) appContext.getContext().getBean("crmDocRecoDao");
		selected = dao.findWithDetail(id);

		if (selected != null) {
			// selected.setCalls(cService.findAllByContext(CrmContext.CrmDocReco.toString(),
			// selected.getId()));
			pageHeader = "Просмотр рекомендацию ";
		}
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
		List<String> responsibleIds = new ArrayList<>();
		for (Salary sal : dealers) {
			responsibleIds.add(sal.getStaff_id().toString());
		}

		searchModel.setResponsibleIds(responsibleIds);
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
		Long brId = null;
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
			brId = searchModel.getBranchId();
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + searchModel.getBranchId();
		}
		managers = new ArrayList<>();
		if (Validation.isEmptyLong(brId) && selected != null) {
			brId = selected.getBranchId();
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + brId;
		}
		if (!Validation.isEmptyLong(brId)) {
			cond += " AND s1.position_id = " + Position.MANAGER_POSITION_ID;
			managers = getSalaryDao().findAllCurrentWithStaff(cond);
		}
	}

	public void loadDealers() {
		dealers = new ArrayList<>();
		String bukrs = searchModel.getBukrs();
		Long brId = searchModel.getBranchId();

		if (Validation.isEmptyString(bukrs) && selected != null) {
			bukrs = selected.getBukrs();
		}

		if (Validation.isEmptyLong(brId) && selected != null) {
			brId = selected.getBranchId();
		}

		if (!Validation.isEmptyString(bukrs) && !Validation.isEmptyLong(brId)
				&& !Validation.isEmptyLong(selectedManagerId)) {
			dealers = getPyramidDao().findAllDealersByBranchManagerId(bukrs, brId, selectedManagerId);
		}
	}

	private PyramidDao getPyramidDao() {
		return (PyramidDao) appContext.getContext().getBean("pyramidDao");
	}

	private SalaryDao getSalaryDao() {
		return (SalaryDao) appContext.getContext().getBean("salaryDao");
	}

	public void prepareCreatingItems() {
		// recoCrudBean.setCreatingItemsBukrs(searchModel.getBukrs());
		// recoCrudBean.setCreatingItemsBranchId(searchModel.getBranchId());
		// recoCrudBean.setCreatingItemsResponsibleId(searchModel.getResponsibleId());
		//
		// List<Staff> d = new ArrayList<>();
		// for (Salary sal : dealers) {
		// d.add(sal.getP_staff());
		// }
		//
		// recoCrudBean.setOwners(d);
		// setShowItemsCreateForm(true);
	}

	/**** Search Form Elements Change Listeners ****/
	public void onBukrsChange(AjaxBehaviorEvent e) {
		// visitCrudBean.setBukrs(searchModel.getBukrs());
	}

	public void onBranchChange(AjaxBehaviorEvent e) {
		loadManagers();
		// System.out.println("BranchId: " + searchModel.getBranchId());
		// visitCrudBean.setBranchId(searchModel.getBranchId());
	}

	public void onManagerChange(AjaxBehaviorEvent e) {
		loadDealers();
		// loadDealers();
		// visitCrudBean.setVisitors(dealers);
	}

	public void onVisitorChange(AjaxBehaviorEvent e) {
		// visitCrudBean.setVisitorId(searchModel.getVisitorId());
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
