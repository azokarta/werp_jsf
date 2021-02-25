package reports.logistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.StaffDao;
import general.services.reports.LogisticsReportService;
import general.tables.Invoice;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.report.LogReport3;
import general.tables.search.StaffSearch;
import user.User;

@ManagedBean(name = "repLog3Bean")
@ViewScoped
public class RepLog3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3149550413328920473L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {

		}
	}

	private String pageHeader = "Движение подотчетных материалов";
	private String bukrs;
	private Long werks;
	private Long staffId;
	private String code;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getWerks() {
		return werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private List<LogReport3> items;

	public List<LogReport3> getItems() {
		return items;
	}

	public void setItems(List<LogReport3> items) {
		this.items = items;
	}

	Double accTotal = 0D;
	Double returnAccTotal = 0D;
	Double wrOffTotal = 0D;
	Double wrOffDocTotal = 0D;
	Double resultBalance = 0D;

	public Double getAccTotal() {
		return accTotal;
	}

	public void setAccTotal(Double accTotal) {
		this.accTotal = accTotal;
	}

	public Double getReturnAccTotal() {
		return returnAccTotal;
	}

	public void setReturnAccTotal(Double returnAccTotal) {
		this.returnAccTotal = returnAccTotal;
	}

	public Double getWrOffTotal() {
		return wrOffTotal;
	}

	public void setWrOffTotal(Double wrOffTotal) {
		this.wrOffTotal = wrOffTotal;
	}

	public Double getWrOffDocTotal() {
		return wrOffDocTotal;
	}

	public void setWrOffDocTotal(Double wrOffDocTotal) {
		this.wrOffDocTotal = wrOffDocTotal;
	}

	public Double getBalanceInPeriod() {
		Double out = accTotal - (wrOffDocTotal + returnAccTotal);
		return out >= 0 ? out : 0D;
	}

	private Double totalBalance = 0D;

	public Double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}

	private void loadTotalBalance() {
		totalBalance = 0D;
		MatnrDao md = appContext.getContext().getBean("matnrDao", MatnrDao.class);
		Matnr m = md.findByCode(getCode());
		MatnrListDao mld = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
		List<MatnrList> tempList = mld
				.findAllAccountability(" staff_id = " + getStaffId() + " AND matnr = " + m.getMatnr());
		if (tempList.size() > 0) {
			totalBalance = tempList.get(0).getMenge();
		}
	}

	public void generateData() {
		try {
			items = new ArrayList<>();
			accTotal = returnAccTotal = wrOffDocTotal = wrOffTotal = 0D;
			LogisticsReportService service = appContext.getContext().getBean("logisticsReportService",
					LogisticsReportService.class);
			items = service.getRep3Data(werks, staffId, fromDate, toDate, code);
			for (LogReport3 lr : items) {
				accTotal += lr.getAccountQuantity();
				returnAccTotal += lr.getReturnFromAccountQuantity();
				wrOffTotal += lr.getWrOffQuantity();
				wrOffDocTotal += lr.getWrOffDocQuantity();
				resultBalance = lr.getBalance();
			}
			loadTotalBalance();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public Double getResultBalance() {
		return resultBalance;
	}

	public void setResultBalance(Double resultBalance) {
		this.resultBalance = resultBalance;
	}

	private List<Staff> staffList = new ArrayList<>();
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

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	public void onSelectStaff(SelectEvent e) {
		selectedStaff = (Staff) e.getObject();
		staffId = selectedStaff.getStaff_id();
		// System.out.println("SEL: " + selectedStaff.getStaff_id());
		GeneralUtil.hideDialog("StaffListDialog");
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
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
