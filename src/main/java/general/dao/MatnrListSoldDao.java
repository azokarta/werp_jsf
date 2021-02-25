package general.dao;

import java.util.List;

import general.tables.MatnrListSold;

public interface MatnrListSoldDao extends GenericDao<MatnrListSold> {
	List<MatnrListSold> findAll(String condition);
	void updateByCondition(String q) throws DAOException;
	
	List<MatnrListSold> findAllGroupped(String condition);
}
