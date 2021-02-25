package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.MatnrMovementItemDao;
import general.tables.MatnrList;
import general.tables.MatnrMovementItem;

@Component("matnrMovementItemDao")
public class MatnrMovementItemDaoImpl extends GenericDaoImpl<MatnrMovementItem> implements MatnrMovementItemDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrMovementItem> findAll(String condition) {
		String s = " SELECT m FROM MatnrMovementItem m ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public void updateByQuery(String query) {
		String s = " UPDATE MatnrMovementItem m " + query;
		this.em.createQuery(s).executeUpdate();
		
	}

	@Override
	public List<MatnrMovementItem> findAllWithMatnrListByMmId(Long mmId) {
		String s = " SELECT m1,m2 FROM MatnrMovementItem m1 LEFT JOIN m1.matnrList m2 ";
		s += " WHERE m1.mm_id = " + mmId;
		Query q = this.em.createQuery(s);
		List<Object[]> r = q.getResultList();
		List<MatnrMovementItem> out = new ArrayList<MatnrMovementItem>();
		for(Object[] o:r){
			MatnrMovementItem mmi = (MatnrMovementItem)o[0];
			MatnrList ml = (MatnrList)o[1];
			mmi.setMatnrList(ml);
			out.add(mmi);
		}
		//System.out.println("SSSS: " + out.size());
		return out;
	}
	
}
