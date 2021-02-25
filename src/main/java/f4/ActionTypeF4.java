package f4;

import general.AppContext;
import general.dao.ActionTypeDao;
import general.dao.DAOException;
import general.tables.ActionType;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "actiontypeF4Bean")
@ApplicationScoped
public class ActionTypeF4 {
	List<ActionType> at_list = new ArrayList<ActionType>();
	//***************************Application Context***************************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;  
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//*************************************************************************
	 
	@PostConstruct
	public void init(){ 
		try
		{
			at_list  = new ArrayList<ActionType>();
			ActionTypeDao actionTypeDao = (ActionTypeDao) appContext.getContext().getBean("actionTypeDao");
			at_list = actionTypeDao.findAll();
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Action type F4 not loaded");
	    	throw new DAOException("Action type F4 not loaded");
		}
		
	}

	public List<ActionType> getAt_list() {
		return at_list;
	}
	
	public void updateF4()
	{	
		try
		{
			at_list  = new ArrayList<ActionType>();
			ActionTypeDao actionTypeDao = (ActionTypeDao) appContext.getContext().getBean("actionTypeDao");
			at_list = actionTypeDao.findAll();
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Action type F4 not loaded");
	    	throw new DAOException("Action type F4 not loaded");
		}
	}
}