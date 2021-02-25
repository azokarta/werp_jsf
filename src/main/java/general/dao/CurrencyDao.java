package general.dao;

import java.util.List;
 
import general.tables.Currency;

public interface CurrencyDao extends GenericDao<Currency>{
	public List<Currency> findAll(String s);
	public List<Currency> findAll();
	public Currency findByCurrency(String s);
	public List<Currency> findAllWithCountries();
}
