package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
import general.dao.MatnrPriceDao;
import general.tables.MatnrPrice;

@Component("matnrPriceDao")
public class MatnrPriceDaoImpl extends GenericDaoImpl<MatnrPrice>implements MatnrPriceDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrPrice> findAll(String condition) {
		String s = " SELECT mp FROM MatnrPrice mp ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MatnrPrice findLastPrice(String bukrs, Long countryId, Long customerId, Long matnr) {
		String s = String.format(
				" SELECT mp FROM MatnrPrice mp WHERE bukrs = '%s' AND country_id = %d AND customer_id = %d AND matnr = %d ORDER BY mp_id DESC ",
				bukrs, countryId, customerId, matnr);
		Query q = this.em.createQuery(s);
		q.setMaxResults(1);
		List<MatnrPrice> l = q.getResultList();
		if (l != null && l.size() > 0) {
			return l.get(0);
		}

		return null;
	}

}
