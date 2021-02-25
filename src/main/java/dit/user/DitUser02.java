package dit.user;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.services.UserService;
import general.tables.User;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "ditUser02Bean")
@ViewScoped
public class DitUser02 {
	// ***************************Application Context********************
	private final static String transaction_code = "DITUSER02";
	private final static Long transaction_id = (long) 15;
	
	
	@PostConstruct
	public void init(){
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		PermissionController.canRead(userData, transaction_id);
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	private String username;
	private User user;
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void toSearch()
	{
		try
		{
			PermissionController.canRead(userData, transaction_id);
			if(username.isEmpty())
			{
				user = new User();
				throw new DAOException("Please, enter username");
			}
			UserService userServ = (UserService) appContext.getContext()
					.getBean("userService");
			user = userServ.searchByUsername(username);
		}
		catch(DAOException e)
		{
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void toUpdate(){
		try {
			PermissionController.canWrite(userData, transaction_id);
			UserService userServ = (UserService) appContext.getContext().getBean("userService");	
			//userServ.updateUser(user);
			user = new User();
			GeneralUtil.addSuccessMessage("User saved successfully");
		}
		catch (DAOException ex)
		{
			GeneralUtil.addErrorMessage(ex.getMessage()); 
		}
	}

	private String username_search;
	public String getUsername_search() {
		return username_search;
	}

	public void setUsername_search(String username_search) {
		this.username_search = username_search;
	}

	//***************************User session***************************
	@ManagedProperty(value="#{userinfo}")
	private user.User userData;
	public user.User getUserData() {
	  return userData;
	}

	public void setUserData(user.User userData) {
	  this.userData = userData;
	}	
}
