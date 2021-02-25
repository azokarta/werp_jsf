package general.dao.impl;


import java.util.List;

import javax.persistence.Query;
import general.dao.MatnrReturnedDao;
import general.tables.MatnrReturned;

import org.springframework.stereotype.Component;

@Component("matnrReturnedDao")
public class MatnrReturnedDaoImpl extends GenericDaoImpl<MatnrReturned> implements MatnrReturnedDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrReturned> findAll(String condition) {
		String s = " SELECT m FROM MatnrReturned m ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
	
	
}
