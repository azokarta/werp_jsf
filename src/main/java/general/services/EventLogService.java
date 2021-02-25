package general.services;

import general.dao.DAOException;
import general.tables.EventLog;

import org.springframework.transaction.annotation.Transactional;


public interface EventLogService {
	
	@Transactional
	void create(EventLog el) throws DAOException;
}
