package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.OrderDao;
import general.dao.OrderStatusDao;
import general.tables.Order;
import general.tables.OrderStatus;

import org.springframework.stereotype.Component;
@Component("orderStatusDao")
public class OrderStatusDaoImpl extends GenericDaoImpl<OrderStatus> implements OrderStatusDao{
	public List<OrderStatus> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM OrderStatus b order by id"); 
    	//query.setMaxResults(20);  
    	List<OrderStatus> os =  query.getResultList();
    	return os;
    }
}
