package general.dao;

import java.util.List;
import general.tables.Werks;
 

public interface WerksDao extends GenericDao<Werks>{ 
	public List<Werks> findAll();
	
	public List<Werks> findAll(String cond);
}
