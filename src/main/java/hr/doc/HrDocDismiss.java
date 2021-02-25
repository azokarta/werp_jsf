package hr.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import f4.BranchF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.beans.BranchBean;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.HrDocDao;
import general.dao.SalaryDao;
import general.services.hr.HrDocService;
import general.tables.Branch;
import general.tables.HrDoc;
import general.tables.HrDocItem;
import general.tables.Salary;
import user.User;

@ManagedBean(name = "hrDocDismissBean")
@ViewScoped
public class HrDocDismiss implements Serializable {

	/**
	 * 
	 */
	private static final String MODE_CREATE = "create";
	private static final String MODE_UPDATE = "update";
	private static final String MODE_VIEW = "view";
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				id = Long.parseLong(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
		}
	}

	private Long id;

	private String mode;
	private String pageHeader = "";

	public String getPageHeader() {
		return pageHeader;
	}

	public String getMode() {
		return mode;
	}

	public void initBean(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {

			loadOrBlankDocument();
			setPageHeader();
		}
	}

	private void setPageHeader() {
		pageHeader = document.getDocTypeName();
		if (MODE_CREATE.equals(mode)) {
			pageHeader += " / Новый документ";
		} else if (MODE_UPDATE.equals(mode)) {
			if (document != null) {
				pageHeader += " / Редактирование документа №" + document.getFormattedRegNumber();
			}
		}
	}

	public void Save() {
		try {
			HrDocService service = (HrDocService) appContext.getContext().getBean("hrDocService");
			if (MODE_CREATE.equals(mode)) {
				service.create(document, userData);
			} else if (MODE_UPDATE.equals(mode)) {
				service.update(document, userData);
			} else {
				throw new DAOException("Not Supported Mode!");
			}

			GeneralUtil.doRedirect("/hr/doc/View.xhtml?id=" + document.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void loadOrBlankDocument() {
		if (MODE_CREATE.equals(mode)) {
			document = new HrDoc();
			document.setTypeId(HrDoc.TYPE_DISMISS);
			document.setCreator(userData.getStaff());
			if (!userData.isMain() && !userData.isSys_admin()) {
				document.setBukrs(userData.getBukrs());
			}
		} else if (MODE_VIEW.equals(mode) || MODE_UPDATE.equals(mode)) {
			HrDocDao d = (HrDocDao) appContext.getContext().getBean("hrDocDao");
			document = d.findWithDetail(id);

			if (MODE_UPDATE.equals(mode)) {
				prepareItemBranches();
				for (HrDocItem item : document.getHrDocItems()) {
					prepareManagerList(item.getBranchId());
				}

				prepareSalaryList();
			}
		}
	}

	HrDoc document;

	public HrDoc getDocument() {
		return document;
	}

	public void setDocument(HrDoc document) {
		this.document = document;
	}

	public void addItemRow() {
		HrDocItem item = new HrDocItem();
		document.addHrDocItem(item);
	}

	public void removeItemRow(HrDocItem item) {
		document.removeHrDocItem(item);
	}

	private List<Salary> managerList = new ArrayList<>();

	public List<Salary> getManagerList() {
		return managerList;
	}

	// Загрузка менеджеров
	private void prepareManagerList(Long branchId) {
		managerList = new ArrayList<>();
		if (managerMap.get(branchId) == null) {
			SalaryDao salDao = (SalaryDao) appContext.getContext().getBean("salaryDao");
			managerList = salDao
					.findAllCurrentWithStaff(String.format(" s1.branch_id = %d AND s1.position_id = %d", branchId, 3));

			managerMap.put(branchId, managerList);
		}
	}

	public void onItemRowEdit(RowEditEvent event) {
		System.out.println("CHANGED " + event);
	}

	public void onItemRowCancel(RowEditEvent event) {
		System.out.println("CANCELLED: " + event);
	}

	public void onItemCellEdit(CellEditEvent event) {
		System.out.println("CELL EVENT TEST: " + event);
	}

	Map<Long, List<Salary>> managerMap = new HashMap<>();

	public List<Salary> getManagers(Long branchId) {
		if (Validation.isEmptyLong(branchId)) {
			return null;
		}
		return managerMap.get(branchId);
	}

	private List<Branch> itemBranches = new ArrayList<>();

	public List<Branch> getItemBranches() {
		return itemBranches;
	}

	private void prepareItemBranches() {
		if (document != null && !Validation.isEmptyLong(document.getBranchId())) {
			Branch docBranch = null;
			for (Branch br : branchF4.getBranch_list()) {
				if (document.getBranchId().equals(br.getBranch_id())) {
					docBranch = br;
					break;
				}
			}

			if (docBranch != null) {
				Integer brType = docBranch.getType().intValue();
				BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
				if (brType.equals(Branch.TYPE_CITY)) {
					itemBranches = brDao.findChilds(docBranch.getBranch_id());
				} else if (brType.equals(Branch.TYPE_BRANCH)) {
					itemBranches = brDao.findChilds(docBranch.getParent_branch_id());
				}
				// if(docBranch.getType().equals(Branch.))
			}
		}
	}

	public void onItemBranchChanged(Long fromSalaryId) {
		if (!Validation.isEmptyLong(fromSalaryId)) {
			for (HrDocItem item : document.getHrDocItems()) {
				if (fromSalaryId.equals(item.getSalaryId())) {
					prepareManagerList(item.getBranchId());
				}
			}
		}
	}

	public void onSelectSalary(SelectEvent e) {
		Salary sal = (Salary) e.getObject();
		if (sal != null) {
			boolean hasError = false;
			for (HrDocItem item : document.getHrDocItems()) {
				if (item.getSalaryId().equals(sal.getSalary_id())) {
					GeneralUtil.addErrorMessage("Сотрудник уже добавлен");
					hasError = true;
					break;
				}
			}

			if (!hasError) {
				HrDocItem item = new HrDocItem();
				item.setSalaryId(sal.getSalary_id());
				item.setStaffId(sal.getStaff_id());
				item.setSalary(sal);
				item.setStaff(sal.getP_staff());
				item.setDepartmentId(sal.getDepartment_id());
				item.setPositionId(sal.getPosition_id());
				item.setBranchId(sal.getBranch_id());
				item.setBukrs(sal.getBukrs());
				document.addHrDocItem(item);
				GeneralUtil.hideDialog("staffWidget");
			}
		}
	}

	private List<Salary> salaryList = new ArrayList<>();

	public List<Salary> getSalaryList() {
		return salaryList;
	}

	public void setSalaryList(List<Salary> salaryList) {
		this.salaryList = salaryList;
	}

	private void prepareSalaryList() {
		salaryList = new ArrayList<>();
		if (document != null) {
			SalaryDao salDao = (SalaryDao) appContext.getContext().getBean("salaryDao");
			salaryList = salDao.findAllCurrentWithStaff(" s1.branch_id = " + document.getBranchId());
		}
	}

	public void onBranchChange() {
		document.setHrDocItems(new ArrayList<>());
		prepareSalaryList();
		prepareItemBranches();
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

	@ManagedProperty("#{branchBean}")
	BranchBean branchBean;

	public BranchBean getBranchBean() {
		return branchBean;
	}

	public void setBranchBean(BranchBean branchBean) {
		this.branchBean = branchBean;
	}

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4;

	public BranchF4 getBranchF4() {
		return branchF4;
	}

	public void setBranchF4(BranchF4 branchF4) {
		this.branchF4 = branchF4;
	}	

}
