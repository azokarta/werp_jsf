package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.RequestDao;
import general.dao.RequestMatnrDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.dao.WerksBranchDao;
import general.services.InvoiceService;
import general.services.MatnrListService;
import general.services.RelatedDocsService;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Request;
import general.tables.RequestMatnr;
import general.tables.Salary;
import general.tables.Staff;
import logistics.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.TreeNode;

import datamodels.MatnrModel;
import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "logSendCrudBean")
@ViewScoped
public class SendCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6860437664088743162L;
	static final Long transactionId = 108L;
	static final String transactionCode = "LG_SEND";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			prepareUserBranches();
			loadStaffMap();
			loadMatnrMap();
			prepareMatnrModel();
			prepareSalaryModel();
		}
	}

	List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = bd.findChilds(userData.getBranch_id());
	}

	private Long id;

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void loadMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			stfMap.put(u.getUser_id(), u.getStaff());
		}
	}

	private String mode;

	public void initBean(String mode) {
		this.mode = mode;

		if (!GeneralUtil.isAjaxRequest()) {
			if (mode.equals("create")) {
				PermissionController.canWriteRedirect(userData, transactionId);
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_SEND);
				invoice.setBukrs(userData.getBukrs());
				invoice.setBranch_id(userData.getBranch_id());
				prepareLoadedParentDocs();
			} else if (mode.equals("update")) {
				loadInvoice();
				loadInvoiceItems();
				prepareLoadedParentDocs();
				loadParentDocs();
				loadMlListInWerks(invoice.getFrom_werks());

			} else if (mode.equals("view")) {

				loadInvoice();
				loadInvoiceItems();
				loadParentDocs();
				prepareRelatedDocsTree();
				if (invoice != null) {
					try {
						actionBean.initBean(invoice);
					} catch (Exception e) {

					}
				}
			}

			preparePageHeader();
		}
	}

	public String getMode() {
		return mode;
	}

	private void loadInvoice() {
		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean("invoiceDao");
		invoice = d.find(id);
		if (invoice != null) {
			Staff stf = stfMap.get(invoice.getCreated_by());
			if (stf == null) {
				invoice.setCreator(new Staff());
			} else {
				invoice.setCreator(stf);
			}
		}
	}

	List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	private Map<Long, List<String>> barcodesMap = new HashMap<Long, List<String>>();

	private void loadInvoiceItems() {
		invoiceItems = new ArrayList<InvoiceItem>();
		if (invoice != null) {
			InvoiceItemDao d = (InvoiceItemDao) appContext.getContext().getBean("invoiceItemDao");
			List<InvoiceItem> temp = d.findAll("invoice_id = " + invoice.getId());
			LogUtil.prepareInvoiceItems(temp, invoiceItems, matnrMap, barcodesMap);
		}
	}

	private InvoiceItem currentItem;

	public InvoiceItem getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(InvoiceItem currentItem) {
		this.currentItem = currentItem;
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	private Map<Long, MatnrList> selectedMlMap = new HashMap<Long, MatnrList>();

	public void assignMatnr() {
		if (selectedMl != null && currentItem != null) {
			if (selectedMlMap.containsKey(selectedMl.getMatnr())) {
				GeneralUtil
						.addErrorMessage(String.format("Материал %s уже имеется в списке", selectedMl.getMatnrName()));
			} else {

				currentItem.setMatnr(selectedMl.getMatnr());
				currentItem.setMatnrObject(selectedMl.getMatnrObject());
				selectedMlMap.put(selectedMl.getMatnr(), selectedMl);
				selectedMl = null;
			}
		}
	}

	public void addMatnrsRow() {
		invoiceItems.add(new InvoiceItem());
	}

	public void deleteMatnrsRow(InvoiceItem ii) {
		invoiceItems.remove(ii);
		// selectedMatnrMap.remove(ii.getMatnr());
	}

	private Invoice invoice;

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	private MatnrModel matnrModel;

	public MatnrModel getMatnrModel() {
		return matnrModel;
	}

	public void setMatnrModel(MatnrModel matnrModel) {
		this.matnrModel = matnrModel;
	}

	private void prepareMatnrModel() {
		matnrModel = new MatnrModel((MatnrDao) appContext.getContext().getBean("matnrDao"));
	}

	private InvoiceItem barcodeItem;

	public InvoiceItem getBarcodeItem() {
		return barcodeItem;
	}

	public void setBarcodeItem(InvoiceItem barcodeItem) {
		this.barcodeItem = barcodeItem;
	}

	public void Save() {
		try {

			if (!PermissionController.canCreate(userData, transactionId)) {
				throw new DAOException(PermissionController.NO_PERMISSION_MSG);
			}

			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}
			GeneralUtil.doRedirect("/logistics/invoice/send/View.xhtml?id=" + invoice.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");

		s.create(invoice, invoiceItems, getMappedParents(), barcodesMap, userData.getUserid());
	}

	private Map<String, List<Long>> getMappedParents() {
		Map<String, List<Long>> m = new HashMap<String, List<Long>>();
		for (Request r : parentDocs) {
			List<Long> t = new ArrayList<Long>();
			if (m.containsKey(Request.CONTEXT)) {
				t = m.get(Request.CONTEXT);
			}

			t.add(r.getId());
			m.put(Request.CONTEXT, t);
		}

		return m;
	}

	private void Update() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");
		s.update(invoice, invoiceItems, getMappedParents(), barcodesMap, userData.getUserid());
	}

	public void Posting() {
		if (mode.equals("view")) {
			if (invoice != null && invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
				try {
					MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
					mls.doPosting(invoice, userData);
					GeneralUtil.addSuccessMessage("Материалы оприходованы успешно!");
				} catch (DAOException e) {
					GeneralUtil.addErrorMessage(e.getMessage());
				}
			}
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		List<String> barcodes = new ArrayList<String>();
		try {
			InputStream in = event.getFile().getInputstream();
			InputStreamReader inReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inReader);
			String line;
			while ((line = br.readLine()) != null) {
				barcodes.add(line);
			}
			if (barcodes.size() == 0) {
				throw new Exception("Список номеров пуст");
			}
			Map<String, Integer> tempMap = new HashMap<String, Integer>();
			String[] b = new String[barcodes.size()];
			for (int i = 0; i < barcodes.size(); i++) {
				b[i] = barcodes.get(i).trim();
				if (tempMap.containsKey(b[i])) {
					throw new Exception(String.format("Номер дублировано %s ", b[i]));
				}

				tempMap.put(b[i], 1);
			}

			currentItem = (InvoiceItem) event.getComponent().getAttributes().get("currentItem");
			if (currentItem == null) {
				throw new Exception("Ошибка в программе. Номера не загружены");
			}

			List<String> tempBarcodes = new ArrayList<String>();
			for (String s : b) {
				tempBarcodes.add(s);
			}

			barcodesMap.put(currentItem.getMatnr(), tempBarcodes);
			currentItem.setBarcodesList(tempBarcodes);
			currentItem.setQuantity(new Double(tempBarcodes.size()));
			GeneralUtil.addSuccessMessage("Номера загружены успешно");

		} catch (IOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		} catch (Exception e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	List<Request> parentDocs = new ArrayList<Request>();
	Map<Long, Integer> selectedParentDocMap = new HashMap<Long, Integer>();
	List<Request> loadedParentDocs;

	public List<Request> getParentDocs() {
		return parentDocs;
	}

	public void setParentDocs(List<Request> parentDocs) {
		this.parentDocs = parentDocs;
	}

	public List<Request> getLoadedParentDocs() {
		return loadedParentDocs;
	}

	public void setLoadedParentDocs(List<Request> loadedParentDocs) {
		this.loadedParentDocs = loadedParentDocs;
	}

	Map<String, List<Request>> loadedParentDocsMap;

	public void changeBukrsListener(AjaxBehaviorEvent e) {
		invoiceItems = new ArrayList<InvoiceItem>();
		loadedParentDocs = new ArrayList<Request>();
		parentDocs = new ArrayList<Request>();
		prepareLoadedParentDocs();
	}

	private void prepareLoadedParentDocs() {
		loadedParentDocs = new ArrayList<Request>();
		RequestDao rd = (RequestDao) appContext.getContext().getBean("requestDao");
		if (userData.isMain() || userData.isSys_admin()) {
			if (loadedParentDocsMap == null) {
				loadedParentDocsMap = new HashMap<String, List<Request>>();
				loadedParentDocsMap.put("1000", new ArrayList<Request>());
				loadedParentDocsMap.put("2000", new ArrayList<Request>());

				List<Request> l = rd.findAll(String.format(" status_id = %d AND res_branch_id IN(%s) ORDER BY id DESC",
						Request.STATUS_1, Branch.AURA_MAIN_BRANCH_ID + "," + Branch.GREEN_LIGHT_MAIN_BRANCH_ID));
				for (Request r : l) {
					Staff stf = stfMap.get(r.getCreated_by());
					if (stf == null) {
						r.setCreator(new Staff());
					} else {
						r.setCreator(stf);
					}

					loadedParentDocsMap.get(r.getBukrs()).add(r);
				}
			}

			if (!Validation.isEmptyString(invoice.getBukrs())) {
				loadedParentDocs = loadedParentDocsMap.get(invoice.getBukrs());
			}
		} else {
			String[] brIds = new String[userBranches.size()];
			for (int k = 0; k < userBranches.size(); k++) {
				brIds[k] = userBranches.get(k).getBranch_id().toString();
			}

			if (brIds.length > 0) {
				loadedParentDocs = rd
						.findAll(String.format(" status_id = %d AND res_branch_id IN(%s) ORDER BY id DESC ",
								Request.STATUS_1, "'" + String.join("','", brIds) + "'"));

				for (Request r : loadedParentDocs) {
					Staff stf = stfMap.get(r.getCreated_by());
					if (stf == null) {
						r.setCreator(new Staff());
					} else {
						r.setCreator(stf);
					}
				}

			}
		}
	}

	private void loadParentDocs() {
	}

	private Request selectedParentDoc;

	public Request getSelectedParentDoc() {
		return selectedParentDoc;
	}

	public void setSelectedParentDoc(Request selectedParentDoc) {
		this.selectedParentDoc = selectedParentDoc;
	}

	public void assignParentDoc() {
		if (selectedParentDoc != null) {
			parentDocs.add(selectedParentDoc);
			selectedParentDocMap.put(selectedParentDoc.getId(), 1);
			addParentDocItems(selectedParentDoc);
			selectedParentDoc = null;
		}
	}

	private void addParentDocItems(Request r) {
		RequestMatnrDao rmd = (RequestMatnrDao) appContext.getContext().getBean("requestMatnrDao");
		List<RequestMatnr> l = rmd.findReqMatnrs(r.getId());
		for (RequestMatnr rm : l) {
			if (!Validation.isEmptyLong(rm.getMatnr())) {
				InvoiceItem ii = new InvoiceItem();
				ii.setBarcode(null);
				ii.setMatnr(rm.getMatnr());
				ii.setQuantity(rm.getQuantity());
				ii.setMatnrObject(matnrMap.get(rm.getMatnr()));
				invoiceItems.add(ii);
			}
		}
	}

	public void deleteParentDocRow(Request r) {
		parentDocs.remove(r);
		selectedParentDocMap.remove(r.getId());
	}

	private void prepareSalaryModel() {
		salaryModel = new SalaryModel((SalaryDao) appContext.getContext().getBean("salaryDao"));
	}

	private SalaryModel salaryModel;

	public SalaryModel getSalaryModel() {
		return salaryModel;
	}

	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	private Salary selectedSalary;

	public Salary getSelectedSalary() {
		return selectedSalary;
	}

	public void setSelectedSalary(Salary selectedSalary) {
		this.selectedSalary = selectedSalary;
	}

	public void assignSalary() {
		if (selectedSalary != null && selectedSalary.getP_staff() != null) {
			invoice.setResponsible_id(selectedSalary.getStaff_id());
			invoice.setResponsible(selectedSalary.getP_staff());
		}
	}

	public void generateMatnrFromRequest() {
		try {
			if (invoice == null) {
				throw new DAOException("Invoice Is Null!");
			}

			if (Validation.isEmptyLong(invoice.getBranch_id())) {
				throw new DAOException("Выберите филиал");
			}

			if (Validation.isEmptyLong(invoice.getTo_werks())) {
				throw new DAOException("Выберите склада получателя");
			}
			WerksBranchDao wbd = (WerksBranchDao) appContext.getContext().getBean("werksBranchDao");
			List<Branch> brList = wbd.findAllBranchesByWerks(invoice.getTo_werks());
			if (brList.size() > 0) {
				String[] brIds = new String[brList.size()];
				for (int k = 0; k < brList.size(); k++) {
					brIds[k] = brList.get(k).getBranch_id().toString();
				}

				RequestMatnrDao rmDao = (RequestMatnrDao) appContext.getContext().getBean("requestMatnrDao");
				String cond = String.format(
						" request_id IN( SELECT id FROM Request req WHERE req.branch_id IN(%s) AND req.res_branch_id=%d AND status_id=%d ) AND matnr IS NOT NULL AND matnr > 0 ",
						"'" + String.join("','", brIds) + "'", invoice.getBranch_id(), Request.STATUS_1);

				List<RequestMatnr> rmList = rmDao.findAll(cond);
				for (RequestMatnr rm : rmList) {
					InvoiceItem ii = new InvoiceItem();
					Matnr m = matnrMap.get(rm.getMatnr());
					ii.setMatnr(rm.getMatnr());
					ii.setMatnrObject(m);
					ii.setQuantity(rm.getQuantity());
					invoiceItems.add(ii);
				}
			}

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private String pageHeader;

	private void preparePageHeader() {
		pageHeader = LogHelper.getPreparedPageHeader(invoice, mode);
	}

	public String getPageHeader() {
		return pageHeader;
	}

	private TreeNode relatedDocsTree;

	public TreeNode getRelatedDocsTree() {
		return relatedDocsTree;
	}

	public void setRelatedDocsTree(TreeNode relatedDocsTree) {
		this.relatedDocsTree = relatedDocsTree;
	}

	private void prepareRelatedDocsTree() {
		RelatedDocsService rds = (RelatedDocsService) appContext.getContext().getBean("relatedDocsService");
		relatedDocsTree = rds.getRelatedTree(invoice.getId(), Invoice.CONTEXT);
	}

	public void fromWerksChangeListener(AjaxBehaviorEvent e) {
		loadMlListInWerks((Long) ((UIOutput) e.getSource()).getValue());
	}

	public List<MatnrList> getMlListInWerks() {
		return mlListInWerks;
	}

	public void setMlListInWerks(List<MatnrList> mlListInWerks) {
		this.mlListInWerks = mlListInWerks;
	}

	private MatnrList selectedMl;

	public MatnrList getSelectedMl() {
		return selectedMl;
	}

	public void setSelectedMl(MatnrList selectedMl) {
		this.selectedMl = selectedMl;
	}

	List<MatnrList> mlListInWerks = new ArrayList<MatnrList>();

	private void loadMlListInWerks(Long werksId) {
		MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
		mlListInWerks = mlDao.findAllInWerks(werksId, MatnrList.STATUS_RECEIVED);
	}

	@ManagedProperty("#{logInvoiceActionBean}")
	ActionBean actionBean;

	public ActionBean getActionBean() {
		return actionBean;
	}

	public void setActionBean(ActionBean actionBean) {
		this.actionBean = actionBean;
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
