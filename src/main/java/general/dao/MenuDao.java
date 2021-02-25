package general.dao;

import java.util.List;
import java.util.Map;

import general.tables.Menu;
 

public interface MenuDao extends GenericDao<Menu>{ 
	public List<Menu> findAll(String cond);
	
	public Map<Long, List<Menu>> getMenuMappedByTransactionId();
}
