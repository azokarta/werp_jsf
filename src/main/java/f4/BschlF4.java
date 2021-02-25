package f4;

import general.AppContext;
import general.dao.BschlDao;
import general.dao.DAOException;
import general.tables.Bschl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;  
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "bschlF4Bean")
@ApplicationScoped
public class BschlF4 {
	List<Bschl> bschl_list = new ArrayList<Bschl>(); 
	public List<Bschl> getBschl_list(){
		return bschl_list;
	}
	
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
			BschlDao bschlDao = (BschlDao) appContext.getContext().getBean("bschlDao");
			bschl_list = bschlDao.findAll();
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bschl F4 not loaded");
	    	throw new DAOException("Bschl F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			bschl_list = new ArrayList<Bschl>(); 
			BschlDao bschlDao = (BschlDao) appContext.getContext().getBean("bschlDao");
			bschl_list = bschlDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bschl F4 not loaded");
	    	throw new DAOException("Bschl F4 not loaded");
		}
	}
	
	public String getName(String a_bschl){
		if (a_bschl!=null)
		{
			for(Bschl wa_bschl:bschl_list)
			{
				if (wa_bschl.getBschl().equals(a_bschl))
				{
					return wa_bschl.getText45();
				}
			}
		}
		
		return "";
	}
}
