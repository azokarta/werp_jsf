package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.services.InvoiceService;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Salary;
import general.tables.Staff;
import logistics.LogHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.SelectEvent;

import datamodels.MatnrModel;
import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "logWriteoffLossCrudBean")
@ViewScoped
public class WriteoffLossCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7661916278003572527L;

	public static Long TRANSACTION_ID = 314L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
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

	public void initBean(String mode) {
		this.mode = mode;

		if (!GeneralUtil.isAjaxRequest()) {
			if (mode.equals("create")) {
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_WRITEOFF_LOSS);
				invoice.setBranch_id(userData.getBranch_id());
				invoice.setBukrs(userData.getBukrs());
				invoice.setDepartment_id(6L);

			} else if (mode.equals("update")) {
				loadInvoice();
				loadInvoiceItems();
				loadMlListInWerks(invoice.getFrom_werks());

			} else if (mode.equals("view")) {

				loadInvoice();
				loadInvoiceItems();
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

	public void assignMl() {
		if (selectedMl != null && currentItem != null) {
			if (selectedMlMap.containsKey(selectedMl.getMatnr())) {
				GeneralUtil.addErrorMessage(
						String.format("Материал %s уже имеется в списке", selectedMl.getMatnrObject().getText45()));
			} else {

				currentItem.setMatnr(selectedMl.getMatnr());
				currentItem.setMatnrObject(selectedMl.getMatnrObject());
				selectedMlMap.put(selectedMl.getMatnr(), 1);
				selectedMl = null;
			}
		}
	}

	public void addMatnrsRow() {
		invoiceItems.add(new InvoiceItem());
	}

	public void deleteMatnrsRow(InvoiceItem ii) {
		invoiceItems.remove(ii);
		selectedMlMap.remove(ii.getMatnr());
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

			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/invoice/writeoff/loss/View.xhtml?id=" + invoice.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");

		s.create(invoice, invoiceItems, null, barcodesMap, userData.getUserid());
	}

	private void Update() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");
		s.update(invoice, invoiceItems, null, barcodesMap, userData.getUserid());
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

	public List<String> getItemBarcodes(Long matnrId) {
		return barcodesMap.get(matnrId);
	}

	List<MatnrList> mlList = new ArrayList<MatnrList>();

	public List<MatnrList> getMlList() {
		return mlList;
	}

	public void setMlList(List<MatnrList> mlList) {
		this.mlList = mlList;
	}

	public void loadMlList(InvoiceItem item) {
		mlList = new ArrayList<MatnrList>();
		try {
			if (Validation.isEmptyLong(invoice.getFrom_werks())) {
				throw new DAOException("Выберите склад отправителя");
			}
			MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
			String cond = String.format(
					" werks = %d AND matnr = %d AND barcode IS NOT NULL AND (staff_id IS NULL OR staff_id = 0) AND status = '%s' ",
					invoice.getFrom_werks(), item.getMatnr(), MatnrList.STATUS_RECEIVED);
			mlList = mlDao.findAll(cond, 0);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onSelectMl(SelectEvent se) {
		MatnrList ml = (MatnrList) se.getObject();
		List<String> barcodes = new ArrayList<String>();
		if (barcodesMap.get(ml.getMatnr()) != null) {
			barcodes = barcodesMap.get(ml.getMatnr());
		}
		if (!barcodes.contains(ml.getBarcode())) {

			barcodes.add(ml.getBarcode());
			barcodesMap.put(ml.getMatnr(), barcodes);
			for (InvoiceItem ii : invoiceItems) {
				if (ii.getMatnr().equals(ml.getMatnr())) {
					ii.setQuantity(new Double(barcodes.size()));
					break;
				}
			}
		}

		GeneralUtil.hideDialog("werksMlWidget");
	}

	public void removeBarcode(Long matnrId, String barcode) {
		List<String> barcodes = new ArrayList<String>();
		for (Entry<Long, List<String>> e : barcodesMap.entrySet()) {
			if (e.getKey().equals(matnrId)) {
				barcodes = e.getValue();
				barcodes.remove(barcode);
				e.setValue(barcodes);
				break;
			}
		}

		for (InvoiceItem ii : invoiceItems) {
			if (ii.getMatnr().equals(matnrId)) {
				ii.setQuantity(new Double(barcodes.size()));
				break;
			}
		}
	}

	public void bukrsChangeListener(AjaxBehaviorEvent e) {
		String b = (String) ((UIOutput) e.getSource()).getValue();
		invoiceItems = new ArrayList<InvoiceItem>();

	}

	public void fromWerksChangeListener(AjaxBehaviorEvent e) {
		loadMlListInWerks((Long) ((UIOutput) e.getSource()).getValue());
	}

	private MatnrList selectedMl;

	public MatnrList getSelectedMl() {
		return selectedMl;
	}

	public void setSelectedMl(MatnrList selectedMl) {
		this.selectedMl = selectedMl;
	}

	private Map<Long, Integer> selectedMlMap = new HashMap<Long, Integer>();

	List<MatnrList> mlListInWerks = new ArrayList<MatnrList>();

	public List<MatnrList> getMlListInWerks() {
		return mlListInWerks;
	}

	private void loadMlListInWerks(Long werksId) {
		MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
		mlListInWerks = mlDao.findAllInWerks(werksId, MatnrList.STATUS_RECEIVED);
		System.out.println("SIZE: " + mlListInWerks.size());
	}
}
