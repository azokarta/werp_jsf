package hr.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.SelectEvent;

import f4.BranchF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.HrDocDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.services.hr.HrDocService;
import general.tables.Branch;
import general.tables.HrDoc;
import general.tables.HrDocApprover;
import general.tables.HrDocItem;
import general.tables.HrDocumentRoute;
import general.tables.Salary;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "hrDocRecruitmentBean")
@ViewScoped
public class HrDocRecruitment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7265877041723482643L;
	private static final String MODE_CREATE = "create";
	private static final String MODE_UPDATE = "update";
	private static final String MODE_VIEW = "view";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
		}
	}

	private HrDoc document;

	public HrDoc getDocument() {
		return document;
	}

	public void setDocument(HrDoc document) {
		this.document = document;
	}

	private Long id;
	private String mode;

	public String getMode() {
		return mode;
	}

	public void onChangeBranch(AjaxBehaviorEvent e) {

	}

	public void setMode(String mode) {
		this.mode = mode;
		MessageProvider mp = new MessageProvider();
		if (!GeneralUtil.isAjaxRequest()) {
			prepareManagersMap();
			if (mode.equals("create")) {
				prepareUserBranches();
				document = new HrDoc();
				document.setTypeId(HrDoc.TYPE_RECRUITMENT);
				document.setCreator(userData.getStaff());
				if (!userData.isSys_admin() && !userData.isMain()) {
					document.setBukrs(userData.getBukrs());
				}
				setPageHeader(mp.getValue("hr.doc.creation") + " " + document.getDocTypeName());
				loadStaffList();
			} else if (mode.equals("update")) {
				prepareUserBranches();
				loadDocument();
				setPageHeader(mp.getValue("hr.doc.editing") + " " + document.getDocTypeName());
				loadStaffList();
			} else if (mode.equals("view")) {
				loadSalaryList();
				loadDocument();
				// loadItems();
				setPageHeader(mp.getValue("hr.doc.view") + " " + document.getDocTypeName() + ", № " + document.getId());
				try {
					actionBean.initBean(document, mode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	private void prepareUserBranches() {
		if (!userData.isMain() && !userData.isSys_admin()) {
			BranchDao d = (BranchDao) appContext.getContext().getBean("branchDao");
			userBranches = d.findChilds(userData.getBranch_id());
		}

	}

	private void loadDocument() {
		if (!Validation.isEmptyLong(id)) {
			HrDocDao d = (HrDocDao) appContext.getContext().getBean("hrDocDao");
			document = d.findWithDetail(id);
			if (MODE_UPDATE.equals(mode)) {
				for (HrDocApprover ap : document.getHrDocApprovers()) {
					directorId = ap.getStaffId();
				}
			}
		}
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private List<HrDocumentRoute> routeList = new LinkedList<HrDocumentRoute>();

	public List<HrDocumentRoute> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<HrDocumentRoute> routeList) {
		this.routeList = routeList;
	}

	private List<Salary> salaryList = new ArrayList<Salary>();

	public List<Salary> getSalaryList() {
		return salaryList;
	}

	private void loadSalaryList() {
		SalaryDao d = (SalaryDao) appContext.getContext().getBean("salaryDao");
		String cond = "";
		if (!userData.isSys_admin()) {
			cond = " s1.bukrs = " + userData.getBukrs();
		}
		if (mode.equals("view")) {
			cond += (cond.length() > 0 ? " AND " : "") + " s1.position_id IN(5,6,10,20,21,22,23,29,49,50,70,71) ";
		}
		salaryList = d.findAllCurrentWithStaff(cond);
	}

	private Map<String, Map<Long, List<Staff>>> managersMap = new HashMap<>();

	private void prepareManagersMap() {
		managersMap = new HashMap<>();
		SalaryDao d = (SalaryDao) appContext.getContext().getBean("salaryDao");
		List<Salary> l = d.findAllCurrentWithStaff(" s1.position_id = 3 ");
		for (Salary s : l) {
			if (s.getP_staff() != null) {
				Map<Long, List<Staff>> tempMap = new HashMap<>();
				List<Staff> temp = new ArrayList<>();
				if (managersMap.containsKey(s.getBukrs())) {
					tempMap = managersMap.get(s.getBukrs());
				}

				if (tempMap.containsKey(s.getBranch_id())) {
					temp = tempMap.get(s.getBranch_id());
				}

				temp.add(s.getP_staff());
				tempMap.put(s.getBranch_id(), temp);

				managersMap.put(s.getBukrs(), tempMap);
			}
		}
	}

	public List<Staff> getManagers() {
		if (Validation.isEmptyString(document.getBukrs())) {
			return new ArrayList<>();
		}

		if (Validation.isEmptyLong(document.getBranchId())) {
			return new ArrayList<>();
		}

		if (!managersMap.containsKey(document.getBukrs())) {
			return new ArrayList<>();
		}

		if (!managersMap.get(document.getBukrs()).containsKey(document.getBranchId())) {
			return new ArrayList<>();
		}

		return managersMap.get(document.getBukrs()).get(document.getBranchId());
	}

	public void onSelectStaff(SelectEvent e) {
		Staff stf = (Staff) e.getObject();
		if (stf != null) {
			HrDocItem item = new HrDocItem();
			item.setStaffId(stf.getStaff_id());
			item.setStaff(stf);
			boolean isExisted = false;
			if (document.getHrDocItems() != null) {
				if (document.getHrDocItems().contains(item)) {
					GeneralUtil.addErrorMessage("Сотрудник уже добавлен в список!");
					isExisted = true;
				}
			}

			if (!isExisted) {
				document.addHrDocItem(item);
				GeneralUtil.hideDialog("staffWidget");
			}
		} else {
			GeneralUtil.addErrorMessage("Ошибка!");
		}
	}

	private List<Staff> staffList = new ArrayList<Staff>();

	public List<Staff> getStaffList() {
		return staffList;
	}

	private void loadStaffList() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		String cond = ""; // "staff_id NOT IN(SELECT sl.staff_id FROM Salary sl)
							// ";
		staffList = d.findAll(cond);
	}

	public void addItemRow() {
		if (document != null) {
			document.addHrDocItem(new HrDocItem());
		}
	}

	public void removeItemRow(HrDocItem o) {
		document.getHrDocItems().remove(o);
	}

	private void addDirectorToApprover() {
		document.setHrDocApprovers(new ArrayList<>());
		if (!Validation.isEmptyLong(directorId)) {
			UserDao userDao = appContext.getContext().getBean("userDao", UserDao.class);
			List<general.tables.User> uList = userDao.findAll(" staff_id = " + directorId);
			StaffDao stfDao = (StaffDao) appContext.getContext().getBean("staffDao");
			Staff stf = stfDao.find(directorId);
			if (uList.size() > 0 && stf != null) {
				HrDocApprover ap = new HrDocApprover();
				ap.setTitle(stf.getLF());
				ap.setUserId(uList.get(0).getUser_id());
				ap.setStatusId(HrDocumentRoute.STATUS_NO_ACTION);
				ap.setHrDoc(document);
				ap.setStaffId(directorId);
				ap.setPositionId(10L);
				document.addHrDocApprover(ap);
			}
		}
	}

	public void Save() {
		try {
			addDirectorToApprover();
			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/hr/doc/View.xhtml?id=" + document.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		HrDocService hrService = (HrDocService) appContext.getContext().getBean("hrDocService");
		hrService.create(document, userData);
	}

	private void Update() {
		HrDocService hrService = (HrDocService) appContext.getContext().getBean("hrDocService");
		hrService.update(document, userData);
	}

	private Long directorId;

	public Long getDirectorId() {
		return directorId;
	}

	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}

	private List<Staff> directorList = new ArrayList<>();

	public List<Staff> getDirectorList() {
		directorList = new ArrayList<>();
		if (!Validation.isEmptyLong(document.getBranchId())) {
			Branch br = branchF4.getL_branch_map().get(document.getBranchId());
			if (br != null) {
				String cond = "";
				if (br.getType() == 4L) {
					BranchDao brDao = appContext.getContext().getBean("branchDao", BranchDao.class);
					List<Branch> brList = brDao.findChilds(br.getBranch_id());
					if (brList.size() > 0) {
						String[] ids = new String[brList.size()];
						for (int k = 0; k < brList.size(); k++) {
							ids[k] = brList.get(k).getBranch_id().toString();
						}

						cond = String.format(" s1.branch_id IN(%s) ", String.join(",", ids));
					}
				} else if (br.getType() == 3L) {
					if (br.getBusiness_area_id() == 5 && !Validation.isEmptyLong(br.getParent_branch_id())) {// Сервис
						BranchDao brDao = appContext.getContext().getBean("branchDao", BranchDao.class);
						List<Branch> brList = brDao.findChilds(br.getParent_branch_id());
						if (brList.size() > 0) {
							String[] ids = new String[brList.size()];
							for (int k = 0; k < brList.size(); k++) {
								ids[k] = brList.get(k).getBranch_id().toString();
							}

							cond = String.format(" s1.branch_id IN(%s) ", String.join(",", ids));
						}
					} else {
						cond = " s1.branch_id = " + br.getBranch_id();
					}
				}

				if (cond.length() > 0) {
					cond += " AND s1.position_id = 10 ";
					SalaryDao salDao = appContext.getContext().getBean("salaryDao", SalaryDao.class);
					List<Salary> l = salDao.findAllCurrentWithStaff(cond); // Директор
					for (Salary sal : l) {
						if (sal.getP_staff() != null) {
							directorList.add(sal.getP_staff());
						}
					}
				}
			}
		}
		return directorList;
	}

	public void setDirectorList(List<Staff> directorList) {
		this.directorList = directorList;
	}

	Map<Long, List<Staff>> directorsMap = new HashMap<>();

	@ManagedProperty("#{hrDocActionBean}")
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
