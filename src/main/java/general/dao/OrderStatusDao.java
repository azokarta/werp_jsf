package general.dao;

import java.util.List;

import general.tables.OrderStatus;

public interface OrderStatusDao  extends GenericDao<OrderStatus>{
	public List<OrderStatus> findAll();
}
