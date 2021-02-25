package reports.hr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import f4.BranchF4;
import f4.DepartmentF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.services.reports.HrReportService;
import general.tables.Branch;
import general.tables.Department;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.report.HrReport3;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repHr3Bean")
@ViewScoped
public class RepHr3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Long transactionId = 577L;

	/**
	 * 
	 */

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			setBukrs(userData.getBukrs());

			for (Department dep : departmentF4Bean.getL_dep("ru")) {
				departmentMap.put(dep.getDep_id(), dep);
			}

			// generateData();
		}
	}

	private List<Branch> branchList = new ArrayList<>();
	private Map<Long, List<Department>> branchDepartments = new HashMap<>();
	private Map<Long, Department> departmentMap = new HashMap<>();
	private int salaryCount = 0;
	private int staffCount = 0;

	public int getSalaryCount() {
		return salaryCount;
	}

	public void setSalaryCount(int salaryCount) {
		this.salaryCount = salaryCount;
	}

	public int getStaffCount() {
		return staffCount;
	}

	public void setStaffCount(int staffCount) {
		this.staffCount = staffCount;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Branch> branchList) {
		this.branchList = branchList;
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

	private String pageHeader = "Список должностей по отделам";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private Map<Long, Map<Long, List<HrReport3>>> outputMap = new HashMap<>();

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

	private void resetData() {
		outputMap.clear();
		branchList.clear();
		branchDepartments.clear();
		salaryCount = 0;
		staffCount = 0;
	}

	public void generateData() {
		try {
			if (!searchModel.isValid()) {
				throw new DAOException(searchModel.getError());
			}

			resetData();

			HrReportService service = appContext.getContext().getBean("hrReportService", HrReportService.class);
			outputMap = service.getRep3Data(searchModel.getBukrs(), searchModel.getBranchIds(),
					searchModel.getPositionIds(), searchModel.getDepartmentIds(), searchModel.getSalaryDate());

			Map<Long, Integer> tempCountMap = new HashMap<>();
			for (Entry<Long, Map<Long, List<HrReport3>>> e1 : outputMap.entrySet()) {
				System.out.println("BR: " + e1.getKey());
				branchList.add(branchF4Bean.getL_branch_map().get(e1.getKey()));
				List<Department> tempDepList = new ArrayList<>();
				for (Entry<Long, List<HrReport3>> e2 : e1.getValue().entrySet()) {
					tempDepList.add(departmentMap.get(e2.getKey()));

					for (HrReport3 rep : e2.getValue()) {
						for (Salary sal : rep.getSalaryList()) {
							tempCountMap.put(sal.getStaff_id(), 1);
							salaryCount += 1;
						}
					}
				}

				if (tempDepList.size() > 0) {
					branchDepartments.put(e1.getKey(), tempDepList);
				}
			}

			staffCount = tempCountMap.size();

			Collections.sort(branchList, new Comparator<Branch>() {

				@Override
				public int compare(Branch o1, Branch o2) {
					return o1.getText45().compareTo(o2.getText45());
				}
			});

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<Department> getBranchDepartments(Long branchId) {
		return branchDepartments.get(branchId);
	}

	public Map<Long, List<HrReport3>> getDataByBranchId(Long branchId) {
		return outputMap.get(branchId);
	}

	public int getAllSalaryCount(Long branchId, Long departmentId) {
		int out = 0;
		if (outputMap.get(branchId) == null) {
			System.out.println(" BRANCH NOT FOUND: " + branchId);
			return 0;
		}

		if (outputMap.get(branchId).get(departmentId) == null) {
			System.out.println(" DEPARTMENT NOT FOUND2: " + departmentId);
			return 0;
		}
		for (HrReport3 rep : outputMap.get(branchId).get(departmentId)) {
			out += rep.getSalaryCount();
		}

		return out;
	}

	public int getAllStaffCount(Long branchId, Long departmentId) {
		Map<Long, Integer> tempMap = new HashMap<>();
		for (HrReport3 rep : outputMap.get(branchId).get(departmentId)) {
			for (Salary sal : rep.getSalaryList()) {
				tempMap.put(sal.getStaff_id(), 1);
			}
		}

		return tempMap.size();
	}

	public List<HrReport3> getDataByDepartmentId(Long branchId, Long depId) {
		// System.out.println(outputMap.get(branchId));
		if (outputMap.get(branchId) == null) {
			return null;
		}

		List<HrReport3> r = outputMap.get(branchId).get(depId);
		return r;
	}

	public Map<Long, Map<Long, List<HrReport3>>> getOutputMap() {
		return outputMap;
	}

	public void ResetSearchForm() {
		setBukrs(null);
		setBranchId(0L);
		setPositionId(0L);
		setStaffId(0L);
		setDepartmentId(0L);
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

	private SearchModel searchModel = new SearchModel();

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	public class SearchModel {
		private String bukrs;
		private String error = "";
		private List<String> branchIds;
		private Long positionId;
		private Long departmentId;
		private List<String> departmentIds;
		private List<String> positionIds;
		private Date salaryDate;

		public List<String> getPositionIds() {
			return positionIds;
		}

		public void setPositionIds(List<String> positionIds) {
			this.positionIds = positionIds;
		}

		public List<String> getDepartmentIds() {
			return departmentIds;
		}

		public void setDepartmentIds(List<String> departmentIds) {
			this.departmentIds = departmentIds;
		}

		public Date getSalaryDate() {
			return salaryDate;
		}

		public void setSalaryDate(Date salaryDate) {
			this.salaryDate = salaryDate;
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

		public List<String> getBranchIds() {
			return branchIds;
		}

		public void setBranchIds(List<String> branchIds) {
			this.branchIds = branchIds;
		}

		public String getBukrs() {
			return bukrs;
		}

		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}

		public boolean isValid() {
			error = Validation.isEmptyString(bukrs) ? "Выберите компанию" : "";

			return error.length() == 0;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

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

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4Bean;

	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}

	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}

	@ManagedProperty("#{departmentF4Bean}")
	DepartmentF4 departmentF4Bean;

	public DepartmentF4 getDepartmentF4Bean() {
		return departmentF4Bean;
	}

	public void setDepartmentF4Bean(DepartmentF4 departmentF4Bean) {
		this.departmentF4Bean = departmentF4Bean;
	}

}
