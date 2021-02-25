package general.dao;
 

import java.sql.Date;
import java.util.List;
import java.util.Map;

import general.tables.ExchangeRate; 

public interface ExchangeRateDao extends GenericDao<ExchangeRate>{ 
	public List<ExchangeRate> getLastCurrencyRates();
	public List<ExchangeRate> getLastCurrencyRates(Date a_date);
	public Map<String,ExchangeRate> getLastCurrencyRatesDynamicMap(String a_dynamicWhere);
	public List<ExchangeRate> getLastCurrencyRatesDynamic(String a_dynamicWhere);
	public List<ExchangeRate> getCurrencyHistory(String a_dynamicWhere, Date a_from, Date a_to);
	public int countExRateForToday(String a_waers);
	public ExchangeRate getLastCurrencyRate(String a_waers,int a_type);
	public double getLastCurrencyRateInternal(String a_bukrs,String a_waers_osnovnoi,String a_waers_dop);
}
