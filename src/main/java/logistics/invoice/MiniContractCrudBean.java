package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.beans.WerksBean;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.dao.WerksBranchDao;
import general.services.InvoiceService;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.Werks;
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
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.SelectEvent;

import datamodels.SalaryModel;
import f4.WerksF4;
import user.User;

@ManagedBean(name = "miniContractCrudBean")
@ViewScoped
public class MiniContractCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1550947026007371490L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, Invoice.TRANSACTION_ID_MINI_CONTRACT);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			loadStaffMap();
			loadMatnrMap();
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
				PermissionController.canWriteRedirect(userData, Invoice.TRANSACTION_ID_MINI_CONTRACT);
				prepareSalaryModel();
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_MINI_CONTRACT);
				invoice.setBranch_id(userData.getBranch_id());
				invoice.setBukrs(userData.getBukrs());
				invoice.setDepartment_id(1L);
				prepareUserWerks();
				if (userWerks.size() == 1) {
					invoice.setFrom_werks(userWerks.get(0).getWerks());
				}
				onBranchChanged();
			} else if (mode.equals("update")) {
				PermissionController.canWriteRedirect(userData, Invoice.TRANSACTION_ID_MINI_CONTRACT);
				prepareSalaryModel();
				loadInvoice();
				// invoice.setBranch_id(userData.getBranch_id());
				loadInvoiceItems();
				loadStaffMatnrList();
				prepareUserWerks();
				onBranchChanged();
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

	private List<MatnrList> staffMatnrList;

	public List<MatnrList> getStaffMatnrList() {
		return staffMatnrList;
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
			for (InvoiceItem ii : invoiceItems) {
				selectedBarcodes.add(ii.getBarcode());
			}
		}
	}

	private InvoiceItem currentItem;

	public InvoiceItem getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(InvoiceItem currentItem) {
		this.currentItem = currentItem;
	}

	public void addMatnrsRow() {
		invoiceItems.add(new InvoiceItem());
	}

	public void deleteMatnrsRow(InvoiceItem ii) {
//		invoiceItems.remove(ii);
//		selectedBarcodes.remove(ii.getBarcode());
//		if (barcodesMap.containsKey(ii.getMatnr())) {
//			barcodesMap.get(ii.getMatnr()).remove(ii.getBarcode());
//		}
		clearAllItems();
	}

	private Invoice invoice;

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public void Save() {
		try {
			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/invoice/reservation/View.xhtml?id=" + invoice.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onFromWerksChanged() {
		clearAllItems();
	}

	private void clearAllItems() {
		invoiceItems = new ArrayList<InvoiceItem>();
		barcodesMap = new HashMap<>();
		selectedBarcodes = new ArrayList<>();
	}

	private List<Werks> userWerks = new ArrayList<>();

	private void prepareUserWerks() {
		if (userData.isSys_admin() || userData.isMain()) {
			userWerks = werksF4Bean.getByBukrs(userData.getBukrs());
		} else {
			userWerks = werksBean.getUserWerks(userData.getBukrs());
		}
	}

	public List<Werks> getUserWerks() {
		return userWerks;
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
		if (!userData.isSys_admin() && !userData.isMain()) {

			salaryModel.getSearchModel().setBukrs(userData.getBukrs());
			// salaryModel.getSearchModel().setUserBranches(getUserWerksBranchesList());

		} else {
			// userWerks = werksBean.getUserWerks(userData.getBukrs());
		}
	}

	public List<Branch> getSelectedWerksBranches() {
		List<Branch> out = new ArrayList<>();
		if (Validation.isEmptyLong(invoice.getFrom_werks())) {
			return out;
		}

		Map<Long, Integer> tempMap = new HashMap<>();
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		WerksBranchDao wbDao = (WerksBranchDao) appContext.getContext().getBean("werksBranchDao");
		for (Branch br : wbDao.findAllBranchesByWerks(invoice.getFrom_werks())) {
			if (!tempMap.containsKey(br.getBranch_id())) {
				if (Branch.TYPE_CITY == br.getType().intValue()) {
					for (Branch br2 : brDao.findChilds(br.getBranch_id())) {
						out.add(br2);
						tempMap.put(br2.getBranch_id(), 1);
					}
				} else if (Branch.TYPE_BRANCH == br.getType().intValue()) {
					out.add(br);
				}
				tempMap.put(br.getBranch_id(), 1);
			}
		}

		return out;
	}

	public void onBukrsChanged() {
		System.out.println("Bukrs Changed");
	}

	public void onBranchChanged() {
		System.out.println("CHANGED BRANCH");
		if (invoice != null && !Validation.isEmptyLong(invoice.getBranch_id())) {
			// System.out.println("Invoice Branch: " + invoice.getBranch_id());
			salaryModel.getSearchModel().setBranch_id(invoice.getBranch_id());
		}
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
			loadStaffMatnrList();
			System.out.println("STFML SIZE: " + staffMatnrList.size());
		}
	}

	private void loadStaffMatnrList() {
		staffMatnrList = new ArrayList<>();
		if (!Validation.isEmptyLong(invoice.getResponsible_id())) {
			MatnrListDao mlDao = appContext.getContext().getBean("matnrListDao", MatnrListDao.class);
			staffMatnrList = mlDao.findAll(String.format(
					" barcode IS NOT NULL AND status = '%s' AND staff_id = %d AND werks=%d AND status='%s' ",
					MatnrList.STATUS_ACCOUNTABILITY, invoice.getResponsible_id(), invoice.getFrom_werks(),
					MatnrList.STATUS_ACCOUNTABILITY), 0);

			for (MatnrList ml : staffMatnrList) {
				Matnr m = matnrMap.get(ml.getMatnr());
				ml.setMatnrName(m.getText45());
				ml.setMatnrCode(m.getCode());
				ml.setMatnrObject(m);
			}
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

	public void onSelectMatnrList(SelectEvent se) {
		MatnrList ml = (MatnrList) se.getObject();
		List<String> barcodes = new ArrayList<String>();
		clearAllItems();

		barcodes.add(ml.getBarcode());
		barcodesMap.put(ml.getMatnr(), barcodes);

		InvoiceItem ii = new InvoiceItem();
		ii.setBarcode(ml.getBarcode());
		ii.setQuantity(1D);
		ii.setMatnrObject(ml.getMatnrObject());
		ii.setMatnr(ml.getMatnr());
		invoiceItems.add(ii);

		GeneralUtil.hideDialog("staffMatnrListWidget");
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

	private MatnrList selectedMl;

	public MatnrList getSelectedMl() {
		return selectedMl;
	}

	public void setSelectedMl(MatnrList selectedMl) {
		this.selectedMl = selectedMl;
	}

	private List<String> selectedBarcodes = new ArrayList<>();

	@ManagedProperty("#{werksBean}")
	WerksBean werksBean;

	public WerksBean getWerksBean() {
		return werksBean;
	}

	public void setWerksBean(WerksBean werksBean) {
		this.werksBean = werksBean;
	}

	@ManagedProperty("#{werksF4Bean}")
	WerksF4 werksF4Bean;

	public WerksF4 getWerksF4Bean() {
		return werksF4Bean;
	}

	public void setWerksF4Bean(WerksF4 werksF4Bean) {
		this.werksF4Bean = werksF4Bean;
	}

}