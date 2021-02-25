package hr.salary;

import f4.BranchF4;
import f4.BukrsF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.services.SalaryService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.search.SalarySearch;
import general.tables.search.StaffSearch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.NoResultException;

import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "hrSalaryBean")
@ViewScoped
public class HrSalary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6747974378201296592L;
	private static final Long transactionId = 212L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			// salaryModel = new SalaryModel(getSalaryDao());
			// salaryModel.getSearchModel().setCurrent(true);
			prepareUserBranches();
			StaffDao sd = (StaffDao) appContext.getContext().getBean("staffDao");
			stfMap = sd.getMappedList("");
			loadItems();
			// System.out.println("TEST");
		}
	}

	private Map<Long, Staff> stfMap;

	public boolean canAll() {
		return PermissionController.canAll(userData, transactionId);
	}

	public boolean canView() {
		return PermissionController.canView(userData, transactionId);
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	private void prepareUserBranches() {
		userBranches.clear();

		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = brDao.findChilds(userData.getBranch_id());
	}

	public void onChangeBranch() {
		System.out.println("BRANCH ID: " + selected.getBranch_id());
		setPyramidItems();
	}

	private Staff selectedStaff;
	private Salary selected = new Salary();
	private List<Salary> items;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public Salary getSelected() {
		return selected;
	}

	public void setSelected(Salary selected) {
		this.selected = selected;
	}

	public List<Salary> getItems() {
		return items;
	}

	StaffSearch staffSearchModel = new StaffSearch();
	SalarySearch salarySearchModel = new SalarySearch();

	public StaffSearch getStaffSearchModel() {
		return staffSearchModel;
	}

	public void setStaffSearchModel(StaffSearch staffSearchModel) {
		this.staffSearchModel = staffSearchModel;
	}

	public SalarySearch getSalarySearchModel() {
		return salarySearchModel;
	}

	public void setSalarySearchModel(SalarySearch salarySearchModel) {
		this.salarySearchModel = salarySearchModel;
	}

	public void loadItems() {
		if (selectedStaff == null) {
			items = getSalaryDao().findAllWithStaff(searchModel.getPreparedCondition());
		} else {
			items = new ArrayList<Salary>();
			List<Salary> temp = getSalaryDao().findAll("staff_id = " + selectedStaff.getStaff_id());
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).isCurrentSalary()) {
					items.add(temp.get(i));
				}
			}
			for (int i = 0; i < temp.size(); i++) {
				if (!temp.get(i).isCurrentSalary()) {
					items.add(temp.get(i));
				}
			}
		}
	}

	private Salary selectedForRemove;

	public Salary getSelectedForRemove() {
		return selectedForRemove;
	}

	public void setSelectedForRemove(Salary selectedForRemove) {
		this.selectedForRemove = selectedForRemove;
		setRemoveDate(new Date());
	}

	private Date payrollDate;

	public Date getPayrollDate() {
		return payrollDate;
	}

	public void setPayrollDate(Date payrollDate) {
		this.payrollDate = payrollDate;
	}

	public void ChangePayrollDate() {
		try {
			if (selectedForPayrollChange == null) {
				throw new DAOException("Должность не выбран");
			}
			selectedForPayrollChange.setPayroll_date(GeneralUtil.removeTime(payrollDate));
			selectedForPayrollChange.setUpdated_by(userData.getUserid());
			selectedForPayrollChange.setUpdated_date(Calendar.getInstance().getTime());
			getSalaryService().updateSalary(selectedForPayrollChange);
			GeneralUtil.addSuccessMessage("У сотрудника успешно изменен Дата Выдачи!");
			GeneralUtil.hideDialog("SalaryPayrollDlg");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	Salary selectedForPayrollChange;

	public void setSelectedForChangePayrollDate(Salary selectedForPayrollChange) {
		this.selectedForPayrollChange = selectedForPayrollChange;
	}

	private Date removeDate;

	public Date getRemoveDate() {
		return removeDate;
	}

	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}

	public void Remove() {
		try {
			if (selectedForRemove == null) {
				throw new DAOException("Должность не выбран");
			}
			selectedForRemove.setEnd_date(GeneralUtil.removeTime(removeDate));
			getSalaryService().removeSalary(selectedForRemove, userData.getUserid());
			GeneralUtil.addSuccessMessage("Сотрудник удачно освобожден от должности!");
			GeneralUtil.hideDialog("SalaryRemoveDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public Salary prepareCreate() {
		selected = new Salary();
		return selected;
	}

	private Long parentPyramidId = 0L;

	public Long getParentPyramidId() {
		return parentPyramidId;
	}

	public void setParentPyramidId(Long parentPyramidId) {
		this.parentPyramidId = parentPyramidId;
	}

	private List<OutputPyramid> outputPyramidList = new ArrayList<OutputPyramid>();

	public List<OutputPyramid> getOutputPyramidList() {
		return outputPyramidList;
	}

	public void setPyramidItems() {
		outputPyramidList.clear();
		if (!Validation.isEmptyString(selected.getBukrs()) && !Validation.isEmptyLong(selected.getBranch_id())) {
			PyramidDao pDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");
			List<Pyramid> pyrList = pDao.dynamicFindPyramid(" branch_id = " + selected.getBranch_id());
			List<Pyramid> rootList = getRootList(pyrList);

			for (Pyramid root : rootList) {
				System.out.println("ROOT ID: " + root.getPyramid_id());
				outputPyramidList.add(getOutputPyramid(root, 0));
				addChildsRecursively(root, pyrList, 1);
			}
		}
	}

	private List<Pyramid> getRootList(List<Pyramid> pyrList) {
		List<Pyramid> out = new ArrayList<>();
		Iterator<Pyramid> it = pyrList.iterator();
		while (it.hasNext()) {
			Pyramid current = it.next();
			boolean hasParent = false;
			for (Pyramid pyr : pyrList) {
				if (!current.getPyramid_id().equals(pyr.getPyramid_id())) {
					if (current.getParent_pyramid_id().equals(pyr.getPyramid_id())) {
						hasParent = true;
						break;
					}
				}
			}

			if (!hasParent) {
				out.add(current);
			}
		}
		return out;
	}

	private OutputPyramid getOutputPyramid(Pyramid p, int deep) {
		OutputPyramid op = new OutputPyramid();
		StringBuffer sb = new StringBuffer("");
		for (int k = 0; k < deep; k++) {
			sb.append(" -> ");
		}
		Staff stf = null;
		if ((stf = stfMap.get(p.getStaff_id())) != null) {
			sb.append(stf.getLF());
		}

		if (!Validation.isEmptyLong(p.getPosition_id())) {
			sb.append("(" + positionF4Bean.getName("" + p.getPosition_id()) + ") ");
		}
		op.setTitle(sb.toString());
		op.setPyramid_id(p.getPyramid_id());
		return op;
	}

	private void addChildsRecursively(Pyramid parent, List<Pyramid> pyrList, int deep) {
		Iterator<Pyramid> it = pyrList.iterator();
		while (it.hasNext()) {
			Pyramid current = it.next();
			if (parent.getPyramid_id().equals(current.getParent_pyramid_id())) {
				outputPyramidList.add(getOutputPyramid(current, deep));
				addChildsRecursively(current, pyrList, deep + 1);
			}
		}
	}

	public class OutputPyramid {
		Long pyramid_id;
		String title;

		public Long getPyramid_id() {
			return pyramid_id;
		}

		public void setPyramid_id(Long pyramid_id) {
			this.pyramid_id = pyramid_id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	/********* CRUD *********/

	public void Save() {
		try {
			if (selectedStaff == null) {
				throw new DAOException("Выберите сотрудника");
			}
			if (Validation.isEmptyLong(selected.getSalary_id())) {
				Create();
			} else {
				Update();
			}
			loadItems();
			GeneralUtil.hideDialog("SalaryCreateDialog");
			GeneralUtil.addSuccessMessage("Сохранено успешно!");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		selected.setCreated_by(userData.getUserid());
		selected.setUpdated_by(userData.getUserid());
		selected.setStaff_id(selectedStaff.getStaff_id());
		selected.setP_staff(selectedStaff);
		for (Branch br : this.branchF4Bean.getBranch_list()) {
			if (br.getBranch_id() == selected.getBranch_id()) {
				this.selected.setCountry_id(br.getCountry_id());
				break;
			}
		}
		getSalaryService().createSalary(selected, parentPyramidId);
	}

	private void Update() {

	}

	public Salary Reset() {
		selected = null;
		return selected;
	}

	/***********************************/
	private SalaryService getSalaryService() {
		return (SalaryService) appContext.getContext().getBean("salaryService");
	}

	private SalaryDao getSalaryDao() {
		return (SalaryDao) appContext.getContext().getBean("salaryDao");
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;

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
	PositionF4 positionF4Bean;

	public PositionF4 getPositionF4Bean() {
		return positionF4Bean;
	}

	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4Bean;

	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}

	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}

	@ManagedProperty("#{bukrsF4Bean}")
	BukrsF4 bukrsF4Bean;

	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}

	public void setBukrsF4Bean(BukrsF4 bukrsF4Bean) {
		this.bukrsF4Bean = bukrsF4Bean;
	}

	/**************************************/

	private String mode;

	public void setMode(String mode) {
		this.mode = mode;
		initBean();
	}

	private void initBean() {
		if (mode.equals("list")) {
			// salaryModel = new SalaryModel(getSalaryDao());
			// salaryModel.getSearchModel().setCurrent(true);
		}
	}

	private SalaryModel salaryModel;

	public SalaryModel getSalaryModel() {
		return salaryModel;
	}

	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	public void Search() {
		selected = null;
	}

	private String bukrs;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public SearchClass searchModel = new SearchClass();

	public SearchClass getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchClass searchModel) {
		this.searchModel = searchModel;
	}

	public class SearchClass {
		private boolean isCurrent = true;
		String bukrs;
		Long branch_id;
		private String firstname;
		private String lastname;
		private Long position_id;
		private Long department_id;
		private Date begDateFrom;
		private Date begDateTo;
		
		

		public Date getBegDateFrom() {
			return begDateFrom;
		}

		public void setBegDateFrom(Date begDateFrom) {
			this.begDateFrom = begDateFrom;
		}

		public Date getBegDateTo() {
			return begDateTo;
		}

		public void setBegDateTo(Date begDateTo) {
			this.begDateTo = begDateTo;
		}

		public Long getDepartment_id() {
			return department_id;
		}

		public void setDepartment_id(Long department_id) {
			this.department_id = department_id;
		}

		public boolean isCurrent() {
			return isCurrent;
		}

		public void setCurrent(boolean isCurrent) {
			this.isCurrent = isCurrent;
		}

		public String getBukrs() {
			return bukrs;
		}

		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public Long getBranch_id() {
			return branch_id;
		}

		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}

		public Long getPosition_id() {
			return position_id;
		}

		public void setPosition_id(Long position_id) {
			this.position_id = position_id;
		}

		public String getPreparedCondition() {
			String cond = " s1.staff_id IS NOT NULL AND s1.staff_id != 0 ";

			// System.out.println("TEMP: " + bukrs + " => " + getPosition_id());
			if (!Validation.isEmptyString(getBukrs())) {
				cond += " AND s1.bukrs = '" + getBukrs() + "' ";
			} else {
				if (!userData.isSys_admin() && !userData.isMain()) {
					cond += " AND s1.bukrs = '" + userData.getBukrs() + "' ";
				}
			}

			if (!Validation.isEmptyLong(getBranch_id())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.branch_id = " + getBranch_id() + " ";
			} else if (!userData.isSys_admin() && !userData.isMain()) {
				if (userBranches.size() == 0) {
					cond += " AND s1.branch_id = -1 ";
				} else {
					String[] bIds = new String[userBranches.size()];
					for (int k = 0; k < userBranches.size(); k++) {
						bIds[k] = userBranches.get(k).getBranch_id().toString();
					}

					cond += String.format(" AND s1.branch_id IN(%s) ", "'" + String.join("','", bIds) + "'");
				}
			}

			if (!Validation.isEmptyLong(getPosition_id())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.position_id = " + getPosition_id() + " ";
			}

			if (!Validation.isEmptyString(getFirstname())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s2.firstname LIKE '%" + getFirstname() + "%' ";
			}

			if (!Validation.isEmptyString(getLastname())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s2.lastname LIKE '%" + getLastname() + "%' ";
			}

			if (!Validation.isEmptyLong(getDepartment_id())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " s1.department_id = " + getDepartment_id() + " ";
			}

			if (isCurrent && begDateFrom == null && begDateTo == null) {
				cond += " AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "' ";
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(begDateFrom != null){
				cond += " AND s1.beg_date >= '" + sdf.format(begDateFrom).toString() + "' ";
			}
			
			if(begDateTo != null){
				cond += " AND s1.beg_date <= '" + sdf.format(begDateTo).toString() + "' ";
			}

			System.out.println("CONDDD: " + cond);
			return cond.length() > 0 ? cond : " 1 = 1 ";
		}
	}
}