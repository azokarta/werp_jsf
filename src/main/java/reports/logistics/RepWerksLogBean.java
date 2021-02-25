package reports.logistics;

import f4.WerksF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.MatnrDao;
import general.dao.StaffDao;
import general.services.InvoiceService;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.Staff;
import general.tables.Werks;
import general.tables.search.StaffSearch;

import java.io.Serializable;
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

import user.User;

@ManagedBean(name = "repWerksLogBean")
@ViewScoped
public class RepWerksLogBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3324159776514256256L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			if (!userData.isMain() && !userData.isSys_admin()) {
				bukrs = userData.getBukrs();
				System.out.println("TEST");
			}
			loadMatnrMap();
			loadStaffMap();
			werksMap = werksF4.getL_werks_map();
			prepareUserBranches();
		}
	}

	private Map<Long, Werks> werksMap = new HashMap<Long, Werks>();

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		stfMap = d.getMappedList("");
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		BranchDao d = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = d.findChilds(userData.getBranch_id());
	}

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	private String bukrs;
	private Long werks;
	private Date fromDate;
	private Date toDate = null;
	private String code;
	private Double inTotal = 0D;
	private Double outTotal = 0D;
	private Integer typeId = 0;
	private Long staffId = 0L;

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Double getInTotal() {
		return inTotal;
	}

	public void setInTotal(Double inTotal) {
		this.inTotal = inTotal;
	}

	public Double getOutTotal() {
		return outTotal;
	}

	public void setOutTotal(Double outTotal) {
		this.outTotal = outTotal;
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

	public Map<Long, Matnr> getMatnrMap() {
		return matnrMap;
	}

	public void setMatnrMap(Map<Long, Matnr> matnrMap) {
		this.matnrMap = matnrMap;
	}

	private Map<Long, Matnr> matnrMap;

	private void loadMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	public void generateList() {
		try {
			if (Validation.isEmptyString(getBukrs())) {
				throw new DAOException("Выберите компанию");
			}

			if (Validation.isEmptyLong(getWerks())) {
				throw new DAOException("Выберите склад");
			}

			if (fromDate == null) {
				throw new DAOException("Выберите дату начало");
			}
			outputList = new ArrayList<RepWerksLogBean.OutputTable>();

			MatnrDao md = appContext.getContext().getBean("matnrDao", MatnrDao.class);
			InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");
			Double balance = 0D;
			boolean countBalance = false;
			if (!Validation.isEmptyString(code)) {
				Matnr selectedMatnr = md.findByCode(code);
				balance = s.getWerksBalance(werks, selectedMatnr.getMatnr(), fromDate);
				OutputTable ot = new OutputTable();
				ot.setBalance(balance);
				outputList.add(ot);
				countBalance = true;
			}
			Map<Date, Map<Long, List<InvoiceItem>>> m = s.getGeneratedWerksLogData(werks, fromDate, toDate, typeId,
					staffId);

			inTotal = outTotal = 0D;

			for (Entry<Date, Map<Long, List<InvoiceItem>>> e : m.entrySet()) {
				Date d = e.getKey();
				for (Entry<Long, List<InvoiceItem>> e1 : e.getValue().entrySet()) {
					// Invoice currentInvoice = eq
					for (InvoiceItem ii : e1.getValue()) {

						Matnr currMatnr = matnrMap.get(ii.getMatnr());
						currMatnr = (currMatnr == null) ? new Matnr() : currMatnr;
						if (!Validation.isEmptyString(getCode())
								&& !currMatnr.getCode().toLowerCase().equals(getCode().toLowerCase())) {
							continue;
						}
						Invoice invoice = ii.getInvoiceObject();
						OutputTable ot = new OutputTable();
						ot.setBalance(0D);
						ot.setBarcode(ii.getBarcode());
						ot.setDate(d);
						ot.setNote(invoice.getNote());
						String fromWerks = werksMap.get(invoice.getFrom_werks()) == null ? ""
								: werksMap.get(invoice.getFrom_werks()).getText45();
						// fromWerks += " => " + invoice.getType_id();
						String toWerks = werksMap.get(invoice.getTo_werks()) == null ? ""
								: werksMap.get(invoice.getTo_werks()).getText45();
						String resp = stfMap.get(invoice.getResponsible_id()) == null ? ""
								: stfMap.get(invoice.getResponsible_id()).getLF();

						if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
							ot.setOutQuantity(ii.getQuantity());
							ot.setReceiver(resp);
							ot.setSender(fromWerks);
							ot.setNote("ПОДОТЧЕТ ");
							balance -= ii.getQuantity();
						} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
							ot.setInQuantity(ii.getQuantity());
							ot.setReceiver(toWerks);
							ot.setSender(resp);
							ot.setNote("ВОЗВРАТ С ПОДОТЧЕТА");
							balance += ii.getQuantity();

						} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING)) {
							ot.setReceiver(toWerks);
							ot.setInQuantity(ii.getQuantity());
							ot.setNote("ОПРИХОДОВАНИЯ ");
							balance += ii.getQuantity();
						} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING_IN)) {
							ot.setSender(fromWerks);
							ot.setReceiver(toWerks);
							ot.setNote("ВНУТРЕННАЯ ОПР ");
							ot.setInQuantity(ii.getQuantity());
							balance += ii.getQuantity();
						} else if (invoice.getType_id().equals(Invoice.TYPE_SEND)) {
							ot.setOutQuantity(ii.getQuantity());
							ot.setSender(fromWerks);
							ot.setReceiver(toWerks);
							ot.setNote("ОТПРАВКА НА ДРУГОЙ СКЛАД ");
							balance -= ii.getQuantity();
						} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
							ot.setSender(fromWerks);
							ot.setOutQuantity(ii.getQuantity());
							ot.setNote("СПИСАНИЕ ");
							if (!Validation.isEmptyLong(invoice.getContract_number())) {
								ot.setNote(ot.getNote() + ", ДОГОВОР №" + invoice.getContract_number());
							} else if (!Validation.isEmptyLong(invoice.getService_number())) {
								ot.setNote(ot.getNote() + ", СЕРВИС №" + invoice.getService_number());
							}
							if (Validation.isEmptyLong(invoice.getResponsible_id())) {
								balance -= ii.getQuantity();
							}
						} else if (invoice.getType_id().equals(Invoice.TYPE_RETURN)) {
							ot.setReceiver(toWerks);
							ot.setInQuantity(ii.getQuantity());
							ot.setNote("ВОЗВРАТ ");
							if (!Validation.isEmptyLong(invoice.getContract_number())) {
								ot.setNote(ot.getNote() + ", ДОГОВОР №" + invoice.getContract_number());
							} else if (!Validation.isEmptyLong(invoice.getService_number())) {
								ot.setNote(ot.getNote() + ", СЕРВИС №" + invoice.getService_number());
							}
							balance += ii.getQuantity();
						} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_LOSS)) {
							ot.setSender(fromWerks);
							ot.setOutQuantity(ii.getQuantity());
							ot.setNote("СПИСАНИЕ ПО ПОТЕРЕ ");
							balance -= ii.getQuantity();
						}
						ot.setDocumentLink(invoice.getDocumentViewLink());
						ot.setMatnrObject(currMatnr);
						if (Validation.isEmptyString(ot.getNote())) {
							ot.setNote(invoice.getNote());
						} else {
							ot.setNote(ot.getNote()
									+ (Validation.isEmptyString(invoice.getNote()) ? "" : invoice.getNote()));
						}

						if (countBalance) {
							ot.setBalance(balance);
						}

						outputList.add(ot);

					}
				}
			}

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private List<OutputTable> outputList = new ArrayList<RepWerksLogBean.OutputTable>();

	public List<OutputTable> getOutputList() {
		return outputList;
	}

	public void setOutputList(List<OutputTable> outputList) {
		this.outputList = outputList;
	}

	public class OutputTable {
		private Date date;
		private Matnr matnrObject;
		private Double inQuantity;
		private Double outQuantity;
		private String barcode;
		private String sender;
		private String receiver;
		private String note;
		private Double balance;
		private String documentLink;

		public String getDocumentLink() {
			return documentLink;
		}

		public void setDocumentLink(String documentLink) {
			this.documentLink = documentLink;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public Matnr getMatnrObject() {
			return matnrObject;
		}

		public void setMatnrObject(Matnr matnrObject) {
			this.matnrObject = matnrObject;
		}

		public Double getInQuantity() {
			return inQuantity;
		}

		public void setInQuantity(Double inQuantity) {
			inTotal += inQuantity;
			this.inQuantity = inQuantity;
		}

		public Double getOutQuantity() {
			return outQuantity;
		}

		public void setOutQuantity(Double outQuantity) {
			outTotal += outQuantity;
			this.outQuantity = outQuantity;
		}

		public String getBarcode() {
			return barcode;
		}

		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public String getReceiver() {
			return receiver;
		}

		public void setReceiver(String receiver) {
			this.receiver = receiver;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

		public Double getBalance() {
			return balance;
		}

		public void setBalance(Double balance) {
			this.balance = balance;
		}

		public OutputTable() {
			super();
			setInQuantity(0D);
			setOutQuantity(0D);
			setBalance(0D);
		}

	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public void removeSelectedStaff() {
		selectedStaff = null;
		staffId = 0L;
	}

	private StaffSearch staffSearchModel = new StaffSearch();

	public StaffSearch getStaffSearchModel() {
		return staffSearchModel;
	}

	public void setStaffSearchModel(StaffSearch staffSearchModel) {
		this.staffSearchModel = staffSearchModel;
	}

	private List<Staff> staffList = new ArrayList<>();

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

	@ManagedProperty(value = "#{werksF4Bean}")
	WerksF4 werksF4;

	public WerksF4 getWerksF4() {
		return werksF4;
	}

	public void setWerksF4(WerksF4 werksF4) {
		this.werksF4 = werksF4;
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
