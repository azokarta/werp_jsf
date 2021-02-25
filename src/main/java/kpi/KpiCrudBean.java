package kpi;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao2.KpiSettingDao;
import general.springservice.KpiSettingService;
import general.tables.KpiItem;
import general.tables.KpiSetting;
import general.tables.Matnr;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "kpiCrudBean")
@ViewScoped
public class KpiCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6307257189518563447L;
	static final Long transactionId = 109L;
	static final String transactionCode = "KPI_CRUD";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
		}
	}

	private Map<Integer, String> indicators = KpiConstants.getIndicatorsMap();
	private KpiSetting selected;
	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public Map<Integer, String> getIndicators() {
		return indicators;
	}

	public void setIndicators(Map<Integer, String> indicators) {
		this.indicators = indicators;
	}

	public KpiSetting getSelected() {
		return selected;
	}

	public void setSelected(KpiSetting selected) {
		this.selected = selected;
	}

	private Long id;
	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			initBean();
		}
	}

	private void loadKpi() {
		if (Validation.isEmptyLong(id)) {
			selected = new KpiSetting();
			setPageHeader("Добавление KPI настройки");
		} else {
			KpiSettingDao d = (KpiSettingDao) appContext.getContext().getBean("kpiSettingDao");
			selected = d.findWithDetail(id);
			if (selected == null) {
				selected = new KpiSetting();
			} else {
				if ("view".equals(mode)) {
					setPageHeader("Просмотр KPI настройки №" + selected.getId());
				} else {
					setPageHeader("Редактирование KPI настройки №" + selected.getId());
				}
			}
		}
	}

	private void initBean() {
		loadKpi();
	}

	public void Save() {
		try {
			if (!PermissionController.canCreate(userData, transactionId)) {
				throw new DAOException(PermissionController.NO_PERMISSION_MSG);
			}
			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/kpi/View.xhtml?id=" + selected.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		KpiSettingService service = (KpiSettingService) appContext.getContext().getBean("kpiSettingService");
		service.create(selected, userData.getUserid());
	}

	private void Update() {
		KpiSettingService service = (KpiSettingService) appContext.getContext().getBean("kpiSettingService");
		service.update(selected, userData.getUserid());
	}

	public void addItemRow() {
		KpiItem item = new KpiItem();
		selected.addKpiItem(item);
	}

	public void deleteItemRow(KpiItem item) {
		selected.removeKpiItem(item);
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
