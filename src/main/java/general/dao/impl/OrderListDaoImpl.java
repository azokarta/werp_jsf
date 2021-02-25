package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.OrderListDao;
import general.tables.OrderList;

import org.springframework.stereotype.Component;
@Component("orderListDao")
public class OrderListDaoImpl extends GenericDaoImpl<OrderList> implements OrderListDao{
	public List<OrderList> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM OrderList b order by id"); 

    	List<OrderList> ol =  query.getResultList();
    	return ol;
    }
	
	public List<OrderList> dynamicFindAll(String wclause) {
    	
		String regx = ";,.";
	    char[] ca = regx.toCharArray();
	    for (char c : ca) {
	    	wclause = wclause.replace(""+c, "");
	    } 
		
		Query query = this.em
                .createQuery("select b FROM OrderList b where " + wclause + " order by id"); 
  
    	List<OrderList> ol =  query.getResultList();
    	return ol;
    }
	
	@Override
	public void deleteByCondition(String condition) {
		String s = " DELETE FROM OrderList o WHERE " + condition;
		Query q = this.em.createQuery(s);
		q.executeUpdate();
	}

	@Override
	public List<OrderList> findAll(String cond) {
		String s = "select b FROM OrderList b ";
		if(cond.length() > 0){
			s += " WHERE " + cond;
		}
		Query query = this.em
                .createQuery(s + " order by id"); 

    	List<OrderList> ol =  query.getResultList();
    	return ol;
	}
}
