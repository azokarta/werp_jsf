package general.services;

import general.dao.DAOException;
import general.tables.Werks;

import org.springframework.transaction.annotation.Transactional;

public interface WerksService {
	
	@Transactional
	void create(Werks w) throws DAOException;
	
	@Transactional
	void update(Werks w) throws DAOException;
}
