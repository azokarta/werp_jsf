package general.services;

import general.dao.DAOException;
import general.tables.Role_action;

import org.springframework.transaction.annotation.Transactional;

public interface Role_actionService {
	@Transactional
	void insertGivenAccess(Role_action a_role_action) throws DAOException;
	
	@Transactional
	void loadByRoleId(Long a_role_id) throws DAOException;
	
	@Transactional
	void updateAccess(Role_action a_role_action) throws DAOException;
}
