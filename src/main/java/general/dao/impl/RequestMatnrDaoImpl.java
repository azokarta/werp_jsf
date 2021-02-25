package general.dao.impl;

import general.dao.RequestMatnrDao;
import general.tables.Matnr;
import general.tables.RequestMatnr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("requestMatnrDao")
public class RequestMatnrDaoImpl extends GenericDaoImpl<RequestMatnr> implements RequestMatnrDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestMatnr> findAll(String condition) {
		String s = " SELECT r FROM RequestMatnr r  ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestMatnr> findReqMatnrs(Long reqId) {
//		String s = " SELECT r,m FROM RequestMatnr r LEFT JOIN r.matnrObject m WHERE r.request_id = :request_id ";
//		Query query = this.em.createQuery(s);
//		query.setParameter("request_id", reqId);
//		List<Object[]> result = query.getResultList();
//		List<RequestMatnr> out = new ArrayList<RequestMatnr>();
//		for (Object[] o : result) {
//			RequestMatnr rm = (RequestMatnr) o[0];
//			Matnr m = (Matnr) o[1];
//			if (m == null) {
//				rm.setMatnrObject(new Matnr());
//			} else {
//				rm.setMatnrObject(m);
//			}
//
//			out.add(rm);
//		}
//		return out;
		
		String s = " SELECT r FROM RequestMatnr r WHERE r.request_id = :request_id ";
		Query query = this.em.createQuery(s);
		query.setParameter("request_id", reqId);
		return query.getResultList();
	}
}
