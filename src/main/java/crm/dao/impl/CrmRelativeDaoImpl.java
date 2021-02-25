package crm.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import crm.dao.CrmRelativeDao;
import crm.tables.CrmRelative;
import general.Validation;
import general.dao.impl.GenericDaoImpl;

@Component("crmRelativeDao")
public class CrmRelativeDaoImpl extends GenericDaoImpl<CrmRelative>implements CrmRelativeDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmRelative> findAll(String cond) {
		String s = "SELECT c FROM CrmRelative c ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

}
