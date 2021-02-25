package general.dao.impl;

import general.Validation;
import general.dao.RevItemDao;
import general.tables.RevItem;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("revItemDao")
public class RevItemDaoImpl extends GenericDaoImpl<RevItem> implements RevItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RevItem> findAll(String condition) {
		String s = "SELECT r FROM RevItem r ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}

		Query q = em.createQuery(s, RevItem.class);
		return q.getResultList();
	}

}
