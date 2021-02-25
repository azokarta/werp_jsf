package logistics.revision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.dao.RevisionDao;
import general.tables.Revision;
import user.User;

@ManagedBean(name = "logRevListBean")
@ViewScoped
public class ListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3157186682478526879L;

	@PostConstruct
	public void checkAccess() {
		System.out.println("CHECKING ACCESS REVISION...");
		loadItems();
	}

	private String pageHeader = "Список ревизии";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	List<Revision> items = new ArrayList<>();

	public List<Revision> getItems() {
		return items;
	}

	private void loadItems() {
		RevisionDao revDao = appContext.getContext().getBean("revisionDao", RevisionDao.class);
		items = revDao.findAll();
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
