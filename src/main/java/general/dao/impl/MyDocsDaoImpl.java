package general.dao.impl;

import general.Validation;
import general.dao.MyDocsDao;
import general.tables.MyDocs;
import general.tables.Request;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("myDocsDao")
public class MyDocsDaoImpl extends GenericDaoImpl<MyDocs> implements MyDocsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<MyDocs> findAll(String condition) {
		String s = " SELECT m FROM MyDocs m ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MyDocs> findAllRequest(String condition) {
		String s = " SELECT md,r FROM MyDocs md, Request r WHERE md.context_id=r.id AND md.context = '"
				+ Request.CONTEXT + "' ";
		if (!Validation.isEmptyString(condition)) {
			s += " AND " + condition;
		}

		Query q = em.createQuery(s);
		List<MyDocs> out = new ArrayList<MyDocs>();
		List<Object[]> result = q.getResultList();
		for (Object[] o : result) {
			MyDocs md = (MyDocs) o[0];
			if (md != null) {
				Request r = (Request) o[1];
				if (r != null) {
					md.setRequest(r);
					out.add(md);
				}
			}
		}

		return out;
	}

}
