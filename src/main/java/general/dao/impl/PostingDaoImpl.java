package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.PostingDao;
import general.tables.Posting;

@Component("postingDao")
public class PostingDaoImpl extends GenericDaoImpl<Posting> implements
		PostingDao {

	@Override
	public List<Posting> findAll(String condition) {
		String s = " SELECT p FROM Posting p ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);
		List<Posting> out = query.getResultList();
		return out;
	}

}
