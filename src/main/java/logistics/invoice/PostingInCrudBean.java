package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.OrderDao;
import general.dao.OrderMatnrDao;
import general.dao.RelatedDocsDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.services.InvoiceService;
import general.services.MatnrListService;
import general.services.RelatedDocsService;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.Order;
import general.tables.OrderMatnr;
import general.tables.RelatedDocs;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.TreeNode;

import datamodels.MatnrModel;
import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "logPostingInCrudBean")
@ViewScoped
public class PostingInCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6860437664088743162L;
	final static Long transactionId = 17L;
	final static String transactionCode = "LG_POS_IN";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
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

	public void initBean(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			if (mode.equals("create")) {
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_POSTING_IN);

				loadDocsForParent();
			} else if (mode.equals("update")) {
				loadInvoice();
				loadInvoiceItems();
				loadDocsForParent();
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
			if (!PermissionController.canCreate(userData, transactionId)) {
				throw new DAOException(PermissionController.NO_PERMISSION_MSG);
			}

			if (mode.equals("create")) {
				if (parentDocs == null || parentDocs.size() == 0) {
					throw new DAOException("Выберите род. документа");
				}
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/invoice/posting-in/View.xhtml?id=" + invoice.getId());

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
		for (ParentDocsClass pdc : parentDocs) {
			List<Long> t = new ArrayList<Long>();
			if (m.containsKey(pdc.getContext())) {
				t = m.get(pdc.getContext());
			}

			t.add(pdc.getId());
			m.put(pdc.getContext(), t);
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

	List<ParentDocsClass> parentDocs = new ArrayList<PostingInCrudBean.ParentDocsClass>();
	Map<Long, Integer> selectedParentDocMap = new HashMap<Long, Integer>();
	List<ParentDocsClass> loadedParentDocs = new ArrayList<PostingInCrudBean.ParentDocsClass>();

	public List<ParentDocsClass> getParentDocs() {
		return parentDocs;
	}

	public void setParentDocs(List<ParentDocsClass> parentDocs) {
		this.parentDocs = parentDocs;
	}

	public List<ParentDocsClass> getLoadedParentDocs() {
		return loadedParentDocs;
	}

	public void setLoadedParentDocs(List<ParentDocsClass> loadedParentDocs) {
		this.loadedParentDocs = loadedParentDocs;
	}

	private void loadDocsForParent() {
		parentDocs = new ArrayList<PostingInCrudBean.ParentDocsClass>();
		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean("invoiceDao");
		List<Invoice> iList = d.findAll(String.format(" type_id = %d AND status_id = %d ORDER BY id DESC",
				Invoice.TYPE_SEND, Invoice.STATUS_MOVING));

		for (Invoice i : iList) {
			ParentDocsClass pdc = new ParentDocsClass();
			pdc.setContext(Invoice.CONTEXT);
			pdc.setDocumentName("Отправка");
			pdc.setId(i.getId());
			Staff stf = stfMap.get(i.getCreated_by());
			if (stf != null) {
				pdc.setCreatorName(stf.getLF());
			}

			loadedParentDocs.add(pdc);
		}
	}

	private void loadParentDocs() {
		RelatedDocsDao d = (RelatedDocsDao) appContext.getContext().getBean("relatedDocsDao");
		RelatedDocs parentRelated = d.findParent(invoice.getId(), Invoice.CONTEXT, Invoice.CONTEXT);
		if (parentRelated != null) {
			InvoiceDao iDao = (InvoiceDao) appContext.getContext().getBean("invoiceDao");
			Invoice parentInvoice = iDao.find(parentRelated.getContext_id());
			if (parentInvoice != null) {
				ParentDocsClass pdc = new ParentDocsClass();
				pdc.setContext(Invoice.CONTEXT);
				pdc.setDocumentName("Отправка");
				pdc.setId(parentInvoice.getId());
				Staff stf = stfMap.get(parentInvoice.getCreated_by());
				if (stf != null) {
					pdc.setCreatorName(stf.getLF());
				}

				parentDocs.add(pdc);
			}
		}
	}

	private ParentDocsClass selectedParentDoc;

	public ParentDocsClass getSelectedParentDoc() {
		return selectedParentDoc;
	}

	public void setSelectedParentDoc(ParentDocsClass selectedParentDoc) {
		this.selectedParentDoc = selectedParentDoc;
	}

	public void assignParentDoc() {
		if (selectedParentDoc != null && !selectedParentDocMap.containsKey(selectedParentDoc.getId())) {

			parentDocs = new ArrayList<PostingInCrudBean.ParentDocsClass>();
			invoiceItems = new ArrayList<InvoiceItem>();
			InvoiceItemDao iiDao = (InvoiceItemDao) appContext.getContext().getBean("invoiceItemDao");
			List<InvoiceItem> iiList = iiDao
					.findQuantitySumGrouppedByMatnr(" invoice_id =  " + selectedParentDoc.getId());

			InvoiceDao invoiceDao = (InvoiceDao) appContext.getContext().getBean("invoiceDao");
			Invoice parentInvoice = invoiceDao.find(selectedParentDoc.getId());
			invoice.setFrom_werks(parentInvoice.getFrom_werks());
			invoice.setTo_werks(parentInvoice.getTo_werks());
			invoice.setBukrs(parentInvoice.getBukrs());
			invoice.setResponsible_id(parentInvoice.getResponsible_id());

			for (InvoiceItem ii : iiList) {
				Matnr m = matnrMap.get(ii.getMatnr());
				if (m != null) {
					InvoiceItem iiNew = new InvoiceItem();
					iiNew.setBarcode(null);
					iiNew.setMatnr(ii.getMatnr());
					iiNew.setMatnrObject(m);
					iiNew.setQuantity(ii.getQuantity());
					invoiceItems.add(iiNew);
				}
			}

			parentDocs.add(selectedParentDoc);
			selectedParentDocMap.put(selectedParentDoc.getId(), 1);
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

	public class ParentDocsClass {
		private Long id;
		private String creatorName;
		private String documentName;
		private String context;

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCreatorName() {
			return creatorName;
		}

		public void setCreatorName(String creatorName) {
			this.creatorName = creatorName;
		}

		public String getDocumentName() {
			return documentName;
		}

		public void setDocumentName(String documentName) {
			this.documentName = documentName;
		}

	}
}
