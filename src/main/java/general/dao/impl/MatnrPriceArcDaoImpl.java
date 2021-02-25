package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.MatnrPriceArcDao;
import general.tables.MatnrPriceArc;

@Component("matnrPriceArcDao")
public class MatnrPriceArcDaoImpl extends GenericDaoImpl<MatnrPriceArc> implements MatnrPriceArcDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrPriceArc> findAll(String condition) {
		String s = " SELECT mp FROM MatnrPriceArc mp ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
}
