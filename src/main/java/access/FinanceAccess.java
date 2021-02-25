package access;


import general.AppContext;
import general.dao.DAOException;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;


@ManagedBean(name = "finAccessBean", eager = true)
@ViewScoped
public class FinanceAccess implements Serializable{
	private static final long serialVersionUID = 1L;
	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2; 
		
	//***************************Application Context********************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//******************************************************************
	//**********************Getter Setter for sessionData**********************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	
	public boolean modifyRateBoolean() {
		try
		{
			if (userData.isSys_admin())
			{
				return false;
			}			
			else if (userData.getL_ra_map()!=null && userData.getL_ra_map().get(115L)!=null && userData.getL_ra_map().get(115L).size()>0)
			{
				return false;
			}
			
			return true;
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			return false;
			//toMainPage();
		}
		
		
	}
	// *****************************************************************************	

	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    } 	
	
	
	//*****************************************************************************************************

}
