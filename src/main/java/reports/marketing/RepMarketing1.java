package reports.marketing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.reports.MarketingReportService;
import general.tables.Demonstration;
import general.tables.report.MarketingReport1;
import user.User;

@ManagedBean(name = "repMarketing1")
@ViewScoped
public class RepMarketing1 implements Serializable {

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

			setYear(Calendar.getInstance().get(Calendar.YEAR));
			setMonth(Calendar.getInstance().get(Calendar.MONTH));
		}
	}

	private String bukrs;
	private Long branchId;
	private int year;
	private int month;
	private Long dealerId;
	Demonstration demo;
	private int renderType = 1;
	private Long managerPyramidId;
	private String firstColumnName = "Менеджеры";

	public String getFirstColumnName() {
		return firstColumnName;
	}

	public void setFirstColumnName(String firstColumnName) {
		this.firstColumnName = firstColumnName;
	}

	private String pageHeader = "Отчет о демо";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public int getRenderType() {
		return renderType;
	}

	public void setRenderType(int renderType) {
		this.renderType = renderType;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	private List<MarketingReport1> items = new ArrayList<>();

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

	public Long getManagerPyramidId() {
		return managerPyramidId;
	}

	public void setManagerPyramidId(Long managerPyramidId) {
		this.managerPyramidId = managerPyramidId;
	}

	public List<MarketingReport1> getItems() {
		return items;
	}

	public void setItems(List<MarketingReport1> items) {
		this.items = items;
	}

	public void generateData(int renderType) {
		try {
			this.renderType = renderType;
			items = new ArrayList<>();
			MarketingReportService service = appContext.getContext().getBean("marketingReportService",
					MarketingReportService.class);
			items = service.getRep1Data(bukrs, branchId, year, month, managerPyramidId, renderType);
			MarketingReport1 mr = null;
			if (items != null && items.size() > 0) {
				mr = items.get(0);
			}
			this.setFirstColumnName(mr);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void setFirstColumnName(MarketingReport1 mr1) {
		if (renderType == 1) {
			firstColumnName = "Менеджеры";
		} else {
			if (mr1 != null) {
				firstColumnName = "Группа (" + mr1.getStaffName() + ") ";
			}
		}
	}

	public List<String> getDayColumns() {
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		List<String> out = new ArrayList<>();
		for (int k = 1; k <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
			out.add(k + "");
		}

		return out;
	}

	public void expandGroup(MarketingReport1 mr1) {
		this.renderType = 2;
		this.managerPyramidId = mr1.getManagerPyramidId();
		generateData(renderType);
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
