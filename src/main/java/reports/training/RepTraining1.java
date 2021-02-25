package reports.training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.reports.TrainingReportService;
import general.tables.Demonstration;
import general.tables.report.TrainingReport1;
import user.User;

@ManagedBean(name = "repTraining1")
@ViewScoped
public class RepTraining1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2847478204862188921L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			if (!userData.isMain() && !userData.isSys_admin()) {
				setBukrs(userData.getBukrs());
			}
		}
	}

	private String bukrs;
	private Long branchId;
	private Long dealerId;
	private Long demosecId;
	private Date fromDate;
	private Date toDate;
	Demonstration demo;

	private String pageHeader = "Отчет о демо";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private List<TrainingReport1> items = new ArrayList<>();

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

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
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

	public List<TrainingReport1> getItems() {
		return items;
	}

	public void setItems(List<TrainingReport1> items) {
		this.items = items;
	}

	public void generateData() {
		try {
			items = new ArrayList<>();
			TrainingReportService service = appContext.getContext().getBean("trainingReportService",
					TrainingReportService.class);
			items = service.getRep1Data(bukrs, branchId, fromDate, toDate);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
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
