package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.UpdFileDao;
import general.tables.UpdFile;

import org.springframework.stereotype.Component;
@Component("updFileDao")
public class UpdFileDaoImpl extends GenericDaoImpl<UpdFile> implements UpdFileDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<UpdFile> findAll(String cond) {
		String s = " SELECT f FROM UpdFile f ";
		if(cond.length() > 0){
			s += " WHERE " + cond;
		}
		
		Query q = em.createQuery(s);
		return q.getResultList();
	}

}
