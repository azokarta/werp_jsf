package user;

import general.AppContext;
import general.GeneralUtil;
import general.PasswordHelper;
import general.SystemLocale;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.EventLogDao;
import general.dao.Role_actionDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.services.EventLogService;
import general.tables.Branch;
import general.tables.EventLog;
import general.tables.Role_action;
import general.tables.Salary;
import general.tables.Staff;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

@ManagedBean(name = "loginManagerBean", eager = true)
@ViewScoped
public class LoginManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ******************************************************************
	// ************************SystemLocale******************************
	@ManagedProperty(value = "#{systemLocale}")
	private SystemLocale systemLocale;

	public SystemLocale getSystemLocale() {
		return systemLocale;
	}

	public void setSystemLocale(SystemLocale systemLocale) {
		this.systemLocale = systemLocale;
	}

	// ******************************************************************
	// ***********************ProgramManager*****************************
	@ManagedProperty(value = "#{programManagerBean}")
	private ProgramManager programManager;

	public ProgramManager getProgramManager() {
		return programManager;
	}

	public void setProgramManager(ProgramManager programManager) {
		this.programManager = programManager;
	}

	// ******************************************************************
	// ******************************************************************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************

	private String username;
	private String password;
	private String u_language;// = "ru";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getU_language() {
		return u_language;
	}

	public void setU_language(String u_language) {
		this.u_language = u_language;
	}

	public void validate() throws IOException {
		if (Validation.CheckForWhiteSpacesString(username) == true) {
			addMessage("Error:", "Username contains spaces");
			resetUser();
			throw new IOException("Username contains spaces");
		}
		if (Validation.CheckForWhiteSpacesString(password) == true) {
			addMessage("Error:", "Password contains spaces");
			resetUser();
			throw new IOException("Password contains spaces");
		}

	}

	public void resetUser() {
		password = null;
		username = null;
		userData.setUsername(null);
		userData.setUserid(0L);
		userData.setBukrs(null);
		userData.setBranch_id(0L);
		userData.setPassword(null);
	}

	private Staff getLoadedStaff(Long staffId) {
		Staff stf = null;
		if (!Validation.isEmptyLong(staffId)) {
			StaffDao stfDao = (StaffDao) appContext.getContext().getBean("staffDao");
			stf = stfDao.find(staffId);
		}
		return stf;
	}

	private void addEvent(Staff stf) {
		EventLogService elService = (EventLogService) appContext.getContext().getBean("eventLogService");
		elService.create(new EventLog(userData.getBukrs(), EventLog.TYPE_WARNING, "Вход в систему",
				stf == null ? 0 : stf.getStaff_id()));
	}

	public void login() throws Exception {
		try {

			
			UserDao ud = (UserDao) appContext.getContext().getBean("userDao");
			general.tables.User user = ud.loadUserByUsername(username);
			if (user == null) {
				throw new DAOException("Incorrect login or password");
			}
			if (!user.getHash().isEmpty() && user.getHash() != null
					&& user.getHash().equals(PasswordHelper.getSha256(password + user.getSalt()))) {
				if (user.getIs_active() == 0) {
					throw new DAOException("User is blocked");
				}
				Staff userStaff = getLoadedStaff(user.getStaff_id());
				if (userStaff == null) {
					throw new DAOException("Employee not found");
				}

				SalaryDao salDao = appContext.getContext().getBean("salaryDao", SalaryDao.class);
				List<Salary> salList = salDao.findAllCurrent(" staff_id = " + userStaff.getStaff_id());
				if (salList == null || salList.size() == 0) {
					throw new DAOException("No access");
				}
				// User userData = new User();
				userData.setUsername(user.getUsername());
				userData.setUserid(user.getUser_id());
				userData.setBukrs(user.getBukrs());
				userData.setBranch_id(Long.valueOf(user.getBranch_id()));
				userData.setPassword(null);
				userData.setU_language(u_language);
				systemLocale.setCurrentLocale(u_language);
				if (user.getIs_root() == 1) {
					userData.setMain(true);
				}
				userData.setIs_agree(user.getIs_agree());
				// if (user.getRole_id() == 1) {
				// userData.setSys_admin(true);
				// }

				List<Role_action> roleActions;
				Role_actionDao raDao = (Role_actionDao) appContext.getContext().getBean("role_actionDao");
				roleActions = raDao.getUserRoleActions(user.getUser_id());
				for (Role_action ra : roleActions) {
					if (ra.getRole_id() == 1) {
						userData.setSys_admin(true);
						break;
					}
				}
				userData.setUserRoleActions(roleActions);
				userData.transactionIds = new Long[roleActions.size()];
				int i = 0;
				for (Role_action ra : roleActions) {
					userData.transactionIds[i] = ra.getTransaction_id();
					i++;
					List<Role_action> wal_ra = new ArrayList<>();
					wal_ra = userData.getL_ra_map().get(ra.getRole_id());
					if (wal_ra != null && wal_ra.size() > 0) {
						wal_ra.add(ra);
					} else if (wal_ra != null || wal_ra == null) {
						wal_ra = new ArrayList<>();
						wal_ra.add(ra);
						userData.getL_ra_map().put(ra.getRole_id(), wal_ra);
					}

				}
				userData.setLogged_in(true);

				// System.out.println(userData.getU_language());
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				HttpServletRequest request = (HttpServletRequest) context.getRequest();
				request.getSession().setAttribute("user", userData);
				FacesContext fCtx = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
				String sessionId = session.getId();
				String text = "";
				// programManager.addNewSessionUser(userData, sessionId);
				

				InetAddress ip = InetAddress.getLocalHost();

				
//				if (ip.getHostAddress().equals("192.168.0.20")){
//					if (u_language.equals("en")) {text = "Test";}
//					else if (u_language.equals("tr")) {text = "Çalışma veritabanı";}
//					else {text = "Тестовая база";}	
//					
//				 }
//				else if (ip.getHostAddress().equals("192.168.0.15")){
//					if (u_language.equals("en")) {text = "Production";}
//					else if (u_language.equals("tr")) {text = "Çalışma baz";}
//					else {text = "Рабочая база";}		
//					
//					
//					
//				}else
//				{
//					text = "Unknown stage";
//				}
				
//				if (u_language.equals("en")) {text = "Test";}
//				else if (u_language.equals("tr")) {text = "Çalışma veritabanı";}
//				else {text = "Тестовая база";}	
//				userData.setBaza(text);
//				userData.setConreqpath("WERP");
//				userData.setLinkToReact("http://192.168.0.20:23040");
				
				
				if (u_language.equals("en")) {text = "Production";}
				else if (u_language.equals("tr")) {text = "Çalışma baz";}
				else {text = "Рабочая база";}			
				userData.setBaza(text);
				userData.setConreqpath("WERP");
				userData.setLinkToReact("http://192.168.0.15:23040");
				
				
				
				
				
				
				

//				if (context.getRequestContextPath().equals("/werp")) {
//					
//
//					String text = "";
//					if (u_language.equals("en")) {text = "Production";}
//					else if (u_language.equals("tr")) {text = "Çalışma baz";}
//					else {text = "Рабочая база";}				
//					
//					userData.setBaza(text);
//					userData.setConreqpath("WERP");
//					userData.setLinkToReact("http://192.168.0.15:23040");
//				} else {
//
//					String text = "";
//					if (u_language.equals("en")) {text = "Test";}
//					else if (u_language.equals("tr")) {text = "Çalışma veritabanı";}
//					else {text = "Тестовая база";}	
//					
//					userData.setBaza(text);
//					userData.setConreqpath("TEST");
//					userData.setLinkToReact("http://192.168.0.15:23041");
//				}

				userData.setStaff(userStaff);
				addEvent(userStaff);
				setUserBranches();
				context.redirect(context.getRequestContextPath() + "/general/mainPage.xhtml");
			} else {
				throw new DAOException("Incorrect login or password");
			}
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
			RequestContext.getCurrentInstance().update("form:messages");
		}
	}

	private void setUserBranches() {
		if (!Validation.isEmptyLong(userData.getBranch_id()) && !userData.isSys_admin() && !userData.isMain()) {
			BranchDao bd = (BranchDao) appContext.getContext().getBean("branchDao");
			userData.setUserBranches(bd.findChilds(userData.getBranch_id()));
		}
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void changeLocale() {
		systemLocale.setCurrentLocale(u_language);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}
}
