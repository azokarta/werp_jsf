package crm.beans.reference;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import crm.dao.CrmRelativeDao;
import crm.services.CrmRelativeService;
import crm.tables.CrmRelative;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import user.User;

@ManagedBean(name = "crmRelativeListBean")
@ViewScoped
public class RelativeListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8244981915700969691L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadItems();
		}
	}

	private CrmRelative selected;
	private String pageHeader = "Список родственных отношении";
	private String breadcrumb = "CRM > Справочники > Список родственных отношении";
	private List<CrmRelative> items;
	private String dialogHeader;

	public List<CrmRelative> getItems() {
		return items;
	}

	public void setItems(List<CrmRelative> items) {
		this.items = items;
	}

	public CrmRelative getSelected() {
		return selected;
	}

	public void setSelected(CrmRelative selected) {
		this.selected = selected;
		setDialogHeader("Редактирование");
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	public void prepareCreate() {
		selected = new CrmRelative();
		setDialogHeader("Добавление");
	}

	public void save() {
		try {
			CrmRelativeService service = (CrmRelativeService) appContext.getContext()
					.getBean("crmRelativeService");
			service.save(selected, userData.getUserid());
			GeneralUtil.addSuccessMessage("Данные сохранены успешно!");
			GeneralUtil.hideDialog("RelativeCreateUpdateDialog");
			loadItems();
			GeneralUtil.updateFormElement("form");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void loadItems() {
		CrmRelativeDao d = (CrmRelativeDao) appContext.getContext().getBean("crmRelativeDao");
		items = d.findAll("");
	}

	private SearchModel searchModel = new SearchModel();

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	public class SearchModel {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCondition() {
			String cond = "";
			if (!Validation.isEmptyString(getName())) {
				cond = " name LIKE '%" + getName() + "%' ";
			}
			return cond;
		}
	}

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
