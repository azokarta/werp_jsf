package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.beans.BranchBean;
import general.beans.WerksBean;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.RelatedDocsDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.services.InvoiceService;
import general.services.MatnrListService;
import general.services.RelatedDocsService;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.RelatedDocs;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.Werks;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.TreeNode;

import user.User;
import datamodels.InvoiceModel;
import datamodels.MatnrModel;
import datamodels.SalaryModel;

@ManagedBean(name = "logReturnCrudBean")
@ViewScoped
public class ReturnBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4467180530399737138L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, Invoice.TRANSACTION_ID_RETURN);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			loadStaffMap();
			loadMatnrMap();
			prepareMatnrModel();
			prepareSalaryModel();
		}
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
	private String type;

	private void loadInvoiceModel() {
		invoiceModel = new InvoiceModel(appContext.getContext().getBean("invoiceDao", InvoiceDao.class), userData);
		if (!userData.isSys_admin() && !userData.isMain()) {
			invoiceModel.getSearchModel().setBukrs(userData.getBukrs());
			werksBean.init();
			branchBean.init();
			invoiceModel.setUserBranches(branchBean.getUserBranches(userData.getBukrs()));
			String cond = "";
			List<Werks> userWerks = werksBean.getUserWerks(userData.getBukrs());
			if (userWerks.size() > 0) {
				String[] ids = new String[userWerks.size()];
				for (int k = 0; k < userWerks.size(); k++) {
					ids[k] = userWerks.get(k).getWerks().toString();
				}
				cond = String.format(" from_werks IN(%s) ", String.join(",", ids));
			} else {
				cond = " from_werks = -1 ";
			}

			invoiceModel.setAdditionalCondition(cond);
		}
		invoiceModel.getSearchModel().setStatus_id(Invoice.STATUS_DONE);
		invoiceModel.getSearchModel().setType_id(Invoice.TYPE_WRITEOFF);
	}

	public void initBean(String mode) {
		this.mode = mode;

		if (!GeneralUtil.isAjaxRequest()) {
			if (mode.equals("create")) {
				PermissionController.canWriteRedirect(userData, Invoice.TRANSACTION_ID_RETURN);
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_RETURN);
				invoice.setBukrs(userData.getBukrs());
				invoice.setBranch_id(userData.getBranch_id());
				loadInvoiceModel();

			} else if (mode.equals("update")) {
				loadInvoice();
				loadInvoiceItems();
				loadInvoiceModel();
				loadParentDocs();

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

	private InvoiceModel invoiceModel;

	public InvoiceModel getInvoiceModel() {
		return invoiceModel;
	}

	public void setInvoiceModel(InvoiceModel invoiceModel) {
		this.invoiceModel = invoiceModel;
	}

	public String getMode() {
		return mode;
	}

	public String getType() {
		return type;
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

	private Map<Long, Matnr> selectedMatnrMap = new HashMap<Long, Matnr>();

	public void assignMatnr() {
		if (selectedMatnr != null && currentItem != null) {
			if (selectedMatnrMap.containsKey(selectedMatnr.getMatnr())) {
				GeneralUtil
						.addErrorMessage(String.format("Материал %s уже имеется в списке", selectedMatnr.getText45()));
			} else {

				currentItem.setMatnr(selectedMatnr.getMatnr());
				currentItem.setMatnrObject(selectedMatnr);
				selectedMatnrMap.put(selectedMatnr.getMatnr(), selectedMatnr);
				selectedMatnr = null;
			}
		}
	}

	public void addMatnrsRow() {
		invoiceItems.add(new InvoiceItem());
	}

	public void deleteMatnrsRow(InvoiceItem ii) {
		invoiceItems.remove(ii);
		selectedMatnrMap.remove(ii.getMatnr());
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
			if (parentDocs == null || parentDocs.size() == 0) {
				throw new DAOException("Выберите род. документа");
			}

			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}
			GeneralUtil.doRedirect("/logistics/invoice/return/View.xhtml?id=" + invoice.getId());

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
		List<Long> l = new ArrayList<>();
		for (Invoice i : parentDocs) {
			l.add(i.getId());
		}
		m.put(Invoice.CONTEXT, l);
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

	Map<Long, List<String>> barcodesMap = new HashMap<Long, List<String>>();

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

	List<Invoice> parentDocs = new ArrayList<Invoice>();
	Map<Long, Integer> selectedParentDocMap = new HashMap<Long, Integer>();
	List<Invoice> loadedParentDocs = new ArrayList<Invoice>();

	public List<Invoice> getParentDocs() {
		return parentDocs;
	}

	public void setParentDocs(List<Invoice> parentDocs) {
		this.parentDocs = parentDocs;
	}

	public List<Invoice> getLoadedParentDocs() {
		return loadedParentDocs;
	}

	public void setLoadedParentDocs(List<Invoice> loadedParentDocs) {
		this.loadedParentDocs = loadedParentDocs;
	}

	private void loadParentDocs() {
		RelatedDocsDao rd = (RelatedDocsDao) appContext.getContext().getBean("relatedDocsDao");
		List<RelatedDocs> l1 = rd
				.findAll(String.format("context = '%s' AND context_id = %d ", Invoice.CONTEXT, invoice.getId()));
		if (l1.size() > 0) {
			String[] ids1 = new String[l1.size()];
			for (int i = 0; i < l1.size(); i++) {
				ids1[i] = l1.get(i).getParent_id().toString();
			}

			List<RelatedDocs> l2 = rd.findAll(String.format(" id IN(%s) ", "'" + String.join("','", ids1) + "'"));
			if (l2.size() > 0) {
				String[] ids2 = new String[l2.size()];
				for (int i = 0; i < l2.size(); i++) {
					ids2[i] = l2.get(i).getContext_id().toString();
				}

				InvoiceDao inDao = appContext.getContext().getBean("invoiceDao", InvoiceDao.class);
				parentDocs = inDao.findAll(String.format(" id IN(%s) ", "'" + String.join("','", ids2) + "'"));
			}

		}
	}

	private Invoice selectedParentDoc;

	public Invoice getSelectedParentDoc() {
		return selectedParentDoc;
	}

	public void setSelectedParentDoc(Invoice selectedParentDoc) {
		this.selectedParentDoc = selectedParentDoc;
	}

	public void removeSelectedParentDoc() {
		selectedParentDoc = null;
		parentDocs = new ArrayList<>();
	}

	public void assignParentDoc() {
		if (selectedParentDoc != null) {

			InvoiceItemDao iiDao = (InvoiceItemDao) appContext.getContext().getBean("invoiceItemDao");
			List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + selectedParentDoc.getId());

			invoiceItems = new ArrayList<InvoiceItem>();
			parentDocs = new ArrayList<Invoice>();
			for (InvoiceItem item : itemList) {
				Matnr m = matnrMap.get(item.getMatnr());
				if (m != null) {
					item.setMatnrObject(m);
					invoiceItems.add(item);
				}
			}

			invoice.setBukrs(selectedParentDoc.getBukrs());
			invoice.setBranch_id(selectedParentDoc.getBranch_id());
			invoice.setTo_werks(selectedParentDoc.getFrom_werks());
			invoice.setContract_number(selectedParentDoc.getContract_number());
			invoice.setService_number(selectedParentDoc.getService_number());
			invoice.setAwkey(selectedParentDoc.getAwkey());

			parentDocs.add(selectedParentDoc);
		}
	}

	public void deleteParentDocRow(ParentDocsClass pdc) {
		parentDocs.remove(pdc);
		selectedParentDocMap.remove(pdc.getId());
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

			if (invoice.getType_id() == Invoice.TYPE_SEND) {
				if (Validation.isEmptyLong(invoice.getTo_werks())) {
					throw new DAOException("Выберите склада получателя");
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

	@ManagedProperty("#{werksBean}")
	WerksBean werksBean;

	public WerksBean getWerksBean() {
		return werksBean;
	}

	public void setWerksBean(WerksBean werksBean) {
		this.werksBean = werksBean;
	}

	@ManagedProperty("#{branchBean}")
	BranchBean branchBean;

	public BranchBean getBranchBean() {
		return branchBean;
	}

	public void setBranchBean(BranchBean branchBean) {
		this.branchBean = branchBean;
	}

}
