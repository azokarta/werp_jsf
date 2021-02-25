package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.beans.WerksBean;
import general.dao.AccountMatnrStateDao;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.SubCompanyDao;
import general.dao.UserDao;
import general.dao.WerksBranchDao;
import general.services.InvoiceService;
import general.tables.AccountMatnrState;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.SubCompany;
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
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.SelectEvent;

import datamodels.MatnrModel;
import datamodels.SalaryModel;
import f4.WerksF4;
import user.User;

@ManagedBean(name = "logAccountabilityCrudBean")
@ViewScoped
public class AccountabilityCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2925575164534855423L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, Invoice.TRANSACTION_ID_ACCOUNTABILITY);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			loadStaffMap();
			loadMatnrMap();
			prepareMatnrModel();
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
	private LogActBean logActBean;

	public void initBean(String mode) {
		this.mode = mode;

		if (!GeneralUtil.isAjaxRequest()) {
			if (mode.equals("create")) {
				PermissionController.canWriteRedirect(userData, Invoice.TRANSACTION_ID_ACCOUNTABILITY);
				invoice = new Invoice();
				invoice.setType_id(Invoice.TYPE_ACCOUNTABILITY);
				invoice.setBranch_id(userData.getBranch_id());
				invoice.setBukrs(userData.getBukrs());
				invoice.setDepartment_id(6L);
				prepareUserWerks();
				if (userWerks.size() == 1) {
					invoice.setFrom_werks(userWerks.get(0).getWerks());
					loadMlListInWerks(userWerks.get(0).getWerks());
				}
				prepareSalaryModel();
				onBranchChanged();
			} else if (mode.equals("update")) {
				loadInvoice();
				// invoice.setBranch_id(userData.getBranch_id());
				loadInvoiceItems();
				loadMlListInWerks(invoice.getFrom_werks());
				prepareUserWerks();
				prepareSalaryModel();
				onBranchChanged();
			} else if (mode.equals("view")) {

				loadInvoice();
				loadInvoiceItems();
				loadAmsList();

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
	
	public void onBranchChanged() {
		if (invoice != null && !Validation.isEmptyLong(invoice.getBranch_id())) {
			// System.out.println("Invoice Branch: " + invoice.getBranch_id());
			salaryModel.getSearchModel().setBranch_id(invoice.getBranch_id());
		}
	}

	List<AccountMatnrState> amsList = new ArrayList<AccountMatnrState>();
	Map<String, List<AccountMatnrState>> amsListMap = new HashMap<String, List<AccountMatnrState>>();

	// Для проставки состояния аппаратов
	private void loadAmsList() {
		AccountMatnrStateDao d = (AccountMatnrStateDao) appContext.getContext().getBean("accountMatnrStateDao");
		amsList = d.findAll("invoice_id = " + invoice.getId());
		Map<String, List<AccountMatnrState>> tempAmsMap = new HashMap<String, List<AccountMatnrState>>();
		for (AccountMatnrState ams : amsList) {
			ams.setMatnrObject(matnrMap.get(ams.getMatnr()));
			List<AccountMatnrState> temp = new ArrayList<AccountMatnrState>();
			if (tempAmsMap.containsKey(ams.getBarcode())) {
				temp = tempAmsMap.get(ams.getBarcode());
			}
			temp.add(ams);
			tempAmsMap.put(ams.getBarcode(), temp);
		}
		// System.out.println("SIZE: " + invoiceItems.size() + " "
		// + barcodesMap.size());
		for (InvoiceItem ii : invoiceItems) {
			if (ii.getMatnrObject().getType() == 1) {

				for (String b : barcodesMap.get(ii.getMatnr())) {
					AccountMatnrStateOutputTable outputTable = new AccountMatnrStateOutputTable();
					outputTable.setItems(tempAmsMap.get(b));
					outputTable.setMatnr(ii.getMatnr());
					outputTable.setMatnrObject(matnrMap.get(ii.getMatnr()));
					outputTable.setBarcode(b);
					amsOutputTable.add(outputTable);
				}
			}
		}

		prepareSpareParts();
	}

	List<AccountMatnrStateOutputTable> amsOutputTable = new ArrayList<AccountMatnrStateOutputTable>();

	public List<AccountMatnrStateOutputTable> getAmsOutputTable() {
		return amsOutputTable;
	}

	public void addAmsRow(AccountMatnrStateOutputTable oTb) {
		AccountMatnrState ams = new AccountMatnrState();
		ams.setBarcode(oTb.getBarcode());
		ams.setMlsMatnrId(oTb.getMatnr());
		ams.setInvoiceId(invoice.getId());
		// System.out.println(oTb.getItems());
		if (oTb.getItems() == null) {
			List<AccountMatnrState> l = new ArrayList<AccountMatnrState>();
			l.add(ams);
			oTb.setItems(l);
		} else {
			oTb.getItems().add(ams);
		}
	}

	private List<AccountMatnrState> getPreparedAmsList() {
		List<AccountMatnrState> out = new ArrayList<AccountMatnrState>();
		for (AccountMatnrStateOutputTable amsTable : amsOutputTable) {
			if (amsTable.getItems() != null) {
				out.addAll(amsTable.getItems());
			}
		}

		return out;
	}

	public void setAmsOutputTable(List<AccountMatnrStateOutputTable> amsOutputTable) {
		this.amsOutputTable = amsOutputTable;
	}

	List<Matnr> spareParts = new ArrayList<Matnr>();

	public void onSelectSparePart(SelectEvent e) {
		Matnr part = (Matnr) e.getObject();
		if (part != null && currentAmsRow != null) {
			currentAmsRow.setMatnr(part.getMatnr());
			currentAmsRow.setCode(part.getCode());
			currentAmsRow.setMatnrObject(part);

			// GeneralUtil.updateFormElement(":amsDlg:amsDataTable");
		}
	}

	public void saveAms() {
		try {
			InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");
			s.saveAms(invoice, getPreparedAmsList(), userData);
			GeneralUtil.addSuccessMessage("Состояние материала сохранено успешно");
			GeneralUtil.hideDialog("amsDlg");
			GeneralUtil.updateFormElement(":form");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<Matnr> getSpareParts() {
		return spareParts;
	}

	private void prepareSpareParts() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		spareParts = d.findAll(" type = 2 ");
	}

	AccountMatnrState currentAmsRow;

	public void setCurrentAmsRow(AccountMatnrState currAms) {
		this.currentAmsRow = currAms;
	}

	public String getMode() {
		return mode;
	}

	private void loadInvoice() {
		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean("invoiceDao");
		SubCompanyDao subCompanyDao = (SubCompanyDao) appContext.getContext().getBean("subCompanyDao");
		StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
		invoice = d.find(id);
		if (invoice != null) {
			Staff stf = stfMap.get(invoice.getCreated_by());
			if (stf == null) {
				invoice.setCreator(new Staff());
			} else {
				invoice.setCreator(stf);
			}
			
			SubCompany subCompany = null;
			Staff companyDir = null;
			
			if(invoice.getResponsible() != null && !Validation.isEmptyLong(invoice.getResponsible().getSubCompanyId())){
				subCompany = subCompanyDao.find(invoice.getResponsible().getSubCompanyId());
				if(subCompany != null && !Validation.isEmptyLong(subCompany.getDirectorId())){
					companyDir = staffDao.find(subCompany.getDirectorId());
				}
			}
			
			logActBean = new LogActBean(subCompany, companyDir, invoice.getResponsible());
			
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

			GeneralUtil.doRedirect("/logistics/invoice/accountability/View.xhtml?id=" + invoice.getId());

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

	public void bukrsChanged() {
		userWerks = werksF4Bean.getByBukrs(invoice.getBukrs());
	}

	private void prepareSalaryModel() {
		salaryModel = new SalaryModel((SalaryDao) appContext.getContext().getBean("salaryDao"));
		if (!userData.isSys_admin() && !userData.isMain()) {

			salaryModel.getSearchModel().setBukrs(userData.getBukrs());
			salaryModel.getSearchModel().setUserBranches(getUserWerksBranchesList());
		} else {
			// userWerks = werksBean.getUserWerks(userData.getBukrs());
		}
	}

	private List<Branch> werksBranchList = new ArrayList<>();

	public List<Branch> getWerksBranchList() {
		return werksBranchList;
	}

	public void setWerksBranchList(List<Branch> werksBranchList) {
		this.werksBranchList = werksBranchList;
	}

	private List<Werks> userWerks = new ArrayList<>();

	public List<Werks> getUserWerks() {
		return userWerks;
	}

	public void setUserWerks(List<Werks> userWerks) {
		this.userWerks = userWerks;
	}

	private void prepareUserWerks() {
		if (userData.isSys_admin() || userData.isMain()) {
			userWerks = werksF4Bean.getByBukrs(userData.getBukrs());
		} else {
			userWerks = werksBean.getUserWerks(userData.getBukrs());
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

	private List<Branch> getUserWerksBranchesList() {
		List<Branch> out = new ArrayList<>();
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		WerksBranchDao wbDao = (WerksBranchDao) appContext.getContext().getBean("werksBranchDao");
		for (Werks w : userWerks) {
			for (Branch br : wbDao.findAllBranchesByWerks(w.getWerks())) {
				if (Branch.TYPE_CITY == br.getType().intValue()) {
					out.addAll(brDao.findChilds(br.getBranch_id()));
				} else if (Branch.TYPE_BRANCH == br.getType().intValue()) {
					out.add(br);
				}
			}
		}

		return out;
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

	private boolean showResponsible = true;

	public boolean isShowResponsible() {
		return showResponsible;
	}

	public void setShowResponsible(boolean showResponsible) {
		this.showResponsible = showResponsible;
	}

	private boolean emptyInvoiceNote = true;

	public boolean isEmptyInvoiceNote() {
		if (invoice != null && invoice.getNote() != null && !invoice.getNote().isEmpty())
			return false;
		else
			return true;
	}

	public void setEmptyInvoiceNote(boolean emptyInvoiceNote) {
		this.emptyInvoiceNote = emptyInvoiceNote;
	}

	public String getActText1(Boolean showResponsible){
		if(logActBean == null){
			return "";
		}
		
		return logActBean.getTextPart1("ru", showResponsible);
	}
	
	public String getActText2(){
		if(logActBean == null){
			return "";
		}
		
		return logActBean.getTextPart2("ru");
	}
	
	public String getActText3(){
		if(logActBean == null){
			return "";
		}
		
		return logActBean.getTextPart3("ru");
	}

	public String getCompanyName() {
		if (invoice != null) {
			if (invoice.getBranch_id() == 13 || invoice.getBranch_id() == 37 || invoice.getBranch_id() == 38) {
				return "ОсОО \"NORDTEAM\" (НОРДТИМ)";
			}
		}

		return "ТОО «Aura Kazakhstan» (Аура Казахстан)";
	}

	public void fromWerksChangeListener(AjaxBehaviorEvent e) {
		invoiceItems = new ArrayList<InvoiceItem>();
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
	}

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