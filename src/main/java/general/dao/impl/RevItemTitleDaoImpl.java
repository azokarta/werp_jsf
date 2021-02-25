package general.dao.impl;

import general.Validation;
import general.dao.RevItemTitleDao;
import general.tables.RevItemTitle;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("revItemTitleDao")
public class RevItemTitleDaoImpl extends GenericDaoImpl<RevItemTitle> implements RevItemTitleDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RevItemTitle> findAll(String cond) {
		String s = " SELECT r FROM RevItemTitle r ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s, RevItemTitle.class);
		return q.getResultList();
	}

}
