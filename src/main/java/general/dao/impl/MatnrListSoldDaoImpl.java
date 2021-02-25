package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.DAOException;
import general.dao.MatnrListSoldDao;
import general.tables.MatnrList;
import general.tables.MatnrListSold;

@Component("matnrListSoldDao")
public class MatnrListSoldDaoImpl extends GenericDaoImpl<MatnrListSold> implements MatnrListSoldDao{

	@Override
	public List<MatnrListSold> findAll(String condition) {
		String s = " SELECT ml FROM MatnrListSold ml ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		@SuppressWarnings("unchecked")
		List<MatnrListSold> out = q.getResultList();
		return out;
	}

	@Override
	public void updateByCondition(String q) throws DAOException {
		// TODO Auto-generated method stub
		String s = " UPDATE MatnrListSold ml " + q;
		this.em.createQuery(s).executeUpdate();
	}

	@Override
	public List<MatnrListSold> findAllGroupped(String condition) {
		String s = " SELECT COUNT(*) AS count, werks,matnr,gjahr FROM MatnrListSold m ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		s += " GROUP BY matnr, werks, gjahr ";
		Query q = this.em.createQuery(s);
		List<Object[]> result = q.getResultList();
		List<MatnrListSold> l = new ArrayList<MatnrListSold>();

		for (Object[] r : result) {
			MatnrListSold mls = new MatnrListSold();
			Long tempLong = new Long(r[0].toString());
			mls.setMenge(tempLong.doubleValue());
			tempLong = new Long(r[1].toString());
			mls.setWerks(tempLong);
			tempLong = new Long(r[2].toString());
			mls.setMatnr(tempLong);
			tempLong = new Long(r[3].toString());
			mls.setGjahr(tempLong.intValue());
			l.add(mls);
		  }
		
		return l;
	}
	
	
}
