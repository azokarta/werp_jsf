package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.MatnrSparePartDao;
import general.dao.SalaryDao;
import general.dao.WriteoffRepairDao;
import general.services.WriteoffRepairService;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrSparePart;
import general.tables.Salary;
import general.tables.WriteoffRepair;
import general.tables.WriteoffRepairItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "logWriteoffRepairCrudBean")
@ViewScoped
public class WriteoffRepairCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3134616993886961657L;
	final static long TRANSACTION_ID = 378L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
			prepareMatnrMap();
		}
	}

	WriteoffRepair wr;

	public WriteoffRepair getWr() {
		return wr;
	}

	public void setWr(WriteoffRepair wr) {
		this.wr = wr;
	}

	private List<Matnr> partList;

	public List<Matnr> getPartList() {
		return partList;
	}

	public void setPartList(List<Matnr> partList) {
		this.partList = partList;
	}

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void prepareMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	private String pageHeader;
	private Long id;
	private String mode;

	public String getPageHeader() {
		MessageProvider mp = new MessageProvider();
		if (wr != null) {
			if (mode.equals("update")) {
				return mp.getValue("logistics.invoice_for_writeoff_repair") + " №" + wr.getId() + " / " + mp.getValue("logistics.editing");
			} else if (mode.equals("view")) {
				return mp.getValue("logistics.invoice_for_writeoff_repair") + " №" + wr.getId() + " / " + mp.getValue("view");
			}
		}

		if (mode.equals("create")) {
			return mp.getValue("logistics.invoice_for_writeoff_repair") + " / " + mp.getValue("logistics.creation");
		}

		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public void setMode(String mode) {
		if (!GeneralUtil.isAjaxRequest()) {
			this.mode = mode;
			if (!"view".equals(mode)) {
				salaryModel = new SalaryModel((SalaryDao) appContext.getContext().getBean("salaryDao"));
			}
			loadWr();
		}
	}

	private void loadWr() {
		if (mode.equals("create")) {
			wr = new WriteoffRepair();
			wr.setBranchId(userData.getBranch_id());
			wr.setBukrs(userData.getBukrs());
			wr.setDate(Calendar.getInstance().getTime());
			wr.setWriteoffRepairItems(new HashSet<WriteoffRepairItem>());
			wr.setStatusId(WriteoffRepair.STATUS_NEW);

		} else {
			WriteoffRepairDao d = (WriteoffRepairDao) appContext.getContext().getBean("writeoffRepairDao");
			wr = d.findWithDetail(id);
			if (wr != null) {
				setPartList(wr.getMatnrObject().getMatnr());
			}
		}
	}

	public String getMode() {
		return mode;
	}

	public void Save() {
		try {
			if (mode.equals("create")) {
				wr.setId(null);
				Create();
			} else if (mode.equals("update")) {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/invoice/writeoff/repair/View.xhtml?id=" + wr.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		WriteoffRepairService s = (WriteoffRepairService) appContext.getContext().getBean("writeoffRepairService");
		s.create(wr, userData.getUserid());
	}

	private void Update() {
		WriteoffRepairService s = (WriteoffRepairService) appContext.getContext().getBean("writeoffRepairService");
		s.update(wr, userData.getUserid());
	}

	public void addItemRow() {
		if (Validation.isEmptyLong(wr.getMatnrListId())) {
			GeneralUtil.addErrorMessage("Выберите материал");
			return;
		}
		WriteoffRepairItem wri = new WriteoffRepairItem();
		wri.setQuantity(1D);
		wr.addWriteoffRepairItem(wri);
	}

	public void deleteItemsRow(WriteoffRepairItem wpi) {
		wr.removeWriteoffRepairItem(wpi);
	}

	private MatnrList selectedMatnrList;

	public MatnrList getSelectedMatnrList() {
		return selectedMatnrList;
	}

	public void setSelectedMatnrList(MatnrList selectedMatnrList) {
		this.selectedMatnrList = selectedMatnrList;
	}

	private void setPartList(Long matnr) {
		partList = new ArrayList<Matnr>();
		MatnrSparePartDao mspd = (MatnrSparePartDao) appContext.getContext().getBean("matnrSparePartDao");
		for (MatnrSparePart msp : mspd.findAllByTovarID(matnr)) {
			Matnr m = matnrMap.get(msp.getSparepart_id());
			if (m != null) {
				partList.add(m);
			}
		}
	}

	public void onSelectMl(SelectEvent e) {
		MatnrList ml = (MatnrList) e.getObject();
		if (ml != null) {
			wr.setWriteoffRepairItems(new HashSet<WriteoffRepairItem>());
			wr.setMatnrListId(ml.getMatnr_list_id());
			wr.setMatnrName(ml.getMatnrObject().getText45() + " (" + ml.getBarcode() + ") ");
			wr.setBarcode(ml.getBarcode());
			wr.setMatnrObject(ml.getMatnrObject());

			// setPartList(ml.getMatnr());
		}

		GeneralUtil.hideDialog("MatnrListDialog");
	}

	public void onSelectPart(SelectEvent e) {
		Matnr m = (Matnr) e.getObject();
		if (m != null) {
			WriteoffRepairItem wri = new WriteoffRepairItem();
			wri.setQuantity(1D);
			wri.setMatnr(m.getMatnr());
			wr.addWriteoffRepairItem(wri);
		}
		GeneralUtil.hideDialog("PartListDialog");
	}

	public void loadMatnrListPart() {
		partList = new ArrayList<Matnr>();
		if (wr != null && wr.getMatnrObject() != null) {
			MatnrSparePartDao mspd = (MatnrSparePartDao) appContext.getContext().getBean("matnrSparePartDao");
			for (MatnrSparePart msp : mspd.findAllByTovarID(wr.getMatnrObject().getMatnr())) {
				Matnr m = matnrMap.get(msp.getSparepart_id());
				if (m != null) {
					partList.add(m);
				}
			}
		}
	}

	List<MatnrList> mlListForRepair = new ArrayList<MatnrList>();

	public List<MatnrList> getMlListForRepair() {
		return mlListForRepair;
	}

	public void loadMlListForRepair() {
		mlListForRepair = new ArrayList<MatnrList>();
		if (!Validation.isEmptyLong(wr.getFromWerks())) {
			MatnrListDao d = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
			// mlListForRepair = d.findAll(
			// String.format(" barcode IS NOT NULL AND matnr IN(%s) ", "'"
			// + String.join("','", matnrIds) + "'"), 0);
			mlListForRepair = d.findAll(String.format(" barcode IS NOT NULL AND bukrs = '%s' AND werks = %d ",
					wr.getBukrs(), wr.getFromWerks()), 0);
			for (MatnrList ml : mlListForRepair) {
				Matnr m = matnrMap.get(ml.getMatnr());
				if (m != null) {
					ml.setMatnrObject(m);
				}
			}
		}
	}

	public void Writeoff() {
		try {

			WriteoffRepairService s = (WriteoffRepairService) appContext.getContext().getBean("writeoffRepairService");
			s.writeoff(wr, userData.getUserid());
			GeneralUtil.doRedirect("/logistics/invoice/writeoff/repair/View.xhtml?id=" + wr.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private SalaryModel salaryModel;

	public SalaryModel getSalaryModel() {
		return salaryModel;
	}

	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	Salary selectedSalary;

	public Salary getSelectedSalary() {
		return selectedSalary;
	}

	public void setSelectedSalary(Salary selectedSalary) {
		this.selectedSalary = selectedSalary;
	}

	public void assignSalary() {
		if (selectedSalary != null) {
			wr.setMaster(selectedSalary.getP_staff());
			wr.setMasterId(selectedSalary.getStaff_id());
		}
	}

	public boolean showWriteoffButton() {
		if (wr == null) {
			return false;
		}

		return wr.getStatusId() == WriteoffRepair.STATUS_NEW && "view".equals(mode)
				&& userData.getUserid().equals(wr.getCreatedBy());
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
