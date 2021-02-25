package crm.beans.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import crm.constants.RecoConstants;
import crm.services.CrmStandartReportService;
import crm.tables.CrmDocReco;
import crm.tables.report.CrmStandartReport;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.SalaryDao;
import general.tables.Position;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "crmStandartRep1Bean")
@ViewScoped
public class CrmStandartRep1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4414818944151484173L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			setBukrs("1000");
			setDisabledBukrs(true);
			loadItems();

		}
	}

	private boolean disabledBukrs;
	private boolean disabledBranch;
	private DashboardModel dashboardModel;
	private String pageHeader = "Отчет по стандартам";
	private List<CrmStandartReport> items;
	private List<Salary> managers;

	private boolean showItemsCreateForm;

	String breadcrumb = " CRM > Отчеты ";
	private String bukrs;
	private Long branchId;
	private Long managerId;

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

	public List<Salary> getManagers() {
		return managers;
	}

	public void setManagers(List<Salary> managers) {
		this.managers = managers;
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

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public List<CrmStandartReport> getItems() {
		return items;
	}

	public void setItems(List<CrmStandartReport> items) {
		this.items = items;
	}

	public DashboardModel getDashboardModel() {
		return dashboardModel;
	}

	public void setDashboardModel(DashboardModel dashboardModel) {
		this.dashboardModel = dashboardModel;
	}

	public String getStatusStyleClass(CrmDocReco item) {
		if (RecoConstants.STATUS_NEW.equals(item.getStatusId())) {
			if ((new Date()).after(item.getCallDate())) {
				return "reco-new-overdue";
			}
			return "reco-new";
		}

		return "";
	}

	public String getDateStyleClass(CrmDocReco item) {
		if (item == null) {
			return "";
		}

		if ((new Date()).after(item.getCallDate())) {
			return "reco-new-overdue";
		}
		return "reco-new";
	}

	public boolean isShowItemsCreateForm() {
		return showItemsCreateForm;
	}

	public void setShowItemsCreateForm(boolean showItemsCreateForm) {
		this.showItemsCreateForm = showItemsCreateForm;
	}

	public void hideItemsCreateForm() {
		setShowItemsCreateForm(false);
		GeneralUtil.updateFormElement("CreateItemsPanel");
		GeneralUtil.updateFormElement("form");
	}

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public void loadItems() {
		CrmStandartReportService service = (CrmStandartReportService) appContext.getContext()
				.getBean("crmStandartReportService");
		items = service.getStandartReportData(getBukrs(), getBranchId(), managerId);
		dashboardModel = new DefaultDashboardModel();
		DashboardColumn c1 = new DefaultDashboardColumn();
		DashboardColumn c2 = new DefaultDashboardColumn();
		DashboardColumn c3 = new DefaultDashboardColumn();

		boolean b1 = true, b2 = false, b3 = false;
		for (CrmStandartReport item : items) {
			if (b1) {
				c1.addWidget("id-" + item.getId());
				b1 = false;
				b2 = true;
				b3 = false;
			} else if (b2) {
				c2.addWidget("id-" + item.getId());
				b1 = false;
				b2 = false;
				b3 = true;
			} else if (b3) {
				c3.addWidget("id-" + item.getId());
				b1 = true;
				b2 = false;
				b3 = false;
			}
		}

		dashboardModel.addColumn(c1);
		dashboardModel.addColumn(c2);
		dashboardModel.addColumn(c3);
	}

	public void loadManagers() {
		managers = new ArrayList<>();
		String cond = "";
		if (!Validation.isEmptyString(getBukrs())) {
			cond = " s1.bukrs = " + getBukrs();
		}

		if (!Validation.isEmptyLong(getBranchId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + getBranchId();
		}

		if (!Validation.isEmptyString(cond)) {
			cond += " AND s1.position_id = " + Position.MANAGER_POSITION_ID;
			SalaryDao salaryDao = (SalaryDao) appContext.getContext().getBean("salaryDao");
			managers = salaryDao.findAllCurrentWithStaff(cond);
		}
	}

	public void onBranchChange(AjaxBehaviorEvent e) {
		loadManagers();
	}

	public String getResultCss(int result, int min, int average) {
		if (result < min) {
			return "bad-result";
		} else if (result < average) {
			return "normal-result";
		} else {
			return "good-result";
		}
	}

	/***********************************/

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
