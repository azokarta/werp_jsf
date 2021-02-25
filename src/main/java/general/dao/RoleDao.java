package general.dao;

import java.util.List;

import general.tables.Role;

public interface RoleDao  extends GenericDao<Role> {

	public Role findByText45(String s);
	
	public List<Role> findAll(String condition);
}
