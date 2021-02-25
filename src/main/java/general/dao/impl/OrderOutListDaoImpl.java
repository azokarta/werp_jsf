package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.OrderOutListDao;
import general.tables.OrderOutList;

import org.springframework.stereotype.Component;

@Component("orderOutListDao")
public class OrderOutListDaoImpl extends GenericDaoImpl<OrderOutList> implements
		OrderOutListDao {

	@Override
	public List<OrderOutList> findAll(String condition) {
		String s = "SELECT ol FROM OrderOutList ol ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);

		List<OrderOutList> ol = query.getResultList();
		return ol;
	}
}
