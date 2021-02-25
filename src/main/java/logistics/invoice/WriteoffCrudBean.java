package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.RelatedDocsDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.services.InvoiceService;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.RelatedDocs;
import general.tables.Salary;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "logWriteoffCrudBean")
@ViewScoped
public class WriteoffCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3134616993886961657L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				parentId = new Long(
						GeneralUtil.getRequestParameter("parent_id"));
			} catch (NumberFormatException e) {
				parentId = 0L;
			}

			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
			prepareMatnrMap();
			prepareSalaryModel();
			loadStaffMap();
		}
	}

	private Map<Long, Staff> userStfMap = new HashMap<Long, Staff>();
	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		StaffDao sDao = (StaffDao) appContext.getContext().getBean("staffDao");
		stfMap = sDao.getMappedList("");
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			userStfMap.put(u.getUser_id(), u.getStaff());
		}
	}

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void prepareMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	private String pageHeader;
	private Long id;
	private Long parentId;
	private String mode;
	private Invoice parentInvoice;
	private List<InvoiceItem> invoiceItems;

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public String getPageHeader() {
		MessageProvider mp = new MessageProvider();
		if (invoice != null) {
			if (mode.equals("update")) {
				return mp.getValue("logistics.invoice_for_writeoff") + " №"+ invoice.getId() + " / " + mp.getValue("logistics.editing");
			} else if (mode.equals("view")) {
				return mp.getValue("logistics.invoice_for_writeoff") + " №" + invoice.getId() + " / " + mp.getValue("view");
			}
		}

		if (mode.equals("create")) {
			return mp.getValue("logistics.invoice_for_writeoff") + " / " + mp.getValue("logistics.creation");
		}

		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public void setMode(String mode) {
		if (!GeneralUtil.isAjaxRequest()) {
			this.mode = mode;
			if (mode.equals("create")) {
				try {
					loadParentInvoice();
					prepareParentInvoiceItems();
					invoice = new Invoice();
					invoice.setType_id(Invoice.TYPE_WRITEOFF);
					invoice.setBranch_id(parentInvoice.getBranch_id());
					invoice.setBukrs(parentInvoice.getBukrs());
					invoice.setCustomer_id(parentInvoice.getCustomer_id());
					invoice.setAwkey(parentInvoice.getAwkey());
					invoice.setDepartment_id(6L);
					invoice.setContract_number(parentInvoice
							.getContract_number());
					invoice.setService_number(parentInvoice.getService_number());
					invoice.setResponsible_id(parentInvoice.getResponsible_id());
					if (!Validation.isEmptyLong(invoice.getResponsible_id())) {
						invoice.setResponsible(stfMap.get(invoice
								.getResponsible_id()));
					}

					if (!userData.isSys_admin() && !userData.isMain()) {
						//invoice.setBukrs(userData.getBukrs());
						//invoice.setBranch_id(userData.getBranch_id());
					}

					salaryModel.getSearchModel().setBukrs(invoice.getBukrs());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (mode.equals("update")) {

				try {
					loadInvoice();
					loadParentInvoice();
					prepareInvoiceItems();
					salaryModel.getSearchModel().setBukrs(invoice.getBukrs());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (mode.equals("view")) {
				try {
					loadInvoice();
					loadParentInvoice();
					prepareInvoiceItems();
					if (invoice != null) {
						try {
							actionBean.initBean(invoice);
						} catch (Exception e) {

						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String getMode() {
		return mode;
	}

	public Invoice getParentInvoice() {
		return parentInvoice;
	}

	private void loadParentInvoice() throws Exception {
		if (Validation.isEmptyLong(parentId)) {
			throw new Exception("Parent Invoice Not Found!");
		}

		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean(
				"invoiceDao");
		parentInvoice = d.find(parentId);
		if (parentInvoice == null) {
			throw new Exception("Parent Invoice Not Found!");
		}
	}

	private Map<Long, List<String>> barcodesMap = new HashMap<Long, List<String>>();

	private void prepareParentInvoiceItems() {
		InvoiceItemDao d = (InvoiceItemDao) appContext.getContext().getBean(
				"invoiceItemDao");
		invoiceItems = new ArrayList<InvoiceItem>();
		List<InvoiceItem> temp = d.findAll("invoice_id = "
				+ parentInvoice.getId());
		LogUtil.prepareInvoiceItems(temp, invoiceItems, matnrMap, barcodesMap);
	}

	private void prepareInvoiceItems() {
		InvoiceItemDao d = (InvoiceItemDao) appContext.getContext().getBean(
				"invoiceItemDao");
		invoiceItems = new ArrayList<InvoiceItem>();
		List<InvoiceItem> temp = d.findAll("invoice_id = " + invoice.getId());
		LogUtil.prepareInvoiceItems(temp, invoiceItems, matnrMap, barcodesMap);
	}

	private void loadInvoice() {
		InvoiceDao d = (InvoiceDao) appContext.getContext().getBean(
				"invoiceDao");
		invoice = d.find(id);
		if (invoice != null) {
			RelatedDocsDao rdd = (RelatedDocsDao) appContext.getContext()
					.getBean("relatedDocsDao");
			RelatedDocs rd = rdd.findParent(invoice.getId(), Invoice.CONTEXT,
					Invoice.CONTEXT);
			if (rd != null) {
				parentId = rd.getContext_id();
			}

			if (invoice != null) {
				Staff stf = userStfMap.get(invoice.getCreated_by());
				if (stf == null) {
					invoice.setCreator(new Staff());
				} else {
					invoice.setCreator(stf);
				}
			}
		}
	}

	private Invoice invoice;

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public void deleteItemsRow(InvoiceItem i) {
		invoiceItems.remove(i);
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

	public void removeResponsible() {
		if (invoice != null) {
			invoice.setResponsible(null);
			invoice.setResponsible_id(0L);
		}
	}

	private SalaryModel salaryModel;

	public SalaryModel getSalaryModel() {
		return salaryModel;
	}

	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	private void prepareSalaryModel() {
		salaryModel = new SalaryModel((SalaryDao) appContext.getContext()
				.getBean("salaryDao"));
	}

	public void Save() {
		try {
			if (mode.equals("create")) {
				Create();
			} else if (mode.equals("update")) {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/invoice/writeoff/View.xhtml?id="
					+ invoice.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean(
				"invoiceService");
		// System.out.println("TEMPPP");
		s.create(invoice, invoiceItems, getParentDocsMap(), barcodesMap,
				userData.getUserid());
	}

	private Map<String, List<Long>> getParentDocsMap() {

		Map<String, List<Long>> m = new HashMap<String, List<Long>>();
		List<Long> l = new ArrayList<Long>();
		if (parentInvoice != null
				&& !Validation.isEmptyLong(parentInvoice.getId())) {
			l.add(parentInvoice.getId());
			m.put(Invoice.CONTEXT, l);
		}
		return m;
	}

	private void Update() {
		InvoiceService s = (InvoiceService) appContext.getContext().getBean(
				"invoiceService");

		s.update(invoice, invoiceItems, getParentDocsMap(), barcodesMap,
				userData.getUserid());
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
}
