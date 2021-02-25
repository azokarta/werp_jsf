package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.MatnrWarDao;
import general.tables.MatnrWar;

@Component("matnrWarDao")
public class MatnrWarDaoImpl extends GenericDaoImpl<MatnrWar> implements MatnrWarDao {
	
	public List<MatnrWar> findAll() {
		Query query = this.em
				.createQuery("select m FROM MatnrWar m"); 
		List<MatnrWar> l_mw = query.getResultList();
		return l_mw;
	}
	
	@Override
	public List<MatnrWar> findAll(String condition) {
		String s = " SELECT m FROM MatnrWar m WHERE " + condition;
		Query query = this.em
				.createQuery(s); 
		List<MatnrWar> l_mw = query.getResultList();
		return l_mw;
	}
	
	public MatnrWar findByMatnr(Long a_matnrId) {
		String s = " SELECT m FROM MatnrWar m WHERE m.matnr_id = " + a_matnrId 
				+ " ORDER BY m.from_date DESC";
		Query query = this.em
				.createQuery(s); 
		List<MatnrWar> l_mw = query.getResultList();
		
		if (l_mw.size() > 0) 
			return l_mw.get(0);
		else 
			return null;
	}
}
