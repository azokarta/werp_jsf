package crm.beans.visit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import crm.constants.CommonConstants;
import crm.services.CrmDocVisitService;
import crm.tables.CrmDocVisit;
import crm.tables.search.CrmDocVisitSearch;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "crmVisitCrudBean")
@ViewScoped
public class VisitCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1245617715764045116L;

	@PostConstruct
	public void init() {

	}

	private CrmDocVisitSearch searchModel = new CrmDocVisitSearch();
	private List<Salary> visitors;
	private String dialogHeader;
	private CrmDocVisit selected;
	private boolean disabledBukrs;
	private boolean disabledBranch;
	private boolean disabledManager;
	private Long selectedManagerId;
	private List<Salary> managers;
	private Map<Integer, String> locations = CommonConstants.getLocations();

	public CrmDocVisitSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CrmDocVisitSearch searchModel) {
		this.searchModel = searchModel;
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

	public List<Salary> getVisitors() {
		return visitors;
	}

	public void setVisitors(List<Salary> visitors) {
		this.visitors = visitors;
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

	public CrmDocVisit getSelected() {
		return selected;
	}

	public void setSelected(CrmDocVisit selected) {
		this.selected = selected;
		setDialogHeader("Редактирование демокарты");
	}

	public void prepareCreate() {
		selected = new CrmDocVisit();
		selected.setBukrs(getSearchModel().getBukrs());
		selected.setBranchId(getSearchModel().getBranchId());
		selected.setVisitorId(getSearchModel().getVisitorId());
		setDialogHeader("Добавление визит");
	}

	public void save() {
		try {
			getService().save(selected, userData.getUserid());
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			setSelected(null);
			GeneralUtil.hideDialog("VisitCreateUpdateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public CrmDocVisitService getService() {
		return (CrmDocVisitService) appContext.getContext().getBean("crmDocVisitService");
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
		loadVisitors();
		List<String> visitorIds = new ArrayList<>();
		for (Salary sal : visitors) {
			visitorIds.add(sal.getStaff_id().toString());
		}

		searchModel.setVisitorIds(visitorIds);
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
		Long brId = searchModel.getBranchId();
		if (Validation.isEmptyLong(brId)) {
			brId = selected == null ? null : selected.getBranchId();
			if (!Validation.isEmptyLong(brId)) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + brId;
			}
		}
		if (!Validation.isEmptyLong(brId)) {
			cond += " AND s1.position_id = " + Position.MANAGER_POSITION_ID;
			managers = getSalaryDao().findAllCurrentWithStaff(cond);
		}
	}

	public void loadVisitors() {
		visitors = new ArrayList<>();
		String bukrs = searchModel.getBukrs();
		Long brId = searchModel.getBranchId();
		if (Validation.isEmptyString(bukrs)) {
			bukrs = selected == null ? null : selected.getBukrs();
		}

		if (Validation.isEmptyLong(brId)) {
			brId = selected == null ? null : selected.getBranchId();
		}

		if (!Validation.isEmptyString(bukrs) && !Validation.isEmptyLong(brId)
				&& !Validation.isEmptyLong(selectedManagerId)) {
			visitors = getPyramidDao().findAllDealersByBranchManagerId(bukrs, brId, selectedManagerId);
		}
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

	private SalaryDao getSalaryDao() {
		return (SalaryDao) appContext.getContext().getBean("salaryDao");
	}

	private PyramidDao getPyramidDao() {
		return (PyramidDao) appContext.getContext().getBean("pyramidDao");
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
		loadVisitors();
		// loadDealers();
		// visitCrudBean.setVisitors(dealers);
	}

	public void onVisitorChange(AjaxBehaviorEvent e) {
		// visitCrudBean.setVisitorId(searchModel.getVisitorId());
	}

	public void searchContract() {
		try {
			if (Validation.isEmptyLong(selected.getContractNumber())) {
				throw new DAOException("Введите номер договора");
			}

			ContractDao cd = (ContractDao) appContext.getContext().getBean("contractDao");
			Contract c = cd.findByContractNumber(selected.getContractNumber());
			if (c == null) {
				throw new DAOException("Контракт не найден!");
			}

			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
			Customer customer = customerDao.find(c.getCustomer_id());
			selected.setClientName(customer.getFullFIO());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
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
