package reports.logistics;

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

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.InvoiceDao;
import general.dao.MatnrDao;
import general.dao.StaffDao;
import general.tables.Invoice;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.search.InvoiceSearch;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repWrittenOffMaterialsBean")
@ViewScoped
public class RepWrittenOffMaterials {

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			prepareMaps();
			searchModel.setStatus_id(Invoice.STATUS_DONE);
		}
	}

	Map<Long, Staff> stfMap = new HashMap<>();
	Map<Long, Matnr> matnrMap = new HashMap<>();

	private void prepareMaps() {
		StaffDao stfDao = appContext.getContext().getBean("staffDao", StaffDao.class);
		stfMap = stfDao.getMappedList("");

		MatnrDao mDao = appContext.getContext().getBean("matnrDao", MatnrDao.class);
		matnrMap = mDao.getMappedList("");
	}

	private InvoiceSearch searchModel = new InvoiceSearch();

	public InvoiceSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(InvoiceSearch searchModel) {
		this.searchModel = searchModel;
	}

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	List<Invoice> items = new ArrayList<>();

	public void loadItems() {
		InvoiceDao inDao = appContext.getContext().getBean("invoiceDao", InvoiceDao.class);
		String cond = searchModel.getCondition();
		cond += (cond.length() > 0 ? " AND " : " ")
				+ String.format(" type_id = %d AND status_id = %d", Invoice.TYPE_WRITEOFF, Invoice.STATUS_DONE);
		Long searchMatnr = null;
		if (!Validation.isEmptyString(code)) {
			for (Entry<Long, Matnr> e : matnrMap.entrySet()) {
				if (e.getValue().getCode().toLowerCase().equals(code.toLowerCase())) {
					searchMatnr = e.getKey();
					break;
				}
			}
		}

		if (searchMatnr != null) {
			cond += " AND id IN(SELECT invoice_id FROM InvoiceItem WHERE matnr = " + searchMatnr + ") ";
		}
		
		if(selectedStaff != null){
			cond += " AND responsible_id = " + selectedStaff.getStaff_id();
		}
		items = inDao.findAll(cond);
		for (Invoice i : items) {
			i.setResponsible(stfMap.get(i.getResponsible_id()));
		}
	}

	public List<Invoice> getItems() {
		return items;
	}

	private String pageHeader = "Отчет о списанных материалов";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	private StaffSearch staffSearchModel = new StaffSearch();

	public StaffSearch getStaffSearchModel() {
		return staffSearchModel;
	}

	public void setStaffSearchModel(StaffSearch staffSearchModel) {
		this.staffSearchModel = staffSearchModel;
	}

	public void loadStaffList() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		staffList = d.findAll(staffSearchModel.getCondition());
		// System.out.println(staffSearchModel.getCondition());
	}

	private List<Staff> staffList;

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	public void removeSelectedStaff() {
		selectedStaff = null;
	}

	public void onSelectStaff(SelectEvent e) {
		selectedStaff = (Staff) e.getObject();
		// System.out.println("SEL: " + selectedStaff.getStaff_id());
		GeneralUtil.hideDialog("StaffListDialog");
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
