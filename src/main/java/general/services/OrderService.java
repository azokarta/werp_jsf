package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import user.User;
import general.dao.DAOException;
import general.tables.Order;
import general.tables.OrderMatnr;
import general.tables.RequestOut;

public interface OrderService {

	@Transactional
	void create( Order order, List<OrderMatnr> omList,List<RequestOut> parentDocs, User userData) throws DAOException;
	
	@Transactional
	void update(Order o,List<OrderMatnr> omList,List<RequestOut> parentDocs,User userData) throws DAOException;
}
