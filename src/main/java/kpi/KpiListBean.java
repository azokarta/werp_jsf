package kpi;

import general.AppContext;
import general.dao2.KpiSettingDao;
import general.tables.KpiSetting;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "kpiListBean")
@ViewScoped
public class KpiListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3563149406337187674L;
	private Long transactionId = 234L;

	@PostConstruct
	public void init() {
		loadItems();
		setPageHeader();
	}

	List<KpiSetting> items;
	private String bukrs;
	private Long branchId;
	private Long positionId;

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

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public List<KpiSetting> getItems() {
		return items;
	}

	public void setItems(List<KpiSetting> items) {
		this.items = items;
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		pageHeader = "Список KPI настроек";
	}

	public void loadItems() {
		KpiSettingDao dao = (KpiSettingDao) appContext.getContext().getBean("kpiSettingDao");
		items = dao.findAll("");
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
