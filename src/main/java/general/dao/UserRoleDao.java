package general.dao;

import java.util.List;

import general.tables.UserRole;

public interface UserRoleDao extends GenericDao<UserRole>{
	public List<UserRole> findUserRoles(Long userId);
	
	public List<UserRole> findAll(String cond);
}
