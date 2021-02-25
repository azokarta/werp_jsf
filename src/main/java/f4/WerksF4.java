package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.WerksDao;
import general.tables.Werks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;  
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "werksF4Bean")
@ApplicationScoped
public class WerksF4 {
	List<Werks> werks_list = new ArrayList<Werks>(); 
	public List<Werks> getWerks_list(){
		return werks_list;
	} 
	Map<Long,Werks> l_werks_map = new HashMap<Long,Werks>();
	public Map<Long, Werks> getL_werks_map() {
		return l_werks_map;
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
			werks_list = new ArrayList<Werks>();
			l_werks_map = new HashMap<Long,Werks>();
			WerksDao werksDao = (WerksDao) appContext.getContext().getBean("werksDao");
			werks_list = werksDao.findAll();
			
			Long key;
			for (Werks wa_werks:werks_list)
			{
				key = wa_werks.getWerks();
				l_werks_map.put(key, wa_werks);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Werks F4 not loaded");
	    	throw new DAOException("Werks F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			werks_list = new ArrayList<Werks>();
			l_werks_map = new HashMap<Long,Werks>();
			WerksDao werksDao = (WerksDao) appContext.getContext().getBean("werksDao");
			werks_list = werksDao.findAll();
			
			Long key;
			for (Werks wa_werks:werks_list)
			{
				key = wa_werks.getWerks();
				l_werks_map.put(key, wa_werks);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Werks F4 not loaded");
	    	throw new DAOException("Werks F4 not loaded");
		}
	}
	
	public String getName(String a_werks)
	{
		if (a_werks!=null && a_werks.length()>0)
		{
			Long a_werks_long = Long.parseLong(a_werks);
			Werks wa_werks = l_werks_map.get(a_werks_long);
			if (wa_werks!=null)
			{
				return wa_werks.getText45();
			}
			else
			{
				return "";
			}
		}
		return "";
	}
	
	public List<Werks> getByBukrs(String a_bukrs)
	{
		List<Werks> l_werks = new ArrayList<Werks>();
		if (a_bukrs!=null && a_bukrs.length()>0)
		{
			for (Werks wa_werks:werks_list)
			{
				if (wa_werks.getBukrs().equals(a_bukrs))
				{
					l_werks.add(wa_werks);
				}
			}
		}
		return l_werks;
	}
}
