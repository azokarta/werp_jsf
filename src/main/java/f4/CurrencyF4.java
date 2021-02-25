package f4;

import general.AppContext;
import general.Validation;
import general.dao.CurrencyDao;
import general.dao.DAOException;
import general.tables.Currency;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;


@ManagedBean(name = "currencyF4Bean")
@ApplicationScoped
public class CurrencyF4 {
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
	
	List<Currency> currency_list = new ArrayList<Currency>(); 
	@PostConstruct
	public void init(){
		try
		{
			CurrencyDao currencyDao = (CurrencyDao) appContext.getContext().getBean("currencyDao");
			currency_list = currencyDao.findAll();
		}
	    catch(Exception ex)
		{
	    	System.out.println("Currency F4 not loaded");
	    	throw new DAOException("Currency F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			currency_list = new ArrayList<Currency>();
			CurrencyDao currencyDao = (CurrencyDao) appContext.getContext().getBean("currencyDao");
			currency_list = currencyDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Currency F4 not loaded");
	    	throw new DAOException("Currency F4 not loaded");
		}
	}
	
	public List<Currency> getCurrency_list(){
		return currency_list;
	}
	
	public String getName(Long id){
		if(Validation.isEmptyLong(id)){
			return null;
		}
		
		for(Currency c:currency_list){
			if(c.getCurrency_id().equals(id)){
				return c.getCurrency();
			}
		}
		
		return null;
	}
}
