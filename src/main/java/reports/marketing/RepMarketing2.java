package reports.marketing;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.reports.MarketingReportService;
import user.User;

@ManagedBean(name = "repMarketing2")
@ViewScoped
public class RepMarketing2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			if (!userData.isMain() && !userData.isSys_admin()) {
				setBukrs(userData.getBukrs());
			}

			setYear(Calendar.getInstance().get(Calendar.YEAR));
		}
	}

	private String bukrs;
	private Long branchId;
	private int year;
	private Long dealerId;

	private String pageHeader = "Диаграмма демо-продажи";
	private LineChartModel demoModel;
	private LineChartModel saleModel;
	private LineChartModel recoModel;

	public LineChartModel getRecoModel() {
		return recoModel;
	}

	public void setRecoModel(LineChartModel recoModel) {
		this.recoModel = recoModel;
	}

	public LineChartModel getSaleModel() {
		return saleModel;
	}

	public void setSaleModel(LineChartModel saleModel) {
		this.saleModel = saleModel;
	}

	public LineChartModel getDemoModel() {
		return demoModel;
	}

	public void setDemoModel(LineChartModel demoModel) {
		this.demoModel = demoModel;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public void generateData() {
		try {
			demoModel = new LineChartModel();
			saleModel = new LineChartModel();
			recoModel = new LineChartModel();

			MarketingReportService service = appContext.getContext().getBean("marketingReportService",
					MarketingReportService.class);
			Map<String, LineChartSeries> seriesMap = service.getRep2Data(bukrs, branchId, year);

			demoModel.addSeries(seriesMap.get("demo"));

			demoModel.setTitle("Диаграмма демонстрации");
			demoModel.setLegendPosition("e");
			// lineModel.setStacked(true);
			demoModel.setShowPointLabels(true);
			demoModel.setAnimate(true);

			Axis xAxis = new CategoryAxis("Месяц");
			demoModel.getAxes().put(AxisType.X, xAxis);
			Axis yAxis = demoModel.getAxis(AxisType.Y);
			yAxis.setLabel("Количество");
			yAxis.setMin(0);
			yAxis.setMax(getMax(seriesMap.get("demo")) + 50);

			/********* SALE ******************/
			saleModel.addSeries(seriesMap.get("sale"));

			saleModel.setTitle("Диаграмма продаж");
			saleModel.setLegendPosition("e");
			// lineModel.setStacked(true);
			saleModel.setShowPointLabels(true);
			saleModel.setAnimate(true);

			xAxis = new CategoryAxis("Месяц");
			saleModel.getAxes().put(AxisType.X, xAxis);
			yAxis = saleModel.getAxis(AxisType.Y);
			yAxis.setLabel("Количество");
			yAxis.setMin(0);
			yAxis.setMax(getMax(seriesMap.get("sale")) + 50);

			/************* RECOMENDATIONS *************/
			recoModel.addSeries(seriesMap.get("reco"));

			recoModel.setTitle("Диаграмма рекомендации");
			recoModel.setLegendPosition("e");
			// lineModel.setStacked(true);
			recoModel.setShowPointLabels(true);
			recoModel.setAnimate(true);

			xAxis = new CategoryAxis("Месяц");
			recoModel.getAxes().put(AxisType.X, xAxis);
			yAxis = recoModel.getAxis(AxisType.Y);
			yAxis.setLabel("Количество");
			yAxis.setMin(0);
			yAxis.setMax(getMax(seriesMap.get("reco")) + 150);

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private Integer getMax(LineChartSeries series) {
		Integer max = 0;
		for (Entry<Object, Number> e : series.getData().entrySet()) {
			Number n = e.getValue();
			if (n == null) {
				continue;
			}
			Integer i = n.intValue();
			if (i > max) {
				max = i;
			}
		}

		return max;
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
