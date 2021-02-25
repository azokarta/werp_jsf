package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.dao.BranchDao;
import general.dao.InvoiceDao;
import general.dao.UserDao;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "logWriteoffLossListBean")
@ViewScoped
public class WriteoffLossListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1183025549155884032L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				i = new Integer(GeneralUtil.getRequestParameter("i"));
			} catch (NumberFormatException e) {
				i = 0;
			}
			setPageHeader();
			prepareUserBranches();
			prepareUserStfMap();
			loadInvoiceList();
		}
	}

	private Map<Long, Staff> userStffMap = new HashMap<Long, Staff>();

	private void prepareUserStfMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			userStffMap.put(u.getUser_id(), u.getStaff());
		}
	}

	private Integer i = 0;
	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		MessageProvider mp = new MessageProvider();
		pageHeader = mp.getValue("logistics.loss_document_title");
		if (i == 0) {
			pageHeader += " / " + mp.getValue("logistics.new_docs");
		} else if (i == 1) {
			pageHeader += " / " + mp.getValue("logistics.closed_docs");;
		}
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = bd.findChilds(userData.getBranch_id());
	}

	private List<Invoice> invoiceList = new ArrayList<Invoice>();

	private void loadInvoiceList() {
		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean(
				"invoiceDao");

		String cond = String.format(" type_id = %d AND status_id = %d ",
				Invoice.TYPE_WRITEOFF_LOSS, i == 0 ? Invoice.STATUS_NEW
						: Invoice.STATUS_DONE);

		if (!userData.isSys_admin() && !userData.isMain()) {
			String[] brIds = new String[userBranches.size()];
			for (int k = 0; k < userBranches.size(); k++) {
				brIds[k] = userBranches.get(k).getBranch_id().toString();
			}

			if (brIds.length > 0) {
				cond += String.format(" AND branch_id IN(%s) ",
						"'" + String.join("','", brIds) + "'");
			} else {
				cond += " AND branch_id = -1 ";
			}
		}

		cond += " ORDER BY id DESC ";

		invoiceList = d.findAll(cond);
		for (Invoice i : invoiceList) {
			Staff stf = userStffMap.get(i.getCreated_by());
			if (stf != null) {
				i.setCreator(stf);
			}
		}
	}

	public List<Invoice> getInvoiceList() {
		return invoiceList;
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
