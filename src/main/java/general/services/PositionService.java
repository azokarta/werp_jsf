package general.services;


import general.dao.DAOException;
import general.tables.Position;

import org.springframework.transaction.annotation.Transactional;

public interface PositionService {
	
	@Transactional
	void create(Position position) throws DAOException;
	
	@Transactional
	void update(Position position) throws DAOException;
}
