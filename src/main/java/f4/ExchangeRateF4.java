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
 




import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.tables.ExchangeRate;

@ManagedBean(name = "exchangeRateF4Bean")
@ApplicationScoped
public class ExchangeRateF4 {
	
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
	
	List<ExchangeRate> exchageRate_list = new ArrayList<ExchangeRate>(); 
	Map<String,ExchangeRate> l_er_map_internal = new HashMap<String,ExchangeRate>();
	Map<String,ExchangeRate> l_er_map_national = new HashMap<String,ExchangeRate>();
	@PostConstruct
	public void init(){ 
		try
		{
			exchageRate_list = new ArrayList<ExchangeRate>(); 
			l_er_map_internal = new HashMap<String,ExchangeRate>();
			l_er_map_national = new HashMap<String,ExchangeRate>();
			ExchangeRateDao exrateDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao");
			exchageRate_list = exrateDao.getLastCurrencyRates();
			String key = "";
			for (ExchangeRate wa_er:exchageRate_list)
			{
				if (wa_er.getType()==2 && (wa_er.getBukrs()==null || wa_er.getBukrs().length()<1))
				{
					System.out.println("Exchange rate F4 internal company is null");
					throw new DAOException("Exchange rate F4 internal company is null");
				}
				
				if (wa_er.getType()==1)
				{
					key = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();					
					l_er_map_national.put(key, wa_er);
				}
				else if (wa_er.getType()==2)
				{
					key = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency()+wa_er.getBukrs();
					l_er_map_internal.put(key, wa_er);
				}
				
			}			
		}
	    catch(Exception ex)
		{
	    	exchageRate_list = new ArrayList<ExchangeRate>(); 
			l_er_map_internal = new HashMap<String,ExchangeRate>();
			l_er_map_national = new HashMap<String,ExchangeRate>();
	    	System.out.println("Exchange rate F4 not loaded");
	    	throw new DAOException("Exchange rate F4 not loaded");
		}		
	}
	
	public void updateF4()
	{
		try
		{
			exchageRate_list = new ArrayList<ExchangeRate>(); 
			l_er_map_internal = new HashMap<String,ExchangeRate>();
			l_er_map_national = new HashMap<String,ExchangeRate>();
			ExchangeRateDao exrateDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao");
			exchageRate_list = exrateDao.getLastCurrencyRates();
			String key = "";
			for (ExchangeRate wa_er:exchageRate_list)
			{
				if (wa_er.getType()==2 && (wa_er.getBukrs()==null || wa_er.getBukrs().length()<1))
				{
					System.out.println("Exchange rate F4 internal company is null");
					throw new DAOException("Exchange rate F4 internal company is null");
				}
				if (wa_er.getType()==1)
				{
					key = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();					
					l_er_map_national.put(key, wa_er);
				}
				else if (wa_er.getType()==2)
				{
					key = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency()+wa_er.getBukrs();
					l_er_map_internal.put(key, wa_er);
				}
				
			}			
		}
	    catch(Exception ex)
		{
	    	exchageRate_list = new ArrayList<ExchangeRate>(); 
			l_er_map_internal = new HashMap<String,ExchangeRate>();
			l_er_map_national = new HashMap<String,ExchangeRate>();
	    	System.out.println("Exchange rate F4 not loaded");
	    	throw new DAOException("Exchange rate F4 not loaded");
		}
	}
	
	public List<ExchangeRate> getExchageRate_list(){
		return exchageRate_list;
	}
	
	
	public Map<String, ExchangeRate> getL_er_map_internal() {
		return l_er_map_internal;
	}

	public Map<String, ExchangeRate> getL_er_map_national() {
		return l_er_map_national;
	}

	public void clearAndAddAll(List<ExchangeRate> al_er){		
		exchageRate_list.clear();
		exchageRate_list.addAll(al_er);
		l_er_map_national.clear();
		l_er_map_internal.clear();
		String key = "";
		for (ExchangeRate wa_er:al_er)
		{
			if (wa_er.getType()==1)
			{
				key = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();					
				l_er_map_national.put(key, wa_er);
			}
			else if (wa_er.getType()==2)
			{
				key = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency()+wa_er.getBukrs();
				l_er_map_internal.put(key, wa_er);
			}
		}
		
	}
} 
