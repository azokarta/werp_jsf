//package general.dao.impl;
//
//
//import java.util.List;
//
//import javax.persistence.Query;
//
//import org.springframework.stereotype.Component;
//
//import general.dao.ConCancelRequestDao;
//import general.tables.ConCancelRequest;
//
//@Component("conCancelRequestDao")
//public class ConCancelRequestDaoImpl extends GenericDaoImpl<ConCancelRequest> implements ConCancelRequestDao {
//
//	public List<ConCancelRequest> findAll() {
//		Query query = this.em
//				.createQuery("select c FROM ConCancelRequest c"); 
//		return query.getResultList();
//	}
//	
//	@Override
//	public List<ConCancelRequest> findAll(String condition) {
//		String s = " SELECT c FROM ConCancelRequest c WHERE " + condition;
//		Query query = this.em
//				.createQuery(s); 
//		return query.getResultList();
//	}
//}
