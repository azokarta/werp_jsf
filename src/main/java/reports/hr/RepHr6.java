package reports.hr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.services.reports.HrReportService;
import general.tables.Branch;
import general.tables.report.HrReport6;
import user.User;

@ManagedBean(name = "repHr6Bean")
@ViewScoped
public class RepHr6 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @TODO
	 */
	private static final Long transactionId = 719L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			prepareSearchForm();
		}
	}

	private void prepareSearchForm() {
		setBranchIds(new ArrayList<>());
		setBukrs(userData.getBukrs());
		if (!userData.isSys_admin() && !userData.isMain()) {
			setDisableBukrs(true);
			for (Branch br : userData.getUserBranches()) {
				branchIds.add(br.getBranch_id());
			}
			if (Validation.isEmptyCollection(userData.getUserBranches())) {
				setDisableBranch(true);
				setBranchId(-1L);
			} else if (userData.getUserBranches().size() == 1) {
				setDisableBranch(true);
				setBranchId(userData.getUserBranches().get(0).getBranch_id());
			}
		}
	}

	private String bukrs;
	private Long branchId;
	private Long positionId;
	private Long departmentId;
	private List<Long> branchIds;
	private boolean disableBranch;
	private boolean disableBukrs;

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public List<Long> getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(List<Long> branchIds) {
		this.branchIds = branchIds;
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

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public boolean isDisableBranch() {
		return disableBranch;
	}

	public void setDisableBranch(boolean disableBranch) {
		this.disableBranch = disableBranch;
	}

	public boolean isDisableBukrs() {
		return disableBukrs;
	}

	public void setDisableBukrs(boolean disableBukrs) {
		this.disableBukrs = disableBukrs;
	}

	private List<HrReport6> items;

	private String pageHeader = "Отчет категория сотрудников";

	public List<HrReport6> getItems() {
		return items;
	}

	public void setItems(List<HrReport6> items) {
		this.items = items;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private void resetData() {
	}

	public void generateData() {
		try {
			List<Long> brIds = new ArrayList<>();
			if (Validation.isEmptyLong(getBranchId())) {
				brIds = getBranchIds();
			} else {
				brIds.add(getBranchId());
			}
			HrReportService service = appContext.getContext().getBean("hrReportService", HrReportService.class);
			items = service.getRep6Data(getBukrs(), brIds, getPositionId(), getDepartmentId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void ResetSearchForm() {

	}

	public void exportXLS(Object document) {
		// System.out.println(document);
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);

			cell.setCellStyle(cellStyle);
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
