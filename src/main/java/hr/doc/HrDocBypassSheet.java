package hr.doc;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import f4.BranchF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.HrDocDao;
import general.dao.SalaryDao;
import general.services.hr.HrDocService;
import general.tables.HrDoc;
import general.tables.HrDocItem;
import general.tables.Salary;
import general.tables.search.SalarySearch;
import user.User;

@ManagedBean(name = "hrDocBypassSheetBean")
@ViewScoped
public class HrDocBypassSheet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8990194088875442589L;

	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}
		try {
			id = new Long(GeneralUtil.getRequestParameter("id"));
		} catch (NumberFormatException e) {
			id = 0L;
		}

		if (Validation.isEmptyLong(id)) {
			try {
				staffId = new Long(GeneralUtil.getRequestParameter("staffId"));
			} catch (NumberFormatException e) {
				staffId = 0L;
			}
		}
	}

	private void prepareSearchModel() {
		searchModel = new SalarySearch();
		searchModel.setBukrs(userData.getBukrs());
		if (!userData.isMain() && !userData.isSys_admin()) {
			searchModel.setBranchIds(userData.getUserBranchIdsAsStringList());
		}
	}

	private Long id;
	private Long staffId;
	private HrDoc document;
	private String mode;
	private String pageHeader;
	private List<Salary> salaries;
	private SalarySearch searchModel;

	public SalarySearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SalarySearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Salary> getSalaries() {
		return salaries;
	}

	public void setSalaries(List<Salary> salaries) {
		this.salaries = salaries;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		pageHeader = document.getDocTypeName();
		if ("create".equals(mode)) {
			pageHeader += " / Новый документ";
		} else if ("update".equals(mode)) {
			if (document != null) {
				pageHeader += " / Редактирование документа №" + document.getFormattedRegNumber();
			}
		}
	}

	public void initBean(String mode) {
		if (!GeneralUtil.isAjaxRequest()) {
			this.mode = mode;
			loadOrBlankDoc();
			setPageHeader();
			prepareSearchModel();
			loadSalaries();
		}
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public HrDoc getDocument() {
		return document;
	}

	public void setDocument(HrDoc document) {
		this.document = document;
	}

	private void loadOrBlankDoc() {
		if (Validation.isEmptyLong(id)) {
			document = new HrDoc();
			document.setTypeId(HrDoc.TYPE_BYPASS_SHEET);
			document.setCreatedAt(Calendar.getInstance().getTime());
			document.setCreatedBy(userData.getUserid());
			document.setUpdatedAt(Calendar.getInstance().getTime());
			document.setUpdatedBy(userData.getUserid());

			if (!userData.isMain() && !userData.isSys_admin()) {
				document.setBukrs(userData.getBukrs());
			}

		} else {
			HrDocDao hrDocDao = (HrDocDao) appContext.getContext().getBean("hrDocDao");
			document = hrDocDao.find(id);
		}
	}

	private void loadSalaries() {
		SalaryDao salaryDao = (SalaryDao) appContext.getContext().getBean("salaryDao");
		salaries = salaryDao.findAllOnDismiss(getSearchModel());
	}

	public void onSelectSalary(SelectEvent sEvent) {

		Salary sal = (Salary) sEvent.getObject();
		if (sal != null) {
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
			GeneralUtil.hideDialog("salaryWidget");
		}
	}

	public void onBranchChange() {
		if (Validation.isEmptyLong(document.getBranchId())) {
			if (!userData.isSys_admin() && !userData.isMain()) {
				searchModel.setBranchIds(userData.getUserBranchIdsAsStringList());
			}
		} else {
			searchModel.setBranch_id(document.getBranchId());
			searchModel.setBranchIds(null);
		}

		loadSalaries();
	}

	public void Save() {
		try {
			HrDocService service = (HrDocService) appContext.getContext().getBean("hrDocService");
			if (HrConstants.DOC_UI_MODE_CREATE.equals(mode)) {
				service.create(document, userData);
			} else if (HrConstants.DOC_UI_MODE_UPDATE.equals(mode)) {
				service.update(document, userData);
			} else {
				throw new DAOException("Not Supported Mode!");
			}

			GeneralUtil.doRedirect("/hr/doc/View.xhtml?id=" + document.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
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

	@ManagedProperty("#{positionF4Bean}")
	PositionF4 positionF4;

	public PositionF4 getPositionF4() {
		return positionF4;
	}

	public void setPositionF4(PositionF4 positionF4) {
		this.positionF4 = positionF4;
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
