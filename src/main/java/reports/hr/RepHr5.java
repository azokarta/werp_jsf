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

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.services.reports.HrReportService;
import general.tables.Branch;
import general.tables.report.HrReport5;
import user.User;

@ManagedBean(name = "repHr5Bean")
@ViewScoped
public class RepHr5 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @TODO
	 */
	private static final Long transactionId = 718L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			prepareSearchForm();
		}
	}

	private void prepareSearchForm() {
		setBukrs(userData.getBukrs());

		if (!userData.isSys_admin() && !userData.isMain()) {
			setUserBranches(userData.getUserBranches());
			setDisableBukrs(true);
			if (Validation.isEmptyCollection(userData.getUserBranches())) {
				setDisableBranch(true);
			} else if (userData.getUserBranches().size() == 1) {
				setDisableBranch(true);
				setBranchId(userData.getUserBranches().get(0).getBranch_id());
			}
		} else {
			setUserBranches(new ArrayList<>());
		}
	}

	private String bukrs;
	private Long branchId;
	private List<Branch> userBranches;
	private boolean disableBranch;
	private boolean disableBukrs;

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
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

	private List<HrReport5> items;

	private String pageHeader = "Отчет стаж (количественно)";

	public List<HrReport5> getItems() {
		return items;
	}

	public void setItems(List<HrReport5> items) {
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
				for (Branch br : getUserBranches()) {
					brIds.add(br.getBranch_id());
				}
			} else {
				brIds.add(getBranchId());
			}
			HrReportService service = appContext.getContext().getBean("hrReportService", HrReportService.class);
			items = service.getRep5Data(getBukrs(), brIds);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void ResetSearchForm() {

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
