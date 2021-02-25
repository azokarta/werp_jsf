package general.services;

import java.util.List;

import general.dao.DAOException;
import general.dao.Role_actionDao;
import general.tables.Role_action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("role_actionService")
public class Role_actionServiceImpl implements Role_actionService{
	@Autowired
	Role_actionDao dao;

	@Override
	public void insertGivenAccess(Role_action a_role_action)
			throws DAOException {
		try {
			dao.create(a_role_action);
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
		
	}

	@Override
	public void loadByRoleId(Long a_role_id) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAccess(Role_action a_role_action) throws DAOException {
		// TODO Auto-generated method stub
		
	}
}
