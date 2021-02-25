package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.OrderOut;
import general.tables.OrderOutList;

public interface OrderOutService {
	
	@Transactional
	void createOrder( OrderOut order, List<OrderOutList> orderList) throws DAOException;
	
	@Transactional
	void updateOrder( OrderOut order, List<OrderOutList> orderList) throws DAOException;
}
