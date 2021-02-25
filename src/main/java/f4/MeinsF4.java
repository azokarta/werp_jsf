package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.MeinsDao;
import general.tables.Meins;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;  
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "meinsF4Bean")
@ApplicationScoped
public class MeinsF4 {
	List<Meins> meins_list = new ArrayList<Meins>(); 
	public List<Meins> getMeins_list(){
		return meins_list;
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
			MeinsDao meinsDao = (MeinsDao) appContext.getContext().getBean("meinsDao");
			meins_list = meinsDao.findAll();
		}
	    catch(Exception ex)
		{
	    	System.out.println("Meins F4 not loaded");
	    	throw new DAOException("Meins F4 not loaded");
		}
	}
	
	public String getName(Long id){
		for(Meins m:meins_list){
			if(m.getMeins().equals(id)){
				return m.getText45();
			}
		}
		return null;
	}
	
	
	public void updateF4()
	{
		try
		{
			meins_list = new ArrayList<Meins>(); 
			MeinsDao meinsDao = (MeinsDao) appContext.getContext().getBean("meinsDao");
			meins_list = meinsDao.findAll();	
		}
	    catch(Exception ex)
		{
	    	System.out.println("Meins F4 not loaded");
	    	throw new DAOException("Meins F4 not loaded");
		}
	}
}
