package general.beans;

import f4.BranchF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.BranchDao;
import general.tables.Branch;
import user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class BranchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6889112615799109795L;

	@PostConstruct
	public void init() {
		// if (!GeneralUtil.isAjaxRequest()) {
		// if (userIsNotAdminAndNotMain()) {
		// loadUserBranches();
		// }
		// }
	}

	private boolean userIsNotAdminAndNotMain() {
		return !userData.isSys_admin() && !userData.isMain();
	}

	private List<Branch> userBranches = new ArrayList<>();

	public List<Branch> getUserBranches(String bukrs) {
		if (userIsNotAdminAndNotMain()) {
			return userData.getUserBranches();
		}
		// System.out.println("TEST: " + bukrs);
		return branchF4Bean.getAllBranchByBukrs(bukrs);
	}

	private String bukrs = "";
	private List<Branch> items;

	public List<Branch> getItems() {
		if (this.bukrs.length() > 0) {
			return this.getItemsByBukrs(this.bukrs);
		}
		this.items = this.branchF4Bean.getBranch_list();
		return items;
	}

	public List<Branch> getItemsByBukrs(String bukrs) {
		List<Branch> out = new ArrayList<Branch>();
		for (Branch b : this.branchF4Bean.getBranch_list()) {
			if (b.getBukrs().equals(bukrs)) {
				out.add(b);
			}
		}
		return out;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getBranchLabel(Long branchId) {
		if (branchId == null || branchId == 0) {
			return "";
		}
		for (Branch b : this.branchF4Bean.getBranch_list()) {
			if (b.getBranch_id().longValue() == branchId) {
				return b.getText45();
			}
		}
		return "";
	}

	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 branchF4Bean;

	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
