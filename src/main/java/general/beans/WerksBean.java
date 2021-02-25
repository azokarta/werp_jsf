package general.beans;

import f4.WerksF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.BranchDao;
import general.dao.WerksBranchDao;
import general.tables.Branch;
import general.tables.Werks;
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
public class WerksBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3716812346528452382L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			if (!userData.isSys_admin() && !userData.isMain()) {
				loadUserWerks();
			}
		}
	}

	private List<Werks> userWerks = new ArrayList<>();

	private void loadUserWerks() {
		userWerks = new ArrayList<>();
		if (!Validation.isEmptyLong(userData.getBranch_id())) {
			WerksBranchDao d = appContext.getContext().getBean("werksBranchDao", WerksBranchDao.class);
			userWerks = d.findAllWerksByBranch2(userData.getBranch_id());
			if (userWerks == null || userWerks.size() == 0) {
				BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
				Branch userBranch = brDao.find(userData.getBranch_id());
				// Провверка филиала на Сервис(5) ИЛИ Себилон (2) ИЛИ Робоклин
				// (1) //Доступ для директоров
				if (userBranch != null && userBranch.getBusiness_area_id() != null) {
					userWerks = d.findAllWerksByBranch2(userBranch.getParent_branch_id());
				}
			}
		}
	}

	public List<Werks> getUserWerks(String bukrs) {
		if (userIsNotAdminAndNotMain()) {
			List<Werks> out = new ArrayList<>();
			if (Validation.isEmptyString(bukrs)) {
				return out;
			}

			for (Werks w : userWerks) {
				if (bukrs.equals(w.getBukrs())) {
					out.add(w);
				}
			}
			return out;
		}
		return werksF4.getByBukrs(bukrs);
	}

	private boolean userIsNotAdminAndNotMain() {
		return !userData.isSys_admin() && !userData.isMain();
	}

	@ManagedProperty(value = "#{werksF4Bean}")
	private WerksF4 werksF4;

	public WerksF4 getWerksF4() {
		return werksF4;
	}

	public void setWerksF4(WerksF4 werksF4) {
		this.werksF4 = werksF4;
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
