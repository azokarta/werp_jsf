package hr.doc;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.dao.MyDocsDao;
import general.tables.HrDoc;
import general.tables.MyDocs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrDocListBean")
@ViewScoped
public class HrDocList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1133282610760024285L;

	@PostConstruct
	public void init() {
		// PermissionController.canRead(userData, transactionId);
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				status = new Integer(GeneralUtil.getRequestParameter("i"));
			} catch (NumberFormatException e) {
				status = 0;
			}
		}
	}

	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (!GeneralUtil.isAjaxRequest()) {
			this.type = type;
			setPageHeader();

			loadItems();
		}
	}

	private List<MyDocs> items = new ArrayList<MyDocs>();

	public List<MyDocs> getItems() {
		return items;
	}

	public void setItems(List<MyDocs> items) {
		this.items = items;
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		MessageProvider mp = new MessageProvider();
		if (type == HrDoc.TYPE_RECRUITMENT) {
			pageHeader = mp.getValue("hr.doc.recruitment");
			if (status == 0) {
				pageHeader += " - " + mp.getValue("hr.new_docs");
			}
		} else if (type == HrDoc.TYPE_DISMISS) {
			pageHeader = mp.getValue("hr.doc.dismiss");
			if (status == 0) {
				pageHeader += " - " + mp.getValue("hr.new_docs");
			}
		} else if (type == HrDoc.TYPE_TRANSFER) {
			pageHeader = mp.getValue("hr.doc.transfer");
			if (status == 0) {
				pageHeader += " - " + mp.getValue("hr.new_docs");
			}
		} else if (type == HrDoc.TYPE_CHANGE_SALARY) {
			pageHeader = mp.getValue("hr.doc.change_salary");
			if (status == 0) {
				pageHeader += " - " + mp.getValue("hr.new_docs");
			}
		}
	}

	private void loadItems() {
		MyDocsDao d = (MyDocsDao) appContext.getContext().getBean("myDocsDao");

		HrDoc tempHrDoc = new HrDoc();
		tempHrDoc.setTypeId(type);

		String cond = String.format(" context = '%s' AND owner = %d ", tempHrDoc.getContext(), userData.getUserid());
		System.out.println("COND: " + cond);
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