package crm.beans.reference;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import crm.constants.ReasonConstants;
import crm.dao.CrmReasonDao;
import crm.services.CrmReasonService;
import crm.tables.CrmReason;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import user.User;

@ManagedBean(name = "crmReasonListBean")
@ViewScoped
public class ReasonListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3075531227439717407L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadItems();
		}
	}

	private CrmReason selected;
	private String pageHeader = "Список причин";
	private String breadcrumb = "CRM > Справочники > Список причин";
	private List<CrmReason> items;
	private String dialogHeader;
	private Map<Integer, String> reasonTypes = ReasonConstants.getReasonTypes();

	public Map<Integer, String> getReasonTypes() {
		return reasonTypes;
	}

	public void setReasonTypes(Map<Integer, String> reasonTypes) {
		this.reasonTypes = reasonTypes;
	}

	public List<CrmReason> getItems() {
		return items;
	}

	public void setItems(List<CrmReason> items) {
		this.items = items;
	}

	public CrmReason getSelected() {
		return selected;
	}

	public void setSelected(CrmReason selected) {
		this.selected = selected;
		setDialogHeader("Редактирование причины");
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
		selected = new CrmReason();
		setDialogHeader("Добавление причины");
	}

	public void save() {
		try {
			CrmReasonService service = (CrmReasonService) appContext.getContext().getBean("crmReasonService");
			service.save(selected, userData.getUserid());
			GeneralUtil.addSuccessMessage("Данные сохранены успешно!");
			GeneralUtil.hideDialog("ReasonCreateUpdateDialog");
			loadItems();
			GeneralUtil.updateFormElement("form");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void loadItems() {
		CrmReasonDao crmReasonDao = (CrmReasonDao) appContext.getContext().getBean("crmReasonDao");
		items = crmReasonDao.findAll(searchModel.getCondition());
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
		private Integer typeId;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getTypeId() {
			return typeId;
		}

		public void setTypeId(Integer typeId) {
			this.typeId = typeId;
		}

		public String getCondition() {
			String cond = "";
			if (!Validation.isEmptyString(getName())) {
				cond = " name LIKE '%" + getName() + "%' ";
			}

			if (getTypeId() != null && getTypeId() > 0) {
				cond += (cond.length() > 0 ? " AND " : " ") + " typeId = " + getTypeId();
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
