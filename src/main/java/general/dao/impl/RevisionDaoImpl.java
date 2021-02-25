package general.dao.impl;

import general.dao.RevisionDao;
import general.tables.Revision;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("revisionDao")
public class RevisionDaoImpl extends GenericDaoImpl<Revision> implements RevisionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Revision> findAll() {
		Query q = em.createQuery(" SELECT r FROM Revision r ", Revision.class);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Revision findWithDetail(Long id) {
		String s = " SELECT r FROM Revision r LEFT JOIN fetch r.revResponsibles res LEFT JOIN fetch res.staff stf WHERE r.id = " + id;
		Query q = em.createQuery(s);
		List<Revision> l = q.getResultList();
		if(l != null && l.size() > 0){
			Revision r = (Revision)l.get(0);
			return r;
		}
		return null;
	}

}
