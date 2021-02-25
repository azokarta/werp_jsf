package general.dao2.impl2;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.impl.GenericDaoImpl;
import general.dao2.KpiItemDao;
import general.tables.KpiItem;

@Component("kpiItemDao")
public class KpiItemDaoImpl extends GenericDaoImpl<KpiItem>implements KpiItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<KpiItem> findAll(String cond) {
		String s = "SELECT i FROM KpiItem i ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

}
