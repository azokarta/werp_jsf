package general.dao;

import java.util.List;

import general.tables.OrderOut;

public interface OrderOutDao  extends GenericDao<OrderOut>{
	public List<OrderOut> findAll(String condition);
	
	void updateByCondition(String query) throws DAOException;
}
