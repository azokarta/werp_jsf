package general.dao;

import java.util.List;

import general.tables.OrderList;

public interface OrderListDao  extends GenericDao<OrderList>{
	public List<OrderList> findAll();
	public List<OrderList> dynamicFindAll(String wclause);
	public void deleteByCondition(String condition);
	
	public List<OrderList> findAll(String cond);
}
