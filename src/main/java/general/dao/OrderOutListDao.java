package general.dao;

import java.util.List;

import general.tables.OrderOutList;

public interface OrderOutListDao  extends GenericDao<OrderOutList>{
	public List<OrderOutList> findAll(String condition);
	
}
