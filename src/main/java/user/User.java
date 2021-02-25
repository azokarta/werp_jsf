package user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import general.AppContext;
import general.Validation;
import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Role_action;
import general.tables.Staff;

@ManagedBean(name = "userinfo", eager = true)
@SessionScoped
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private Long userid;
	private String bukrs;
	private Long branch_id;
	private String password;
	private boolean logged_in = false;
	private String u_language;// = "ru";
	private boolean sys_admin = false;
	private boolean main = false;
	private int is_agree;
	private String linkToReact = "";
	
	public String getLinkToReact() {
		return linkToReact;
	}

	public void setLinkToReact(String linkToReact) {
		this.linkToReact = linkToReact;
	}

	private List<Branch> userBranches;

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	public String[] getUserBranchIdsAsStringArray() {
		String[] out = null;
		if (!Validation.isEmptyCollection(userBranches)) {
			out = new String[userBranches.size()];
			for (int k = 0; k < userBranches.size(); k++) {
				out[k] = userBranches.get(k).getBranch_id().toString();
			}
		} else {
			out = new String[1];
		}

		return out;
	}

	public List<String> getUserBranchIdsAsStringList() {
		List<String> out = new ArrayList<>();
		if (!Validation.isEmptyCollection(userBranches)) {
			for (Branch br : userBranches) {
				out.add(br.getBranch_id().toString());
			}
		}

		return out;
	}

	public int getIs_agree() {
		return is_agree;
	}

	public void setIs_agree(int is_agree) {
		this.is_agree = is_agree;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	Map<Long, List<Role_action>> l_ra_map = new HashMap<Long, List<Role_action>>();

	public Map<Long, List<Role_action>> getL_ra_map() {
		return l_ra_map;
	}

	public void setL_ra_map(Map<Long, List<Role_action>> l_ra_map) {
		this.l_ra_map = l_ra_map;
	}

	private List<Role_action> userRoleActions = new ArrayList<Role_action>();
	private Bkpf p_bkpf = new Bkpf();
	private String baza = "";
	private String conreqpath = "";
	private Staff staff;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String getStaffDisplayName() {
		if (staff == null) {
			return username;
		} else {
			return staff.getLF();
		}
	}

	public List<Role_action> getUserRoleActions() {
		return userRoleActions;
	}

	public void setUserRoleActions(List<Role_action> userRoleActions) {
		this.userRoleActions = userRoleActions;
	}

	List<UserRoleActions> ura_list = new ArrayList<UserRoleActions>();
	Long[] transactionIds;

	public Long[] getTransactionIds() {
		return transactionIds;
	}

	public void setLogged_in(boolean logged_in) {
		this.logged_in = logged_in;
	}

	public void setTransactionIds(Long[] transactionIds) {
		this.transactionIds = transactionIds;
	}

	public List<UserRoleActions> getUra_list() {
		return ura_list;
	}

	public void setUra_list(List<UserRoleActions> ura_list) {
		this.ura_list = ura_list;
	}

	public boolean isLogged_in() {
		return logged_in;
	}

	public String getUsername() {
		if (logged_in == false) {
			resetUser();
		}

		return username;
	}

	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
		if (bukrs != null && bukrs.equals("0001")) {
			main = true;
		}
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
		/*
		 * if (branch_id!=null && branch_id==1) { main = true; }
		 */
	}

	public boolean isMain() {
		return main;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void resetUser() {
		username = null;
		userid = 0L;
		bukrs = null;
		branch_id = new Long(0);
		password = null;
	}

	@PostConstruct
	public void init() {
		try {
			if (!FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
				resetUser();
				// return; // Skip ajax requests.
			}

		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
			// toMainPage();
		}

	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void logout() throws IOException {
		// ./general/mainPage?faces-redirect=true

		FacesContext fCtx = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
		String sessionId = session.getId();
		System.out.println(sessionId);
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		logged_in = false;
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		context.redirect(context.getRequestContextPath() + "/index.xhtml");
	}

	public String getU_language() {
		return u_language;
	}

	public void setU_language(String language) {
		this.u_language = language;
	}

	public boolean isSys_admin() {
		return sys_admin;
	}

	public void setSys_admin(boolean sys_admin) {
		this.sys_admin = sys_admin;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public Bkpf getP_bkpf() {
		return p_bkpf;
	}

	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}

	// New fi doc ---------------------------------------
	private boolean new_fi_doc = false;
	private Long trans_id = 0L;

	public boolean isNew_fi_doc() {
		return new_fi_doc;
	}

	public void setNew_fi_doc(boolean new_fi_doc) {
		this.new_fi_doc = new_fi_doc;
	}

	public Long getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(Long trans_id) {
		this.trans_id = trans_id;
	}
	// -------------------------------------------------

	public String getBaza() {
		return baza;
	}

	public void setBaza(String baza) {
		this.baza = baza;
	}

	public String getConreqpath() {
		return conreqpath;
	}

	public void setConreqpath(String conreqpath) {
		this.conreqpath = conreqpath;
	}
}
