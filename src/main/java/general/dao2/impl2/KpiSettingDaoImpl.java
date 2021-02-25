package general.dao2.impl2;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.impl.GenericDaoImpl;
import general.dao2.KpiItemDao;
import general.dao2.KpiSettingDao;
import general.tables.KpiSetting;

@Component("kpiSettingDao")
public class KpiSettingDaoImpl extends GenericDaoImpl<KpiSetting>implements KpiSettingDao {

	@Autowired
	KpiItemDao itemDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<KpiSetting> findAll(String cond) {
		String s = "SELECT k FROM KpiSetting k";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public KpiSetting findWithDetail(Long id) {
		KpiSetting out = super.find(id);
		if (out != null) {
			out.setKpiItems(itemDao.findAll("kpi_id = " + out.getId()));
		}
		// String s = "SELECT s FROM KpiSetting s INNER JOIN FETCH s.kpiItems
		// WHERE s.id = :id ";
		// Query q = em.createQuery(s);
		// q.setParameter("id", id);
		// return (KpiSetting)q.getSingleResult();
		// out.setKpiItems(itemDao.f);
		return out;
	}
}
