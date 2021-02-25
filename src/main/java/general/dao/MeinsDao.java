package general.dao;

import java.util.List;

import general.tables.Meins;
 

public interface MeinsDao extends GenericDao<Meins>{ 
	public List<Meins> findAll();
	public int updateDynamicSingle(Long meins);
}
