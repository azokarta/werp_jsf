package general.dao.impl;

import general.Validation;
import general.dao.HrDocActionLogDao;
import general.tables.HrDocActionLog;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("hrDocActionLogDao")
public class HrDocActionLogDaoImpl extends GenericDaoImpl<HrDocActionLog>implements HrDocActionLogDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocActionLog> findAll(String cond) {
		String s = " SELECT h FROM HrDocActionLog h ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);

		return q.getResultList();
	}

}
