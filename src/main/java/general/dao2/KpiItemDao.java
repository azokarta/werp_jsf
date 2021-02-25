package general.dao2;

import java.util.List;

import general.dao.GenericDao;
import general.tables.KpiItem;

public interface KpiItemDao extends GenericDao<KpiItem> {
	List<KpiItem> findAll(String cond);
}
