package general.dao;

import java.util.List;

import general.tables.OrderMatnr;

public interface OrderMatnrDao extends GenericDao<OrderMatnr>{
	public List<OrderMatnr> findAll(String cond);
	
	public List<OrderMatnr> findAllByOrderId(Long orderId);
	
	public List<OrderMatnr> findAllNotPostingByOrderId(Long orderId);
}
