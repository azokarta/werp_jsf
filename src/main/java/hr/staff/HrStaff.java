package hr.staff;

import f4.BranchF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.PermissionController;
import general.Validation;
import general.dao.AddressDao;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.services.StaffService;
import general.services.UpdFileService;
import general.tables.Address;
import general.tables.Branch;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.UpdFile;
import general.tables.search.StaffSearch;
import hr.salary.HrSalary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;

import datamodels.StaffModel;
import user.User;

@ManagedBean(name = "hrStaffBean")
@ViewScoped
public class HrStaff implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5880714994244868334L;
	private static final long transactionId = 83L;

	private boolean currentUserIsStaff = false;
	private Staff scout = new Staff();
	private Salary scoutSalary;

	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		// PermissionController.canRead(userData, transactionId);
		prepareUserBranchesMap();
		prepareUserBranches();
		try {
			staffId = Long.valueOf(GeneralUtil.getRequestParameter("staffId"));
		} catch (Exception e) {
			staffId = userData.getStaff().getStaff_id();
			currentUserIsStaff = true;
		}
	}

	/*
	 * mode - view|update|list
	 */
	private String mode;

	public String getMode() {
		return mode;
	}

	private boolean canView() {
		if (currentUserIsStaff || userData.isMain() || userData.isSys_admin()) {
			return true;
		}

		for (Salary s : hrSalaryBean.getItems()) {
			if (s.isCurrentSalary()) {
				if (!userBranchesMap.containsKey(s.getBranch_id())) {
					return false;
				}
			}
		}

		return true;
	}

	private Address residenceAddress = null;
	private Address livingAddress = null;

	public Address getResidenceAddress() {
		return residenceAddress;
	}

	public void setResidenceAddress(Address residenceAddress) {
		this.residenceAddress = residenceAddress;
	}

	public Address getLivingAddress() {
		return livingAddress;
	}

	public void setLivingAddress(Address livingAddress) {
		this.livingAddress = livingAddress;
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		if (!userData.isSys_admin() && !userData.isMain()) {
			BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");
			userBranches = bd.findChilds(userData.getBranch_id());
		}
	}

	private Map<Long, Branch> userBranchesMap = new HashMap<Long, Branch>();

	private void branchChildsRecursive(Long parentBranchId) {
		for (Branch br : branchF4.getBranch_list()) {
			if (!Validation.isEmptyLong(br.getParent_branch_id()) && br.getParent_branch_id().equals(parentBranchId)) {
				userBranchesMap.put(br.getBranch_id(), br);
				branchChildsRecursive(br.getBranch_id());
			}
		}
	}

	private void prepareUserBranchesMap() {
		branchChildsRecursive(userData.getBranch_id());
		userBranchesMap.put(userData.getBranch_id(), new Branch());

	}

	public void initMode(String mode) {
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}
		this.mode = mode;
		if (mode.equals("view")) {
			loadStaffWithDetail();
			initAdditionalItems();
			loadAddresses();
			blankAddresses();
			if (!canView()) {
				GeneralUtil.doRedirect("/no_permission.xhtml");
			}

		} else if (mode.equals("update")) {
			PermissionController.canWrite(userData, transactionId);
			loadStaff();
			blankAddresses();
		} else if (mode.equals("create")) {
			PermissionController.canWrite(userData, transactionId);
			staff = new Staff();
			blankAddresses();
		} else {
			staffModel = new StaffModel(getStaffDao());
			staffModel.setUserBranches(userBranches);
			Integer dismissed = 0;
			try {
				dismissed = Integer.valueOf(GeneralUtil.getRequestParameter("dismissed"));
			} catch (Exception e) {
				//
			}

			if (dismissed != null && dismissed == 1) {
				staffModel.setDismissed(true);
			} else {
				staffModel.setDismissed(false);
			}
		}
		setPageHeader();
	}

	private StaffModel staffModel;
	private Long staffId;
	private Staff staff;
	private List<Staff> items;
	private StaffSearch searchModel = new StaffSearch();
	private String pageHeader;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public StaffSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(StaffSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Staff> getItems() {
		return items;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public StaffModel getStaffModel() {
		return staffModel;
	}

	private void loadStaffWithDetail() {
		staff = getStaffDao().findWithDetail(staffId);
		if (staff == null) {
			throw new DAOException("Сотрудник не найден!");
		}
	}

	private void loadStaff() {
		staff = getStaffDao().find(staffId);
		if (staff == null) {
			throw new DAOException("Сотрудник не найден!");
		}
		
		if(!Validation.isEmptyLong(staff.getTsStaffId())){
			scout = getStaffDao().find(staff.getTsStaffId());
		}

		loadAddresses();
	}

	private void loadAddresses() {
		AddressDao ad = (AddressDao) appContext.getContext().getBean("addressDao");
		List<Address> l = ad.findAll(" customer_id = " + staff.getCustomer_id());
		for (Address a : l) {
			if (a.getAddrType() == Address.TYPE_REGISTERED) {
				residenceAddress = a;
			} else if (a.getAddrType() == Address.TYPE_HOME) {
				livingAddress = a;
			}
		}
	}

	boolean isNewLivingAddress = false;
	boolean isNewResAddress = false;

	private void blankAddresses() {
		if (livingAddress == null) {
			livingAddress = new Address();
			livingAddress.setAddrType(Address.TYPE_HOME);
			isNewLivingAddress = true;
		}

		if (residenceAddress == null) {
			residenceAddress = new Address();
			residenceAddress.setAddrType(Address.TYPE_REGISTERED);
			isNewResAddress = true;
		}
	}

	private void setPageHeader() {
		MessageProvider mp = new MessageProvider();
		if (mode.equals("view")) {
			pageHeader = mp.getValue("hr.staff_card") + " / " + staff.getLF();
			// + (staff.getDismissed() > 0 ? " (Уволенный)" : "");
		} else if (mode.equals("update")) {
			pageHeader = mp.getValue("hr.update_staff") + " / " + staff.getLF();
		} else if (mode.equals("create")) {
			pageHeader = mp.getValue("hr.create_staff") ;
		} else {

		}
	}

	public void Save() {
		boolean isCreate = false;
		try {
			if (staff == null) {
				throw new DAOException("Selected Not Found Exception");
			}

			if (Validation.isEmptyLong(staff.getStaff_id())) {
				isCreate = true;
				Create();
			} else {
				Update();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.doRedirect("/hr/staff/View.xhtml?staffId=" + staff.getStaff_id());
			setEditMode(false);

			// if (mode.equals("create")) {
			// GeneralUtil.doRedirect("/hr/staff/View.xhtml?staffId="
			// + staff.getStaff_id());
			// }
		} catch (DAOException e) {
			if (isCreate) {
				staff.setStaff_id(null);
			}
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		staff.setCreated_by(userData.getUserid());
		staff.setUpdated_by(userData.getUserid());
		staff.setCreated_date(Calendar.getInstance().getTime());

		getStaffService().createStaff(staff, userData, livingAddress, residenceAddress);
	}

	private void Update() {
		staff.setUpdated_by(userData.getUserid());
		staff.setUpdated_date(Calendar.getInstance().getTime());
		livingAddress.setAddr_id(null);
		residenceAddress.setAddr_id(null);
		getStaffService().updateStaff(staff, userData, livingAddress, residenceAddress);
	}

	private boolean editMode = false;

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	private void initAdditionalItems() {
		hrSalaryBean.setSelectedStaff(staff);
		hrSalaryBean.loadItems();

		// hrExpenceBean.setSelectedStaff(staff);
		// hrExpenceBean.loadItems();

		hrEducationBean.setSelectedStaff(staff);
		// hrEducationBean.loadItems();

		hrDepositBean.setSelectedStaff(staff);
		hrDepositBean.loadItems();

		hrSodBean.setSelectedStaff(staff);
		hrSodBean.loadItems();
		// fileBean.getUf().setContext("staff");
		// fileBean.getUf().setContext_id(staff.getStaff_id());
		// loadStaffFiles();

		hrStaffFileBean.setSelectedStaff(staff);
		hrStaffFileBean.loadFiles();
	}

	public void onTabChange(TabChangeEvent event) {
		setEditMode(false);
		String currentTab = event.getTab().getId();
		// GeneralUtil.updateFormElement("empTabs:" + currentTab);
	}

	public String getSalaries(Staff stf) {
		StringBuffer sb = new StringBuffer("");
		List<Salary> l = stf.getCurrentSalaries();
		if (l == null) {
			return sb.toString();
		}
		for (Salary sl : l) {
			sb.append(positionF4Bean.getName(sl.getPosition_id().toString()) + ", ");
		}
		return sb.toString().replaceAll(", $", "");
	}

	public void Search() {
		selected = null;
	}

	private Staff selected;

	public Staff getSelected() {
		return selected;
	}

	public void setSelected(Staff selected) {
		this.selected = selected;
	}

	private String breadcrumb;

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	// private List<UpdFile> staffFiles;
	//
	// public List<UpdFile> getStaffFiles() {
	// return staffFiles;
	// }
	//
	// public void setStaffFiles(List<UpdFile> staffFiles) {
	// this.staffFiles = staffFiles;
	// }

	// private void loadStaffFiles() {
	// staffFiles = new ArrayList<UpdFile>();
	// UploadFileDao ufDao = (UploadFileDao) appContext.getContext().getBean(
	// "uploadFileDao");
	// staffFiles = ufDao.findAll(" context = 'staff' AND context_id = "
	// + staff.getStaff_id());
	// }

	/***************************/

	private StaffService getStaffService() {
		return (StaffService) appContext.getContext().getBean("staffService");
	}

	private StaffDao getStaffDao() {
		return (StaffDao) appContext.getContext().getBean("staffDao");
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4;

	public BranchF4 getBranchF4() {
		return branchF4;
	}

	public void setBranchF4(BranchF4 branchF4) {
		this.branchF4 = branchF4;
	}

	@ManagedProperty("#{userinfo}")
	User userData;
	@ManagedProperty("#{hrSalaryBean}")
	HrSalary hrSalaryBean;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public HrSalary getHrSalaryBean() {
		return hrSalaryBean;
	}

	public void setHrSalaryBean(HrSalary hrSalaryBean) {
		this.hrSalaryBean = hrSalaryBean;
	}

	@ManagedProperty("#{hrExpenceBean}")
	HrExpence hrExpenceBean;

	public HrExpence getHrExpenceBean() {
		return hrExpenceBean;
	}

	public void setHrExpenceBean(HrExpence hrExpenceBean) {
		this.hrExpenceBean = hrExpenceBean;
	}

	@ManagedProperty("#{hrEducationBean}")
	HrEducation hrEducationBean;

	public HrEducation getHrEducationBean() {
		return hrEducationBean;
	}

	public void setHrEducationBean(HrEducation hrEducationBean) {
		this.hrEducationBean = hrEducationBean;
	}

	@ManagedProperty("#{hrDepositBean}")
	HrDeposit hrDepositBean;

	public HrDeposit getHrDepositBean() {
		return hrDepositBean;
	}

	public void setHrDepositBean(HrDeposit hrDepositBean) {
		this.hrDepositBean = hrDepositBean;
	}

	@ManagedProperty("#{hrSodBean}")
	HrSod hrSodBean;

	public HrSod getHrSodBean() {
		return hrSodBean;
	}

	public void setHrSodBean(HrSod hrSodBean) {
		this.hrSodBean = hrSodBean;
	}

	@ManagedProperty("#{positionF4Bean}")
	PositionF4 positionF4Bean;

	public PositionF4 getPositionF4Bean() {
		return positionF4Bean;
	}

	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}

	@ManagedProperty("#{hrStaffFileBean}")
	HrStaffFile hrStaffFileBean;

	public HrStaffFile getHrStaffFileBean() {
		return hrStaffFileBean;
	}

	public void setHrStaffFileBean(HrStaffFile hrStaffFileBean) {
		this.hrStaffFileBean = hrStaffFileBean;
	}

	public void Dismiss() {
		try {
			getStaffService().dismissStaff(staff);
			GeneralUtil.addSuccessMessage("Сотрудник уволен");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean canRead(String action) {
		if (currentUserIsStaff) {
			return true;
		}
		if (action.equals("salary")) {
			return PermissionController.canView(userData, 212L);// HRSALARYLIST
		}
		return false;
	}

	public boolean canCreate(String action) {
		if (action.equals("salary")) {
			return PermissionController.canCreate(userData, 212L);
		}
		return false;
	}

	public boolean canAll(String action) {
		if (action.equals("salary")) {
			return PermissionController.canAll(userData, 212L);
		} else if ("staff".equals(action)) {
			return PermissionController.canAll(userData, transactionId);
		}
		return false;
	}

	private Part updFile;

	public Part getUpdFile() {
		return updFile;
	}

	public void setUpdFile(Part updFile) {
		this.updFile = updFile;
	}

	public void UploadFile() {
		try {
			StaffService s = (StaffService) appContext.getContext().getBean("staffService");

			s.saveFile(staff, updFile, userData);
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.doRedirect("/hr/staff/View.xhtml?staffId=" + staff.getStaff_id());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void uploadAvatar(FileUploadEvent event) {
		try {
			UploadedFile file = (UploadedFile) event.getFile();
			UpdFile uf = new UpdFile();
			uf.setFile_name(file.getFileName());
			uf.setFile_size(file.getSize());
			uf.setMime_type(file.getContentType());

			UpdFileService ufService = (UpdFileService) appContext.getContext().getBean("updFileService");
			ufService.create(file, uf, userData);

			staff.setImage_id(uf.getId());
			GeneralUtil.addSuccessMessage("Картинка сохранено успешно!");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}

	}

	/**
	 * Закрыть редактирования L(lastname)M(Middlename)F(Firstname) юзерам кроме
	 * админа и Головного (is_root)
	 * 
	 * @return boolean
	 */
	public boolean disableLmf() {
		Long gulshatUserId = Long.valueOf(489);
		if ("create".equals(mode) || userData.isSys_admin() || userData.isMain() || gulshatUserId.equals(userData.getUserid())) {
			return false;
		}
		
		return true;
	}

	public Staff getScout() {
		return scout;
	}

	public void setScout(Staff scout) {
		this.scout = scout;
	}

	public Salary getScoutSalary() {
		return scoutSalary;
	}

	public void setScoutSalary(Salary scoutSalary) {
		this.scoutSalary = scoutSalary;
	}
	
	public void assignScoutSalary() {
		if (scoutSalary != null && scoutSalary.getP_staff() != null) {
			scout = scoutSalary.getP_staff();
			staff.setTsStaffId(scout.getStaff_id());
		}
	}

	public void resetScoutSalary() {
		scoutSalary = null;
	}
	
	public void removeSelectedScout(){
		scoutSalary = null;
		scout = new Staff();
		staff.setTsStaffId(null);
	}

	// public void deleteStaff(){
	// try{
	// StaffService ss =
	// (StaffService)appContext.getContext().getBean("staffService");
	// ss.removeStaffDupl(staff.getStaff_id());
	// GeneralUtil.addSuccessMessage("Success");
	// }catch(DAOException e){
	// GeneralUtil.addErrorMessage(e.getMessage());
	// }
	// }
}
