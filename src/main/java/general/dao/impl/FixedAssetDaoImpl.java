//package general.dao.impl;
//
//import java.util.List;
//
//import javax.persistence.Query;
//
//import general.dao.FixedAssetDao;
//import general.tables.FixedAsset;
//
//import org.springframework.stereotype.Component; 
//@Component("fixedAssetDao")
//public class FixedAssetDaoImpl extends GenericDaoImpl<FixedAsset> implements FixedAssetDao {
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<FixedAsset> findAll() {
//		Query q = this.em.createQuery("SELECT fa FROM FixedAsset fa");
//		return q.getResultList();
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<FixedAsset> findAll(String condition) {
//		String s = "SELECT fa FROM FixedAsset fa ";
//		if(condition.length() > 0){
//			s += " WHERE " + condition;
//		}
//		Query q = this.em.createQuery(s);
//		return q.getResultList();
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public FixedAsset findOne(String condition) {
//		String s = "SELECT fa FROM FixedAsset fa WHERE " + condition;
//		Query q = this.em.createQuery(s);
//		List<FixedAsset> l = q.getResultList();
//		if(l.size() > 0){
//			return l.get(0);
//		}
//		return null;
//	}
//
//	@Override
//	public Integer findMaxCounter(Long branchId) {
//		Query q = this.em.createQuery("SELECT MAX(counter) FROM FixedAsset fa WHERE fa.branch_id = :b");
//		q.setParameter("b", branchId);
//		return (Integer) q.getSingleResult();
//	}
//
//}