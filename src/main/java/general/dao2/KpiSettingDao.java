package general.dao2;

import java.util.List;

import general.dao.GenericDao;
import general.tables.KpiSetting;

public interface KpiSettingDao extends GenericDao<KpiSetting> {

	List<KpiSetting> findAll(String cond);
	
	KpiSetting findWithDetail(Long id);
}
