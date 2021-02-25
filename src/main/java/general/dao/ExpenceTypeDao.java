package general.dao;

import java.util.List;

import general.tables.ExpenceType;

public interface ExpenceTypeDao extends GenericDao<ExpenceType> {
    
	public List<ExpenceType> findAll();
}
