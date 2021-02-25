package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.Role;
import general.tables.Role_action;

import org.springframework.transaction.annotation.Transactional;

public interface RoleService {
	@Transactional
	void createRole(Role r, List<Role_action> rActionList) throws DAOException;
	
	@Transactional
	void updateRole(Role r, List<Role_action> rActionList) throws DAOException;
}
