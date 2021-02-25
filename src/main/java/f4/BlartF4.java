package f4;

import general.AppContext;
import general.dao.BlartDao;
import general.dao.DAOException;
import general.tables.Blart;
import general.tables.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "blartF4Bean")
@ApplicationScoped
public class BlartF4 {
	List<Blart> blart_list = new ArrayList<Blart>(); 
	public List<Blart> getBlart_list(){
		return blart_list;
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
	
	Map<String,Blart> l_blart_map = new HashMap<String,Blart>();
	
	@PostConstruct
	public void init(){
		try
		{
			BlartDao blartDao = (BlartDao) appContext.getContext().getBean("blartDao");
			blart_list = blartDao.findAll();
			
			for (Blart bl: blart_list) {
				l_blart_map.put(bl.getBlart(), bl);
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Blart F4 not loaded");
	    	throw new DAOException("Blart F4 not loaded");
		}
	}	
	
	public void updateF4()
	{
		try
		{
			blart_list = new ArrayList<Blart>(); 
			l_blart_map = new HashMap<String,Blart>();
			BlartDao blartDao = (BlartDao) appContext.getContext().getBean("blartDao");
			blart_list = blartDao.findAll();
			
			for (Blart bl: blart_list) {
				l_blart_map.put(bl.getBlart(), bl);
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Blart F4 not loaded");
	    	throw new DAOException("Blart F4 not loaded");
		}
	}

	public Map<String, Blart> getL_blart_map() {
		return l_blart_map;
	}
	
	public String getName(String a_blart)
	{
		
		if (a_blart!=null && a_blart.length()>0)
		{
			Blart wa_blart = l_blart_map.get(a_blart);
			if (wa_blart!=null)
			{
				return wa_blart.getText45();
			}
			else
			{
				return "";
			}
			
		}
		return "";
	}

}
