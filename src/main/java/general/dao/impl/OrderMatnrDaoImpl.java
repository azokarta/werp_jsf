package general.dao.impl;

import general.dao.OrderMatnrDao;
import general.tables.OrderMatnr;
import java.util.List;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("orderMatnrDao")
public class OrderMatnrDaoImpl extends GenericDaoImpl<OrderMatnr>implements OrderMatnrDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderMatnr> findAll(String cond) {
		String s = " SELECT o FROM OrderMatnr o ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderMatnr> findAllNotPostingByOrderId(Long orderId) {
		String s = "SELECT om FROM OrderMatnr om WHERE quantity > posting_quantity AND order_id = " + orderId;
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderMatnr> findAllByOrderId(Long orderId) {
		String s = "SELECT om FROM OrderMatnr om WHERE order_id = " + orderId;
		Query q = em.createQuery(s);
		return q.getResultList();
	}
}
