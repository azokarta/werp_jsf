package dms.demo;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.DemoDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.services.DemoService;
import general.tables.Branch;
import general.tables.Demonstration;
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
import javax.faces.event.AjaxBehaviorEvent;

import datamodels.DemoModel;
import f4.BranchF4;
import user.User;

@ManagedBean(name = "dmsdListBean")
@ViewScoped
public class DmsdList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Long ISLAMBEK_STAFF_ID = 5262L;
	private static final Long NURMAN_STAFF_ID = 4830L;
	private static final Long BAHTYBAY_STAFF_ID = 4620L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			prepareStfMap();
			setPageHeader();
			prepareUserBranches();

			demoModel = new DemoModel(appContext.getContext().getBean("demoDao", DemoDao.class), userData);
			demoModel.setUserBranches(getUserBranches());
			demoModel.getSearchModel().setBranchIds(getUserBranchIds());
			demoModel.setStfMap(stfMap);
			if (!userData.isSys_admin() && !userData.isMain()) {
				demoModel.getSearchModel().setBukrs(userData.getBukrs());
			}
		}
	}

	DemoModel demoModel;

	public DemoModel getDemoModel() {
		return demoModel;
	}

	public void setDemoModel(DemoModel demoModel) {
		this.demoModel = demoModel;
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	private void prepareUserBranches() {
		userBranches = new ArrayList<Branch>();

		if (!userData.isSys_admin() && !userData.isMain()) {
			BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");

			List<Branch> l = bd.findChilds(userData.getBranch_id());
			for (Branch br : l) {
				// if (br.getType() == Branch.TYPE_BRANCH) {
				userBranches.add(br);
				// }
			}
		}

		if (userData.getStaff().getStaff_id().equals(ISLAMBEK_STAFF_ID)) {
			for (Branch br : branchF4.getBranch_list()) {
				if (br.getBranch_id().equals(36L)) {// CHM CEB1
					userBranches.add(br);
				} else if (br.getBranch_id().equals(35L) || br.getBranch_id().equals(71L)) {// CHM
																							// ROB1
																							// &&
																							// CHM
																							// SERV
					userBranches.add(br);
				} else if (br.getBranch_id().equals(41L) || br.getBranch_id().equals(42L)
						|| br.getBranch_id().equals(72L)) {// TAR
					// ROB1
					// &&
					// TAR
					// CEB1 && TAR SERVICE
					userBranches.add(br);
				} else if (br.getBranch_id().equals(43L) || br.getBranch_id().equals(44L)
						|| br.getBranch_id().equals(65L)) {// OSK
					// ROB1
					// &&
					// OSK
					// CEB1 && OSK SERV
					userBranches.add(br);
				} else if (br.getBranch_id().equals(39L) || br.getBranch_id().equals(40L)
						|| br.getBranch_id().equals(64L)) {// KRG
					// ROB1
					// &&
					// KRG
					// CEB1 && KRG SERV
					userBranches.add(br);
				}
			}
		} else if (userData.getStaff().getStaff_id().equals(BAHTYBAY_STAFF_ID)) {
			for (Branch br : branchF4.getBranch_list()) {
				if (br.getBranch_id().equals(51L) || br.getBranch_id().equals(52L) || br.getBranch_id().equals(70L)) {// KZO
					// ROB1
					// &&
					// KZO
					// CEB1 && kZO SERV
					userBranches.add(br);
				}
			}
		}
	}

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	private List<String> getUserBranchIds() {
		List<String> out = new ArrayList<>();
		for (Branch br : userBranches) {
			out.add(br.getBranch_id().toString());
		}

		if (!userData.isMain() && !userData.isSys_admin() && Validation.isEmptyCollection(out)) {
			out.add("-1");
		}

		return out;
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();
	private Map<Long, Staff> userStfMap = new HashMap<Long, Staff>();

	private void prepareStfMap() {
		StaffDao sd = (StaffDao) appContext.getContext().getBean("staffDao");
		List<Staff> l = sd.findAll("");
		for (Staff s : l) {
			stfMap.put(s.getStaff_id(), s);
		}

		UserDao ud = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> uList = ud.findAll(" staff_id IS NOT NULL AND staff_id > 0 ");
		for (general.tables.User u : uList) {
			Staff stf = stfMap.get(u.getStaff_id());
			if (stf != null) {
				userStfMap.put(u.getUser_id(), stf);
			}
		}
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		this.pageHeader = "Список демонстрации";
	}

	private Demonstration selected;

	public void prepareCreate() {
		selected = new Demonstration();
		if (!userData.isSys_admin() && !userData.isMain()) {
			selected.setBukrs(userData.getBukrs());
		}
		isCreate = true;
	}

	public Demonstration getSelected() {
		return selected;
	}

	public void setSelected(Demonstration selected) {
		this.selected = selected;
		loadDealers();
		loadSecretars();
		isCreate = false;
	}

	boolean isCreate = false;

	public void Save() {
		try {
			DemoService s = (DemoService) appContext.getContext().getBean("demoService");
			if (isCreate) {
				s.create(selected, userData.getUserid());
				String bukrs = selected.getBukrs();
				Long branchId = selected.getBranchId();
				Long demoSecId = selected.getDemosecId();
				Long dealerBranchId = selected.getDealerBranchId();

				selected = new Demonstration();
				selected.setBukrs(bukrs);
				selected.setBranchId(branchId);
				selected.setDemosecId(demoSecId);
				selected.setDealerBranchId(dealerBranchId);
				// GeneralUtil.updateFormElement("DemoCreateDlg:DemoCreateForm");
			} else {
				s.update(selected, userData.getUserid());
				GeneralUtil.hideDialog("DemoCreateDlg");
			}

			GeneralUtil.addSuccessMessage("Создано успешно!");

			//

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Reset() {
		selected = null;
	}

	private List<Staff> dealers = new ArrayList<Staff>();
	private List<Staff> secretars = new ArrayList<Staff>();

	public List<Staff> getSecretars() {
		return secretars;
	}

	public List<Staff> getDealers() {
		return dealers;
	}

	public void onChangeBranch(AjaxBehaviorEvent e) {
		loadSecretars();
	}

	public void onChangeDealerBranch(AjaxBehaviorEvent e) {
		loadDealers();
	}

	public void loadSecretars() {
		secretars = new ArrayList<Staff>();
		if (selected != null && !Validation.isEmptyLong(selected.getBranchId())) {
			SalaryDao d = (SalaryDao) appContext.getContext().getBean("salaryDao");
			List<Salary> l = d.findAllCurrentWithStaff(
					"s1.branch_id = " + selected.getBranchId() + " AND s1.position_id IN(8,75) ");
			// System.out.println("SAL SIZE: " + l.size());
			for (Salary s : l) {
				secretars.add(s.getP_staff());
			}
		}
	}

	public void loadDealers() {
		dealers = new ArrayList<Staff>();
		if (selected != null && !Validation.isEmptyLong(selected.getDealerBranchId())) {
			SalaryDao d = (SalaryDao) appContext.getContext().getBean("salaryDao");
			// List<Salary> l = d.findAllCurrentWithStaff("s1.branch_id = "
			// + selected.getBranchId()
			// + " AND s1.position_id IN(3,4,10,12,67) ");
			List<Salary> l = d.findAllCurrentWithStaff("s1.branch_id = " + selected.getDealerBranchId());
			// System.out.println("SAL SIZE: " + l.size());
			Map<Long, Integer> tempStfMap = new HashMap<>();
			for (Salary s : l) {
				if (!tempStfMap.containsKey(s.getP_staff().getStaff_id())) {
					dealers.add(s.getP_staff());
					tempStfMap.put(s.getP_staff().getStaff_id(), 1);
				}
			}
		}

	}

	public Map<Integer, String> getStatuses() {
		return Demonstration.getStatuses();
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 branchF4;

	public BranchF4 getBranchF4() {
		return branchF4;
	}

	public void setBranchF4(BranchF4 branchF4) {
		this.branchF4 = branchF4;
	}

}
