package hr.staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import f4.BranchF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.services.SalaryService;
import general.tables.Role_action;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.search.SalarySearch;
import user.User;

@ManagedBean(name = "hrOnDismissList")
@ViewScoped
public class HrOnDismissList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5267237238121216115L;
	private static final long transactionId = 658L;

	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		PermissionController.canRead(userData, transactionId);
		if (!userData.isMain() && !userData.isSys_admin()) {
			searchModel.setBranchIds(userData.getUserBranchIdsAsStringList());
		}
		loadData();
	}

	private Staff staff;
	private List<Salary> items;
	private SalarySearch searchModel = new SalarySearch();
	private String pageHeader = "Список сотрудников которые в процессе увольнения";

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public SalarySearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SalarySearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Salary> getItems() {
		return items;
	}

	public void setItems(List<Salary> items) {
		this.items = items;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void Search() {
		selected = null;
		loadData();
	}

	private Salary selected;

	public Salary getSelected() {
		return selected;
	}

	public void setSelected(Salary selected) {
		this.selected = selected;
	}

	private String breadcrumb;

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	public boolean showButtons() {
		if (userData.isMain()) {
			return true;
		}

		if (userData.isSys_admin()) {
			return true;
		}

		Long rukovodstvoRoleId = 52L;

		for (Role_action ra : userData.getUserRoleActions()) {
			if (ra.getRole_id().equals(rukovodstvoRoleId)) {
				return true;
			}
		}

		return false;
	}

	private void loadData() {
		items = new ArrayList<>();
		SalaryService service = (SalaryService) appContext.getContext().getBean("salaryService");
		items = service.findAllOnDismiss(getSearchModel());
	}

	public boolean canAll() {
		return PermissionController.canAll(userData, transactionId);
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4;

	public BranchF4 getBranchF4() {
		return branchF4;
	}

	public void setBranchF4(BranchF4 branchF4) {
		this.branchF4 = branchF4;
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
