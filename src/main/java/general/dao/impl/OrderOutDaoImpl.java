package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.DAOException;
import general.dao.OrderOutDao;
import general.tables.OrderOut;

import org.springframework.stereotype.Component;
@Component("orderOutDao")
public class OrderOutDaoImpl extends GenericDaoImpl<OrderOut> implements OrderOutDao{

	@Override
	public List<OrderOut> findAll(String condition){ 
    	String s = "SELECT o FROM OrderOut o";
    	if(condition.length() > 0){
    		s += " WHERE " + condition;
    	}

    	Query query = this.em.createQuery(s); 
    	List<OrderOut> ord =  query.getResultList();
    	return ord;
    }

	@Override
	public void updateByCondition(String query) throws DAOException {
		// TODO Auto-generated method stub
		String s = " UPDATE OrderOut o " + query;
		this.em.createQuery(s).executeUpdate();
	}
}
