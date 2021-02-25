package reference;

import java.util.ArrayList;
import java.util.List;

import general.AppContext;
import general.PermissionController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean
@ViewScoped
public abstract class RefBase<T> {

	private boolean canRead = false;

	@PostConstruct
	public void init() {
		// PermissionController.getInstance().canRead(userData,
		// this.getTransactionId());
	}
	
	T selectedRecord;

	public T getSelectedRecord() {
		return selectedRecord;
	}

	public void setSelectedRecord(T selectedRecord) {
		this.selectedRecord = selectedRecord;
	}

	private String searchName = "";

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	List<T> recordList = null;

	public List<T> getRecordList() {
		return recordList;
	}

	abstract public void Search();

	abstract public void Create();

	abstract public void Update();

	abstract public Long getTransactionId();

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	String breadcrumb = " Справочники ";

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public T prepareCreate() {
		// this.selectedRecord = new T();
		return this.selectedRecord;
	}

	public void Reset() {
		this.selectedRecord = null;
	}
}
