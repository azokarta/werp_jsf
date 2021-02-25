package general.services;

import general.dao.DAOException;
import general.tables.State;

import org.springframework.transaction.annotation.Transactional;

public interface StateService {
	
	@Transactional
	void create(State s) throws DAOException;
	
	@Transactional
	void update(State s) throws DAOException;
	
}
