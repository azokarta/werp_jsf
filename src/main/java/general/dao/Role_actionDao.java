package general.dao;

import java.util.List;

import general.tables.Role_action;

public interface Role_actionDao extends GenericDao<Role_action> {
	public List<Role_action> getByRoleId(Long role_id);
	public void deleteByRoleId(Long roleId);
	
	public List<Role_action> getUserRoleActions(Long roleId);
}
