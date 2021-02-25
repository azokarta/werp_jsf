package hr.staff;

import f4.BranchF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.AddressDao;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.services.StaffService;
import general.services.UpdFileService;
import general.tables.Address;
import general.tables.Branch;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.UpdFile;
import general.tables.search.StaffSearch;
import hr.salary.HrSalary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;

import datamodels.StaffModel;
import user.User;

@ManagedBean(name = "hrStaffListBean")
@ViewScoped
public class HrStaffList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long transactionId = 83L;

	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		PermissionController.canRead(userData, transactionId);
		prepareUserBranchesMap();
		prepareUserBranches();

		staffModel = new StaffModel((StaffDao) appContext.getContext().getBean("staffDao"));
		staffModel.setUserBranches(userBranches);
		staffModel.setDismissed(false);
	}

	private List<String> selectedBranchIds;

	public List<String> getSelectedBranchIds() {
		return selectedBranchIds;
	}

	public void setSelectedBranchIds(List<String> selectedBranchIds) {
		this.selectedBranchIds = selectedBranchIds;
	}

	private Address residenceAddress = null;
	private Address livingAddress = null;

	public Address getResidenceAddress() {
		return residenceAddress;
	}

	public void setResidenceAddress(Address residenceAddress) {
		this.residenceAddress = residenceAddress;
	}

	public Address getLivingAddress() {
		return livingAddress;
	}

	public void setLivingAddress(Address livingAddress) {
		this.livingAddress = livingAddress;
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		if (!userData.isSys_admin() && !userData.isMain()) {
			BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");
			userBranches = bd.findChilds(userData.getBranch_id());
		}
	}

	private Map<Long, Branch> userBranchesMap = new HashMap<Long, Branch>();

	private void branchChildsRecursive(Long parentBranchId) {
		for (Branch br : branchF4.getBranch_list()) {
			if (!Validation.isEmptyLong(br.getParent_branch_id()) && br.getParent_branch_id().equals(parentBranchId)) {
				userBranchesMap.put(br.getBranch_id(), br);
				branchChildsRecursive(br.getBranch_id());
			}
		}
	}

	private void prepareUserBranchesMap() {
		branchChildsRecursive(userData.getBranch_id());
		userBranchesMap.put(userData.getBranch_id(), new Branch());

	}
	
	public List<Branch> getUserBranches() {
		return userBranches;
	}

	private StaffModel staffModel;
	private Long staffId;
	private Staff staff;
	private List<Staff> items;
	private StaffSearch searchModel = new StaffSearch();
	private String pageHeader;

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

	public boolean canAll() {
		return PermissionController.canAll(userData, transactionId);
	}

	/***************************/

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

	@ManagedProperty("#{positionF4Bean}")
	PositionF4 positionF4Bean;

	public PositionF4 getPositionF4Bean() {
		return positionF4Bean;
	}

	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}
}
