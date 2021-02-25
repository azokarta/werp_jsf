package reports.analytics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.services.reports.AnalyticsReportService;
import general.tables.Month;
import general.tables.report.CustomerReport;
import general.tables.search.CustomerSearch;
import user.User;

@ManagedBean(name = "repAnalytics1")
@ViewScoped
public class RepAnalytics1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1257432002849749289L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			dayList = new ArrayList<>();
			for (int k = 1; k < 32; k++) {
				Day d = new Day();
				d.setId(k);
				d.setValue(k + "");
				dayList.add(d);
			}

			monthList = new ArrayList<>();
			for (int k = 0; k < 12; k++) {
				Month m = new Month(k + 1, monthsRu[k], "ru");
				monthList.add(m);
			}

			Calendar c = Calendar.getInstance();
			day = c.get(Calendar.DAY_OF_MONTH);
			month = c.get(Calendar.MONTH)+1;
		}
	}

	String[] monthsRu = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь",
			"Ноябрь", "Декабрь" };

	private String bukrs;
	private Long branchId;
	private Date birthDate;
	private int month;
	private int day;

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
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

	private String pageHeader = "Отчет о днях рождениях клиентов";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	CustomerSearch searchModel = new CustomerSearch();
	List<CustomerReport> items = new ArrayList<>();
	

	public List<CustomerReport> getItems() {
		return items;
	}

	public void generateData() {
//		if(Validation.isEmptyString(bukrs)){
//			GeneralUtil.addErrorMessage("Выберите компанию");
//			return;
//		}
		
		if(Validation.isEmptyLong(branchId)){
			GeneralUtil.addErrorMessage("Выберите филиал");
			return;
		}
		
		AnalyticsReportService service = appContext.getContext().getBean("analyticsReportService",AnalyticsReportService.class);
		items = service.getRep1Data(getBukrs(),getBranchId(), getDay(), getMonth());
	}

	private List<Month> monthList = new ArrayList<>();

	public List<Month> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<Month> monthList) {
		this.monthList = monthList;
	}

	private List<Day> dayList;

	public List<Day> getDayList() {
		return dayList;
	}

	public void setDayList(List<Day> dayList) {
		this.dayList = dayList;
	}

	public class Day {
		Integer id;
		String value;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public class OutputTable{
		
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
