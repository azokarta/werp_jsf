package general.dao;

import java.util.List;

import general.tables.Order;

public interface OrderDao  extends GenericDao<Order>{
	public List<Order> findAll();
	public List<Order> findAll(String condition);	
	public void insert();
	public void updateByQuery(String query);
}
