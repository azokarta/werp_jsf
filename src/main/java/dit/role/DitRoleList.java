package dit.role;

import java.util.ArrayList;
import java.util.List;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.RoleDao;
import general.tables.Role;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ditRoleListBean")
@ViewScoped
public class DitRoleList {
	
	private static final Long TRANSACTION_ID = 129L;

	@PostConstruct
	public void init(){
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		PermissionController.canRead(userData, TRANSACTION_ID);
//		if(!userData.isSys_admin()){
//			GeneralUtil.doRedirect("/no_permission.xhtml");
//		}
		
		loadItems();
	}
	
	public void Search(){
		loadItems();
	}
	
	private List<Role> items;
	public List<Role> getItems() {
		return items;
	}

	private void loadItems(){
		items = new ArrayList<Role>();
		RoleDao d = (RoleDao)appContext.getContext().getBean("roleDao");
		items = d.findAll(searchModel.getCondition());
	}
	
	
	private Role selected;
	public Role getSelected() {
		return selected;
	}

	public void setSelected(Role selected) {
		this.selected = selected;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	@ManagedProperty(value = "#{userinfo}")
	private user.User userData;
	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public user.User getUserData() {
		return userData;
	}

	public void setUserData(user.User userData) {
		this.userData = userData;
	}
	
	private DitRoleSearchModel searchModel = new DitRoleSearchModel();
	public DitRoleSearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(DitRoleSearchModel searchModel) {
		this.searchModel = searchModel;
	}


	public class DitRoleSearchModel{
		private String text45;
		private String bukrs;
		public String getText45() {
			return text45;
		}
		public void setText45(String text45) {
			this.text45 = text45;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		
		public String getCondition(){
			String condition = "";
			if(!Validation.isEmptyString(bukrs)){
				condition = " bukrs = '" + bukrs + "' ";
			}
			
			if(!Validation.isEmptyString(text45)){
				condition += (condition.length() > 0 ? " AND " : " ") + " text45 LIKE '" + text45 + "%' ";
			}
			return condition;
		}
		
	}
}
