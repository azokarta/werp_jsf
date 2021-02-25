package general.services.hr;


import general.dao.DAOException;
import general.tables.HrDocActionLog;

import org.springframework.transaction.annotation.Transactional;

public interface HrDocActionLogService {

	@Transactional
	void create(HrDocActionLog log,Long userId) throws DAOException;
}
