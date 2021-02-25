package logistics;

import f4.BranchF4;
import f4.WerksF4;
import general.AppContext;
import general.dao.BranchDao;
import general.dao.WerksBranchDao;
import general.tables.Branch;
import general.tables.Werks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "lgHelperBean")
@ViewScoped
public class LgHelperBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5600760440886164985L;

	public List<Branch> getUserBranches(String bukrs) {
		if (!userData.isMain() && !userData.isSys_admin()) {
			prepareUserBranches();
			return userBranchesMap.get(userData.getUserid());
		}

		return branchF4.getAllBranchByBukrs(bukrs);
	}

	private Map<Long, List<Branch>> userBranchesMap = new HashMap<Long, List<Branch>>();

	private void prepareUserBranches() {
		if (userBranchesMap.get(userData.getUserid()) == null) {
			BranchDao d = appContext.getContext().getBean("branchDao",
					BranchDao.class);
			userBranchesMap.put(userData.getUserid(),
					d.findChilds(userData.getBranch_id()));
		}
	}

	public List<Werks> getUserWerks(String bukrs) {
		if (!userData.isMain() && !userData.isSys_admin()) {
			prepareUserWerks();
			return userWerksMap.get(userData.getUserid());
		}

		return werksF4.getByBukrs(bukrs);
	}

	Map<Long, List<Werks>> userWerksMap = new HashMap<Long, List<Werks>>();

	private void prepareUserWerks() {
		if (userWerksMap.get(userData.getUserid()) == null) {
			WerksBranchDao wbDao = appContext.getContext().getBean(
					"werksBranchDao", WerksBranchDao.class);
			userWerksMap.put(userData.getUserid(),
					wbDao.findAllWerksByBranch2(userData.getBranch_id()));
		}
	}

	@ManagedProperty(value = "#{branchF4Bean}")
	BranchF4 branchF4;

	public BranchF4 getBranchF4() {
		return branchF4;
	}

	public void setBranchF4(BranchF4 branchF4) {
		this.branchF4 = branchF4;
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
