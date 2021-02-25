package general.dao;

import java.util.List;

import general.tables.Bschl; 
 

public interface BschlDao extends GenericDao<Bschl>{ 
	public List<Bschl> findAll();
}
