package reports.logistics;

import java.util.ArrayList;
import java.util.Date;
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
import general.Helper;
import general.Validation;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.StaffDao;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.Staff;
import general.tables.search.InvoiceSearch;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repAccountabilityMaterialsBean")
@ViewScoped
public class RepAccountabilityMaterials {

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

	private Date fromDate;
	private Date toDate;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	List<InvoiceItem> items = new ArrayList<>();

	public void loadItems() {
		InvoiceItemDao iiDao = appContext.getContext().getBean("invoiceItemDao", InvoiceItemDao.class);
		String cond = searchModel.getCondition();
		cond += (cond.length() > 0 ? " AND " : " ")
				+ String.format(" type_id = %d AND status_id = %d", Invoice.TYPE_ACCOUNTABILITY, Invoice.STATUS_DONE);
		Long searchMatnr = null;
		if (!Validation.isEmptyString(code)) {
			for (Entry<Long, Matnr> e : matnrMap.entrySet()) {
				if (e.getValue().getCode().toLowerCase().equals(code.toLowerCase())) {
					searchMatnr = e.getKey();
					break;
				}
			}
		}

		if (selectedStaff != null) {
			cond += " AND responsible_id = " + selectedStaff.getStaff_id();
		}

		if (fromDate != null) {
			cond += " AND invoice_date >= '" + Helper.getFormattedDateForDb(fromDate) + "' ";
		}

		if (toDate != null) {
			cond += " AND invoice_date <= '" + Helper.getFormattedDateForDb(toDate) + "' ";
		}

		String subQuery = " SELECT id FROM Invoice i WHERE " + cond;

		String cond2 = " invoice_id IN( " + subQuery + ") ";

		if (searchMatnr != null) {
			cond2 += " AND matnr = " + searchMatnr + ") ";
		}

		items = new ArrayList<>();
		List<InvoiceItem> tempList = iiDao.findAll(cond2);
		Map<Long, InvoiceItem> iiMap = new HashMap<>();
		for (InvoiceItem ii : tempList) {
			ii.setMatnrObject(matnrMap.get(ii.getMatnr()));
			if (Validation.isEmptyString(ii.getBarcode())) {
				Double q = 0D;
				if (iiMap.containsKey(ii.getMatnr())) {
					q = iiMap.get(ii.getMatnr()).getQuantity();
				}

				q++;
				ii.setQuantity(q);
				iiMap.put(ii.getMatnr(), ii);
			} else {
				items.add(ii);
			}
		}

		for (Entry<Long, InvoiceItem> e : iiMap.entrySet()) {
			items.add(e.getValue());
		}

	}
	
	public int getItemsCount(){
		return items.size();
	}

	public List<InvoiceItem> getItems() {
		return items;
	}

	private String pageHeader = "Отчет о материалов в подотчете";

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
