package hr.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.dao.MyDocsDao;
import general.tables.HrDoc;
import general.tables.MyDocs;
import user.User;

@ManagedBean(name = "hrDocDismissListBean")
@ViewScoped
public class HrDocDismissList implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				status = new Integer(GeneralUtil.getRequestParameter("i"));
			} catch (NumberFormatException e) {
				status = 0;
			}

			loadItems();
			addPageHeader();
		}
	}

	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private String pageHeader = "Заявка об увольнении";

	public String getPageHeader() {
		return pageHeader;
	}

	private void addPageHeader(){
		if(getStatus() == 0){
			pageHeader += " / Новые";
		}else if(getStatus() == 1){
			pageHeader += " / Входящие";
		}else if(getStatus() == 2){
			pageHeader += " / Отправленные";
		}else if(getStatus() == 3){
			pageHeader += " / Согласованные";
		}else if(getStatus() == 4){
			pageHeader += " / Закрытые";
		}else if(getStatus() == 5){
			pageHeader += " / Отказанные";
		}
	}

	private void loadItems() {
		HrDoc doc = new HrDoc();
		doc.setTypeId(HrDoc.TYPE_DISMISS);
		MyDocsDao d = (MyDocsDao) appContext.getContext().getBean("myDocsDao");
		String cond = String.format(" context = '%s' AND owner = %d ", doc.getContext(), userData.getUserid());

		if (getStatus() == 0) {
			cond += " AND status_id = " + MyDocs.STATUS_CREATE;
		} else if (getStatus() == 1) {
			cond += " AND status_id = " + MyDocs.STATUS_IN;
		} else if (getStatus() == 2) {
			cond += " AND status_id = " + MyDocs.STATUS_SENT;
		} else if (getStatus() == 3) {
			cond += " AND status_id = " + MyDocs.STATUS_CONFIRMED;
		} else if (getStatus() == 4) {
			cond += " AND status_id = " + MyDocs.STATUS_CLOSED;
		} else if (getStatus() == 5) {
			cond += " AND status_id = " + MyDocs.STATUS_REFUSED;
		} else {
			cond += " AND status_id = -1 ";
		}

		cond += " ORDER BY context_id DESC ";
		items = d.findAll(cond);
	}

	private List<MyDocs> items = new ArrayList<>();

	public List<MyDocs> getItems() {
		return items;
	}

	public void setItems(List<MyDocs> items) {
		this.items = items;
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