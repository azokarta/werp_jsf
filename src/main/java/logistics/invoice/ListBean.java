package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.PermissionController;
import general.dao.BranchDao;
import general.dao.InvoiceDao;
import general.dao.StaffDao;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.Staff;
import general.tables.search.InvoiceSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import logistics.LgHelperBean;
import datamodels.InvoiceModel;
import user.User;

@ManagedBean(name = "logInvoiceListBean")
@ViewScoped
public class ListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5984369581760444973L;

	private Long posTransactionId = 234L;
	private Long posInTransactionId = 17L;
	private Long sendTransactionId = 108L;
	private Long writeOffDocTransactionId = 108L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				i = new Integer(GeneralUtil.getRequestParameter("i"));
			} catch (NumberFormatException e) {
				i = 0;
			}
			prepareUserBranches();
			prepareStfMap();
			if (!userData.isSys_admin() && !userData.isMain()) {
				searchModel.setBukrs(userData.getBukrs());
			}
		}
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void prepareStfMap() {
		StaffDao sd = (StaffDao) appContext.getContext().getBean("staffDao");
		stfMap = sd.getMappedList("");
	}

	private InvoiceModel invoiceModel;

	public InvoiceModel getInvoiceModel() {
		return invoiceModel;
	}

	public void setInvoiceModel(InvoiceModel invoiceModel) {
		this.invoiceModel = invoiceModel;
	}

	private Integer typeId;

	public void initBean(String type) {
		if (!GeneralUtil.isAjaxRequest()) {
			invoiceModel = new InvoiceModel(appContext.getContext().getBean("invoiceDao", InvoiceDao.class), userData);
			invoiceModel.setStfMap(stfMap);
			if (type.equals("writeoff_doc")) {
				PermissionController.canRead(userData, writeOffDocTransactionId);
				typeId = Invoice.TYPE_WRITEOFF_DOC;

				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_WRITEOFF_DOC);
				invoiceModel.setAdditionalCondition(" ((service_number > 0 AND from_werks=0) OR service_number=0 ) ");

			} else if (type.equals("posting")) {
				typeId = Invoice.TYPE_POSTING;
				PermissionController.canRead(userData, posTransactionId);
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_POSTING);

			} else if (type.equals("posting_in")) {
				PermissionController.canRead(userData, posInTransactionId);
				typeId = Invoice.TYPE_POSTING_IN;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_POSTING_IN);
			} else if (type.equals("writeoff")) {

				typeId = Invoice.TYPE_WRITEOFF;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_WRITEOFF);

			} else if (type.equals("send")) {
				PermissionController.canRead(userData, sendTransactionId);
				typeId = Invoice.TYPE_SEND;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_SEND);
			} else if (type.equals("accountability")) {

				typeId = Invoice.TYPE_ACCOUNTABILITY;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_ACCOUNTABILITY);

			} else if (type.equals("return")) {

				typeId = Invoice.TYPE_RETURN;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_RETURN);

			} else if (type.equals("accountability_return")) {

				typeId = Invoice.TYPE_ACCOUNTABILITY_RETURN;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_ACCOUNTABILITY_RETURN);

			} else if (type.equals("mini_contract")) {
				typeId = Invoice.TYPE_MINI_CONTRACT;
				invoiceModel.getSearchModel().setType_id(Invoice.TYPE_MINI_CONTRACT);
			} else {
				typeId = 0;
				invoiceModel.getSearchModel().setType_id(0);
			}

			if (typeId.equals(Invoice.TYPE_SEND)) {
				if (i == 0) {
					invoiceModel.setAdditionalCondition(String.format(" ( status_id = %d OR status_id = %d) ",
							Invoice.STATUS_MOVING, Invoice.STATUS_NEW));
				}
			}

			invoiceModel.getSearchModel().setStatus_id(i == 0 ? Invoice.STATUS_NEW : Invoice.STATUS_DONE);

			if (!userData.isMain() && !userData.isSys_admin()) {
				invoiceModel.getSearchModel().setBukrs(userData.getBukrs());
				invoiceModel.setUserBranches(lgHelperBean.getUserBranches(userData.getBukrs()));
			}
			setPageHeader();
			loadInvoiceList();
		}

	}

	private Integer i = 0;
	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		MessageProvider mp = new MessageProvider();
		if (typeId.equals(Invoice.TYPE_WRITEOFF_DOC)) {
			pageHeader = mp.getValue("logistics.write_off_doc");
		} else if (typeId.equals(Invoice.TYPE_RETURN)) {
			//pageHeader = "Документы возврата";
			pageHeader = mp.getValue("logistics.return_docs");
		} else if (typeId.equals(Invoice.TYPE_POSTING)) {
			//pageHeader = "Оприходования";
			pageHeader = mp.getValue("logistics.posting_docs");
		} else if (typeId.equals(Invoice.TYPE_WRITEOFF)) {
			pageHeader = mp.getValue("logistics.write_off_doc");
			//pageHeader = "Списания";
		} else if (typeId.equals(Invoice.TYPE_SEND)) {
			pageHeader = mp.getValue("logistics.send_docs");
			//pageHeader = "Отправки";
		} else if (typeId.equals(Invoice.TYPE_POSTING_IN)) {
			pageHeader = mp.getValue("logistics.posting_in_docs");
			//pageHeader = "Внутреннее оприходование";
		} else if (typeId.equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
			pageHeader = mp.getValue("logistics.return_from_account");
			if (i == 0) {
				pageHeader += " / " + mp.getValue("logistics.new_docs");
			} else if (i == 1) {
				pageHeader += " / " + mp.getValue("logistics.closed_docs");
			}
		} else if (typeId.equals(Invoice.TYPE_ACCOUNTABILITY)) {
			pageHeader = mp.getValue("logistics.embezzlement");
			if (i == 0) {
				pageHeader += " / " + mp.getValue("logistics.new_docs");
			} else if (i == 1) {
				pageHeader += " / " + mp.getValue("logistics.closed_docs");;
			}
		} else if (typeId.equals(Invoice.TYPE_MINI_CONTRACT)) {
			pageHeader = mp.getValue("logistics.reserved_docs");
			//pageHeader = "Зарезервированные материалы (Мини дог)";
			if (i == 0) {
				pageHeader += " / " + mp.getValue("logistics.new_docs");;
			} else if (i == 1) {
				pageHeader += " / " + mp.getValue("logistics.closed_docs");;
			}
		}
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = bd.findChilds(userData.getBranch_id());
	}

	private List<Invoice> invoiceList = new ArrayList<Invoice>();

	public void loadInvoiceList() {
		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean("invoiceDao");

		String cond = searchModel.getCondition();
		cond += (cond.length() > 0 ? " AND " : " ") + String.format(" type_id = %d AND status_id = %d ", typeId,
				i == 0 ? Invoice.STATUS_NEW : Invoice.STATUS_DONE);
		if (typeId.equals(Invoice.TYPE_SEND)) {
			if (i == 0) {
				cond = String.format(" type_id = %d AND ( status_id = %d OR status_id = %d) ", typeId,
						Invoice.STATUS_MOVING, Invoice.STATUS_NEW);
			}
		}

		if (!userData.isSys_admin() && !userData.isMain()) {
			String[] brIds = new String[userBranches.size()];
			for (int k = 0; k < userBranches.size(); k++) {
				brIds[k] = userBranches.get(k).getBranch_id().toString();
			}

			if (brIds.length > 0) {
				cond += String.format(" AND branch_id IN(%s) ", "'" + String.join("','", brIds) + "'");
			} else {
				cond += " AND branch_id = -1 ";
			}
		}

		cond += " ORDER BY id DESC ";

		invoiceList = d.findAll(cond);
		for (Invoice i : invoiceList) {
			Staff stf = stfMap.get(i.getResponsible_id());
			if (stf != null) {
				i.setResponsible(stf);
			}
		}
	}

	public InvoiceSearch searchModel = new InvoiceSearch();

	public InvoiceSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(InvoiceSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Invoice> getInvoiceList() {
		return invoiceList;
	}

	public void resetSearchParams() {
		searchModel = new InvoiceSearch();
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

	@ManagedProperty("#{lgHelperBean}")
	LgHelperBean lgHelperBean;

	public LgHelperBean getLgHelperBean() {
		return lgHelperBean;
	}

	public void setLgHelperBean(LgHelperBean lgHelperBean) {
		this.lgHelperBean = lgHelperBean;
	}

}
