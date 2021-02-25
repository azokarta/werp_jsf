package general.dao.impl;

import general.dao.HrDocumentRouteDao;
import general.tables.HrDocumentRoute;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("hrDocumentRouteDao")
public class HrDocumentRouteDaoImpl extends GenericDaoImpl<HrDocumentRoute> implements HrDocumentRouteDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocumentRoute> findAll(String cond) {
		String s = "SELECT h FROM HrDocumentRoute h ";
		if(cond.length() > 0){
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	
}
