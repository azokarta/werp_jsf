package f4;
import general.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty;

import general.dao.CountryDao; 
import general.dao.DAOException;
import general.tables.Country;

@ManagedBean(name = "countryF4Bean")
@ApplicationScoped
public class CountryF4 {
	
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
	
	
	List<Country> country_list = new ArrayList<Country>();
	Map<Long,Country> l_country_map = new HashMap<Long,Country>();
	
	@PostConstruct
	public void init(){ 
		try
		{
			country_list = new ArrayList<Country>();
			l_country_map = new HashMap<Long,Country>();
			CountryDao countryDao = (CountryDao) appContext.getContext().getBean("countryDao");
			country_list = countryDao.findAll();
			
			Long key;
			for (Country wa_c:country_list)
			{
				key = wa_c.getCountry_id();
				l_country_map.put(key, wa_c);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Country F4 not loaded");
	    	throw new DAOException("Country F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			country_list = new ArrayList<Country>();
			l_country_map = new HashMap<Long,Country>();
			CountryDao countryDao = (CountryDao) appContext.getContext().getBean("countryDao");
			country_list = countryDao.findAll();
			
			Long key;
			for (Country wa_c:country_list)
			{
				key = wa_c.getCountry_id();
				l_country_map.put(key, wa_c);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Country F4 not loaded");
	    	throw new DAOException("Country F4 not loaded");
		}
	}

	public List<Country> getCountry_list(){
		return country_list;
	}
	
	public Map<Long, Country> getL_country_map() {
		return l_country_map;
	}
	
	public String getName(Long id){
		if(id != null){
			for(Country c:country_list){
				if(c.getCountry_id() == id){
					return c.getCountry();
				}
			}
		}
		return "";
	}

} 
