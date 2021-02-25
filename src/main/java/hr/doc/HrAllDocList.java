package hr.doc;

import general.AppContext;
import general.GeneralUtil;
import general.dao.HrDocDao;
import general.tables.HrDoc;
import general.tables.search.HrDocSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import datamodels.HrDocModel;
import user.User;

@ManagedBean(name = "hrAllDocListBean")
@ViewScoped
public class HrAllDocList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9152148876068316069L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			dataModel = new HrDocModel((HrDocDao) appContext.getContext().getBean("hrDocDao"), searchModel);
			if (!userData.isMain() && !userData.isSys_admin()) {
				searchModel.setBukrs(userData.getBukrs());
			}
			searchModel.setStatusId(0);
		}
	}

	public Map<Integer, String> getDocTypes() {
		Map<Integer, String> out = new HashMap<>();
		out.put(HrDoc.TYPE_DISMISS, "Заявление об увольнении");
		out.put(HrDoc.TYPE_RECRUITMENT, "Заявление о приеме");
		out.put(HrDoc.TYPE_TRANSFER, "Заявление о переводе");
		return out;
	}

	public Map<Integer,String> getStatuses() {
		Map<Integer, String> out = new HashMap<>();
		out.put(HrDoc.STATUS_ON_CREATE, "На создании");
		out.put(HrDoc.STATUS_ON_APPROVEMENT, "На согласовании");
		out.put(HrDoc.STATUS_ON_EXECUTION, "На исполнении");
		out.put(HrDoc.STATUS_REFUSED, "Отказано");
		out.put(HrDoc.STATUS_CANCELLED, "Отменено");
		out.put(HrDoc.STATUS_CLOSED, "Закрыт");
		return out;
	}

	private HrDocSearch searchModel = new HrDocSearch();
	private HrDocModel dataModel;

	public HrDocModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(HrDocModel dataModel) {
		this.dataModel = dataModel;
	}

	public HrDocSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(HrDocSearch searchModel) {
		this.searchModel = searchModel;
	}

	List<HrDoc> items = new ArrayList<>();

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public List<HrDoc> getItems() {
		return items;
	}

	public void setItems(List<HrDoc> items) {
		this.items = items;
	}

	private String pageHeader = "Все документы HR отдела";

	public String getPageHeader() {
		return pageHeader;
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