package general.services;

import general.dao.DAOException;
import general.tables.MatnrWar;

import org.springframework.transaction.annotation.Transactional;

public interface MatnrWarService {
	
	@Transactional
	void create(MatnrWar mw) throws DAOException;
	
	@Transactional
	void update(MatnrWar mw) throws DAOException;
}
