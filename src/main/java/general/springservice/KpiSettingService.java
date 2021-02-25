package general.springservice;


import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.KpiSetting;

public interface KpiSettingService {
	@Transactional
	public void create(KpiSetting entity, Long userId) throws DAOException;

	@Transactional
	public void update(KpiSetting entity, Long userId) throws DAOException;
}
