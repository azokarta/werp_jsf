package hr.staff;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import datamodels.StaffModel;
import f4.BranchF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.StaffDao;
import general.tables.Role_action;
import general.tables.Staff;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "hrDismissedList")
@ViewScoped
public class HrDismissedList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6392191741273667655L;
	private static final long transactionId = 122L;

	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		PermissionController.canRead(userData, transactionId);
		staffModel = new StaffModel(appContext.getContext().getBean("staffDao", StaffDao.class));
		staffModel.setDismissed(true);
	}

	private StaffModel staffModel;
	private Staff staff;
	private List<Staff> items;
	private StaffSearch searchModel = new StaffSearch();
	private String pageHeader = "Список уволенных сотрудников";

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public StaffSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(StaffSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Staff> getItems() {
		return items;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public StaffModel getStaffModel() {
		return staffModel;
	}

	public void Search() {
		selected = null;
	}

	private Staff selected;

	public Staff getSelected() {
		return selected;
	}

	public void setSelected(Staff selected) {
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
		Long pomoshnikHr = 353L;

		for (Role_action ra : userData.getUserRoleActions()) {
			if (ra.getRole_id().equals(rukovodstvoRoleId)) {
				return true;
			}
			
			if(pomoshnikHr.equals(ra.getRole_id())){
				return true;
			}
		}

		return false;
	}
	
	public boolean canAll(){
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
