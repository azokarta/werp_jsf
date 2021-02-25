package reports.hr;

import java.io.Serializable;
import java.util.ArrayList;
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
import general.tables.Role;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repHr2Bean")
@ViewScoped
public class RepHr2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -649661975802143196L;
	private static final Long transactionId = 454L;

	/**
	 * 
	 */

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

	private Salary selected;

	public Salary getSelected() {
		return selected;
	}

	public void setSelected(Salary selected) {
		this.selected = selected;
	}

	public void onRowSelect(SelectEvent event) {
		selected = (Salary) event.getObject();
	}

	public void onRowUnselect(UnselectEvent event) {
		selected = null;
	}

	private int itemsCount = 0;
	private String bukrs;
	private Long branchId;
	private Long positionId;
	private Long staffId;
	private Long departmentId;
	private String currency;
	private Double totalAmount = 0D;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
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
	
	

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	private String pageHeader = "Оклады сотрудников";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private List<Salary> items = new ArrayList<>();

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

	public List<Salary> getItems() {
		return items;
	}

	public void setItems(List<Salary> items) {
		this.items = items;
	}

	public void generateData() {
		try {
			items = new ArrayList<>();
			HrReportService service = appContext.getContext().getBean("hrReportService", HrReportService.class);
			items = service.getRep2Data(bukrs, branchId, positionId, departmentId, staffId,currency);
			itemsCount = items.size();
			totalAmount = 0D;
			for(Salary sal: items){
				totalAmount += sal.getAmount();
			}
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void ResetSearchForm() {
		setBukrs(null);
		setBranchId(0L);
		setPositionId(0L);
		setStaffId(0L);
		setDepartmentId(0L);
		setCurrency(null);
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
