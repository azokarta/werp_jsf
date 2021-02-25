package general.services;

import general.dao.DAOException;
import general.tables.ReqEventLog;

import org.springframework.transaction.annotation.Transactional;

public interface ReqEventLogService {
	
	@Transactional
	void create(ReqEventLog rel) throws DAOException;
}
