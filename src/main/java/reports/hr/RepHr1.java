package reports.hr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.RoleDao;
import general.dao.StaffDao;
import general.services.reports.HrReportService;
import general.services.reports.TrainingReportService;
import general.tables.Demonstration;
import general.tables.Role;
import general.tables.Staff;
import general.tables.report.HrReport1;
import general.tables.report.TrainingReport1;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repHr1Bean")
@ViewScoped
public class RepHr1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5382332154598969172L;
	private static final long transactionId = 333L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);

			if (!userData.isSys_admin() && !userData.isMain()) {
				setBukrs(userData.getBukrs());
			}

			RoleDao rDao = appContext.getContext().getBean("roleDao", RoleDao.class);
			roleList = rDao.findAll("");
		}
	}

	List<Role> roleList = new ArrayList<>();

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	private HrReport1 selected;

	public HrReport1 getSelected() {
		return selected;
	}

	public void setSelected(HrReport1 selected) {
		this.selected = selected;
	}

	public void onRowSelect(SelectEvent event) {
		selected = (HrReport1) event.getObject();
	}

	public void onRowUnselect(UnselectEvent event) {
		selected = null;
	}

	private int itemsCount = 0;
	private String bukrs;
	private Long branchId;
	private String username;
	private Long roleId;
	private Long staffId;
	private int isRoot = 2;
	private int isActive = 2;

	public int getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(int isRoot) {
		this.isRoot = isRoot;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public int getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(int itemsCount) {
		this.itemsCount = itemsCount;
	}

	private String pageHeader = "Отчет о ролей у пользователей";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private List<HrReport1> items = new ArrayList<>();

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public List<HrReport1> getItems() {
		return items;
	}

	public void setItems(List<HrReport1> items) {
		this.items = items;
	}

	public void generateData() {
		try {
			items = new ArrayList<>();
			HrReportService service = appContext.getContext().getBean("hrReportService", HrReportService.class);
			items = service.getRep1Data(bukrs, branchId, roleId, isActive, isRoot, username);
			itemsCount = items.size();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void ResetSearchForm() {
		setBukrs(null);
		setBranchId(0L);
		setRoleId(0L);
		setIsActive(2);
		setIsRoot(2);
		setUsername(null);
		// generateData();
	}

	private StaffSearch staffSearchModel = new StaffSearch();

	public StaffSearch getStaffSearchModel() {
		return staffSearchModel;
	}

	public void setStaffSearchModel(StaffSearch staffSearchModel) {
		this.staffSearchModel = staffSearchModel;
	}

	private List<Staff> staffList = new ArrayList<>();

	public void loadStaffList() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		staffList = d.findAll(staffSearchModel.getCondition());
		// System.out.println(staffSearchModel.getCondition());
	}

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	public void onSelectStaff(SelectEvent e) {
		selectedStaff = (Staff) e.getObject();
		staffId = selectedStaff.getStaff_id();
		// System.out.println("SEL: " + selectedStaff.getStaff_id());
		GeneralUtil.hideDialog("StaffListDialog");
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
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
