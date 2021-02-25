package reports.logistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.services.reports.LogisticsReportService;
import general.tables.Staff;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repLog2Bean")
@ViewScoped
public class RepLog2 {

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {

		}
	}

	private String pageHeader = "Отчет сотрудник Подотчет Списание";
	private String bukrs;
	private Long branchId;
	private Long werks;
	private Long staffId;
	private String code;
	private Date fromDate;
	private Date toDate;

	private String error = "";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

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

	public Long getWerks() {
		return werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	private boolean isValidParams() {
		error = "";
		if (Validation.isEmptyString(bukrs)) {
			error += "Выберите компанию \n";
		}

		// if (Validation.isEmptyLong(branchId)) {
		// error += "Выберите филиал \n";
		// }

		if (Validation.isEmptyLong(werks)) {
			error += "Выберите склад \n";
		}

		if (Validation.isEmptyLong(staffId)) {
			error += "Выберите сотрудника \n";
		}

		if (Validation.isEmptyString(code)) {
			error += "Введите код материала \n";
		}

		if (fromDate == null) {
			error += "Введите Дата с \n";
		}

		return error.length() == 0;
	}

	public void generateData() {
		try {
			if (!isValidParams()) {
				throw new DAOException(error);
			}

			LogisticsReportService service = appContext.getContext().getBean("logisticsReportService",
					LogisticsReportService.class);
			items = service.getRep2Data(branchId, werks, staffId, fromDate, toDate, code);

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removeSelectedStaff() {
		selectedStaff = null;
		staffId = 0L;
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

	private List<OutputTable> items = new ArrayList<>();

	public List<OutputTable> getItems() {
		return items;
	}

	public void setItems(List<OutputTable> items) {
		this.items = items;
	}

	public class OutputTable {
		private Double qAccountability = 0D;
		private Double qReturned = 0D;
		private Double qToWriteoff = 0D;
		private Double qWrittenoff = 0D;

		public Double getqAccountability() {
			return qAccountability;
		}

		public void setqAccountability(Double qAccountability) {
			this.qAccountability = qAccountability;
		}

		public Double getqReturned() {
			return qReturned;
		}

		public void setqReturned(Double qReturned) {
			this.qReturned = qReturned;
		}

		public Double getqToWriteoff() {
			return qToWriteoff;
		}

		public void setqToWriteoff(Double qToWriteoff) {
			this.qToWriteoff = qToWriteoff;
		}

		public Double getqWrittenoff() {
			return qWrittenoff;
		}

		public void setqWrittenoff(Double qWrittenoff) {
			this.qWrittenoff = qWrittenoff;
		}
		
		public Double getBalane(){
			return (getqAccountability()-getqReturned()-getqWrittenoff());
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
}
