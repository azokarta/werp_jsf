package general.dao; 

import java.util.List;
import general.tables.Demonstration;
import general.tables.search.DemoSearch;

public interface DemoDao extends GenericDao<Demonstration> {
    
	public List<Demonstration> findAll(String condition);
	
	
	public List<Demonstration> findAllLazy(String cond, int first, int pageSize);
	
	public List<Demonstration> findAllByCriteriaLazy(DemoSearch searchModel,String orderByField,String orderByDir,int first, int pageSize);
	public int getRowCountByCriteria(DemoSearch searchModel);
	
	public int getRowCount(String cond);
}
