package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.dao.WerksBranchDao;
import general.services.InvoiceService;
import general.services.MatnrListService;
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

import org.primefaces.event.SelectEvent;

import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "logAccountabilityReturnCrudBean")
@ViewScoped
public class AccountabilityReturnCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6312333311747904570L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, Invoice.TRANSACTION_ID_ACCOUNTABILITY_RETURN);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			loadStaffMap();
			loadMatnrMap();
			salaryModel = new SalaryModel((SalaryDao) appContext.getContext().getBean("salaryDao"));
			loadStaffList();
		}
	}

	private Long id;
	private boolean dismissedStaff = false;

	public boolean isDismissedStaff() {
		return dismissedStaff;
	}

	public void setDismissedStaff(boolean dismissedStaff) {
		this.dismissedStaff = dismissedStaff;
	}

	public void onChangeDismissedFlag() {
		invoice.setResponsible(null);
		invoice.setResponsible_id(0L);
		invoiceItems = new ArrayList<>();
		loadStaffMatnrList();
	}

	private List<Staff> staffList = new ArrayList<Staff>();

	public List<Staff> getStaffList() {
		return staffList;
	}

	private void loadStaffList() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		String cond = "staff_id NOT IN(SELECT sl.staff_id FROM Salary sl WHERE end_date >= '"
				+ Helper.getCurrentDateForDb() + "' )";
		staffList = d.findAll(cond);
	}

	public void onSelectStaff(SelectEvent e) {
		Staff stf = (Staff) e.getObject();
		invoiceItems = new ArrayList<>();
		loadStaffMatnrList();
		if (stf != null) {
			invoice.setResponsible_id(stf.getStaff_id());
			invoice.setResponsible(stf);
			loadStaffMatnrList();
		} else {
			GeneralUtil.addErrorMessage("Ошибка!");
		}
		GeneralUtil.hideDialog("staffWidget");
	}

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void loadMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();
	private Map<Long, Staff> userStfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			userStfMap.put(u.getUser_id(), u.getStaff());
		}

		StaffDao sd = (StaffDao) appContext.getContext().getBean("staffDao");
		stfMap = sd.getMappedList("");
	}

	private String mode;

	public void initBean(String mode) {
		this.mode = mode;

		if (!GeneralUtil.isAjaxRequest()) {
			if (mode.equals("create")) {
				PermissionController.canWriteRedirect(userData, Invoice.TRANSACTION_ID_ACCOUNTABILITY_RETURN);
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_ACCOUNTABILITY_RETURN);
				invoice.setBranch_id(userData.getBranch_id());
				invoice.setBukrs(userData.getBukrs());
				invoice.setDepartment_id(6L);

			} else if (mode.equals("update")) {
				loadInvoice();
				loadInvoiceItems();
				loadStaffMatnrList();
			} else if (mode.equals("view")) {

				loadInvoice();
				loadInvoiceItems();
				if (invoice != null) {
					try {
						actionBean.initBean(invoice);
					} catch (Exception e) {

					}
				}
				loadStaffMatnrList();
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
			Staff stf = userStfMap.get(invoice.getCreated_by());
			if (stf == null) {
				invoice.setCreator(new Staff());
			} else {
				invoice.setCreator(stf);
			}
		} else {
			throw new DAOException("Invoice not found!");
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

			barcodesMap = new HashMap<Long, List<String>>();

			if (!Validation.isEmptyLong(invoice.getId())) {
				InvoiceItemDao d = (InvoiceItemDao) appContext.getContext().getBean("invoiceItemDao");
				invoiceItems = d.findAll("invoice_id = " + invoice.getId());
				for (InvoiceItem ii : invoiceItems) {
					ii.setMatnrObject(matnrMap.get(ii.getMatnr()));
					if (!Validation.isEmptyString(ii.getBarcode())) {
						if (barcodesMap.containsKey(ii.getMatnr())) {
							barcodesMap.get(ii.getMatnr()).add(ii.getBarcode());
						} else {
							List<String> t = new ArrayList<>();
							t.add(ii.getBarcode());
							barcodesMap.put(ii.getMatnr(), t);
						}
					}

					selectedMlMap.put(ii.getMatnr(), 1);
				}
			}
		}
	}

	public void deleteMatnrsRow(InvoiceItem ii) {
		if (!Validation.isEmptyString(ii.getBarcode())) {
			List<String> barcodes = new ArrayList<String>();
			for (Entry<Long, List<String>> e : barcodesMap.entrySet()) {
				if (e.getKey().equals(ii.getMatnr())) {
					barcodes = e.getValue();
					barcodes.remove(ii.getBarcode());
					e.setValue(barcodes);
					break;
				}
			}
		}
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

	public void Save() {
		try {
			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/invoice/accountability/return/View.xhtml?id=" + invoice.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");

		s.create(invoice, getPreparedInvoiceItems(), null, barcodesMap, userData.getUserid());
	}

	private List<InvoiceItem> getPreparedInvoiceItems() {
		List<InvoiceItem> out = new ArrayList<>();
		Map<Long, InvoiceItem> tempMap = new HashMap<>();
		for (InvoiceItem ii : invoiceItems) {
			if (Validation.isEmptyString(ii.getBarcode())) {
				out.add(ii);
			} else {
				Double q = 0D;
				if (tempMap.containsKey(ii.getMatnr())) {
					q = tempMap.get(ii.getMatnr()).getQuantity();
				}

				ii.setQuantity(q + 1);
				tempMap.put(ii.getMatnr(), ii);
			}
		}

		for (Entry<Long, InvoiceItem> e : tempMap.entrySet()) {
			out.add(e.getValue());
		}

		return out;
	}

	private void Update() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");
		s.update(invoice, getPreparedInvoiceItems(), null, barcodesMap, userData.getUserid());
	}

	private String pageHeader;

	private void preparePageHeader() {
		pageHeader = LogHelper.getPreparedPageHeader(invoice, mode);
	}

	public String getCompanyName() {
		if (invoice != null) {
			if (invoice.getBranch_id() == 13 || invoice.getBranch_id() == 37 || invoice.getBranch_id() == 38) {
				return "ОсОО \"NORDTEAM\" (НОРДТИМ)";
			}
		}

		return "ТОО «Aura Kazakhstan» (Аура Казахстан)";
	}

	/************************************/
	List<AccountMatnrStateOutputTable> amsOutputTable = new ArrayList<AccountMatnrStateOutputTable>();

	public List<AccountMatnrStateOutputTable> getAmsOutputTable() {
		return amsOutputTable;
	}

	public void setAmsOutputTable(List<AccountMatnrStateOutputTable> amsOutputTable) {
		this.amsOutputTable = amsOutputTable;
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

	private List<Werks> userWerks = new ArrayList<Werks>();

	private void loadUserWerks() {
		WerksBranchDao wbDao = (WerksBranchDao) appContext.getContext().getBean("werksBranchDao");
		userWerks = wbDao.findAllWerksByBranch2(userData.getBranch_id());
	}

	private Map<Long, Integer> selectedMlMap = new HashMap<Long, Integer>();

	List<MatnrList> mlListInWerks = new ArrayList<MatnrList>();

	public List<MatnrList> getMlListInWerks() {
		return mlListInWerks;
	}

	private Salary selectedSalary;

	public Salary getSelectedSalary() {
		return selectedSalary;
	}

	public void setSelectedSalary(Salary selectedSalary) {
		this.selectedSalary = selectedSalary;
	}

	private SalaryModel salaryModel;

	public SalaryModel getSalaryModel() {
		return salaryModel;
	}

	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	List<MatnrList> staffMatnrList = new ArrayList<>();

	public List<MatnrList> getStaffMatnrList() {
		return staffMatnrList;
	}

	private void loadStaffMatnrList() {
		staffMatnrList = new ArrayList<>();
		if (!Validation.isEmptyLong(invoice.getResponsible_id())) {
			MatnrListService mlService = appContext.getContext().getBean("matnrListService", MatnrListService.class);
			List<MatnrList> temp = mlService.findStaffMatnrList(invoice.getResponsible_id());
			Map<Long, MatnrList> tempMap = new HashMap<>();
			for (MatnrList ml : temp) {
				if (Validation.isEmptyString(ml.getBarcode())) {

					if (tempMap.containsKey(ml.getMatnr())) {
						ml.setMenge(tempMap.get(ml.getMatnr()).getMenge() + 1);
					}
					tempMap.put(ml.getMatnr(), ml);
				} else {
					staffMatnrList.add(ml);
				}
			}

			for (Entry<Long, MatnrList> e : tempMap.entrySet()) {
				staffMatnrList.add(e.getValue());
			}

			for (MatnrList ml : staffMatnrList) {
				ml.setMatnrCode(matnrMap.get(ml.getMatnr()).getCode());
				ml.setMatnrName(matnrMap.get(ml.getMatnr()).getName(userData.getU_language()));
			}
		}
	}

	public void onSelectMatnrList(SelectEvent e) {
		MatnrList ml = (MatnrList) e.getObject();
		if (Validation.isEmptyString(ml.getBarcode())) {
			if (!selectedMlMap.containsKey(ml.getMatnr())) {
				selectedMlMap.put(ml.getMatnr(), 1);
				InvoiceItem ii = new InvoiceItem();
				ii.setMatnr(ml.getMatnr());
				ii.setQuantity(ml.getMenge());
				ii.setMatnrObject(matnrMap.get(ml.getMatnr()));
				invoiceItems.add(ii);
			}
		} else {
			if (barcodesMap.containsKey(ml.getMatnr())) {
				boolean b = false;
				for (String s : barcodesMap.get(ml.getMatnr())) {
					if (s.equals(ml.getBarcode())) {
						b = true;
						break;
					}
				}

				if (!b) {
					barcodesMap.get(ml.getMatnr()).add(ml.getBarcode());
					InvoiceItem ii = new InvoiceItem();
					ii.setMatnr(ml.getMatnr());
					ii.setQuantity(ml.getMenge());
					ii.setBarcode(ml.getBarcode());
					ii.setMatnrObject(matnrMap.get(ml.getMatnr()));
					invoiceItems.add(ii);
				}
			} else {
				List<String> tempList = new ArrayList<>();
				tempList.add(ml.getBarcode());
				barcodesMap.put(ml.getMatnr(), tempList);
				InvoiceItem ii = new InvoiceItem();
				ii.setMatnr(ml.getMatnr());
				ii.setQuantity(ml.getMenge());
				ii.setBarcode(ml.getBarcode());
				ii.setMatnrObject(matnrMap.get(ml.getMatnr()));
				invoiceItems.add(ii);
			}
		}
		GeneralUtil.hideDialog("matnrListDlg");
	}

	public void assignSalary() {
		invoiceItems = new ArrayList<>();
		invoice.setResponsible(null);
		invoice.setResponsible_id(0L);
		loadStaffMatnrList();

		if (selectedSalary != null && selectedSalary.getP_staff() != null) {
			invoice.setResponsible(selectedSalary.getP_staff());
			invoice.setResponsible_id(selectedSalary.getStaff_id());
			loadStaffMatnrList();
		}
	}
}