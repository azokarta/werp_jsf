package general.services;

import java.util.List;

import general.dao.DAOException;
import general.dao.RoleDao;
import general.dao.Role_actionDao;
import general.tables.Role;
import general.tables.Role_action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
	@Autowired
	Role_actionDao rActionDao;

	@Autowired
	RoleDao roleDao;

	@Override
	public void createRole(Role r, List<Role_action> rActionList)
			throws DAOException {
		String error = validateRole(r, true);

		if (error.length() > 0) {
			throw new DAOException(error);
		}
		roleDao.create(r);

		for (Role_action rAction : rActionList) {
			if (rAction.getAction_id() > 0) {
				rAction.setRole_id(r.getRole_id());
				rActionDao.create(rAction);
			}
		}
	}

	private String validateRole(Role r, boolean isNew) {
		String error = "";
		if (r.getText45().length() == 0) {
			error += "Название обязательно для заполнение";
		}
		// if(r.getBukrs().isEmpty() || r.getBukrs() == null)
		// {
		// //error += "Выберите компанию";
		// }
		if (r.getText45().length() > 0) {
			Role tempRole = roleDao.findByText45(r.getText45());
			if (tempRole != null) {
				if (isNew) {
					error += "Роль с таким названием существует";
				} else if (!r.getRole_id().equals(tempRole.getRole_id())) {
					error += "Роль с таким названием существует";
				}
			}
		}
		return error;
	}

	@Override
	public void updateRole(Role r, List<Role_action> rActionList)
			throws DAOException {
		String error = validateRole(r, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		roleDao.update(r);
		rActionDao.deleteByRoleId(r.getRole_id());
		for (Role_action rAction : rActionList) {
			if (rAction.getAction_id() > 0) {
				rAction.setId(null);
				rAction.setRole_id(r.getRole_id());
				rActionDao.create(rAction);
			}
		}
	}
}
