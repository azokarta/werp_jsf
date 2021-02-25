package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.RoleDao;
import general.tables.Role;

import org.springframework.stereotype.Component; 
@Component("roleDao")
public class RoleDaoImpl extends GenericDaoImpl<Role> implements RoleDao {

	@Override
	public Role findByText45(String s) {
		Role resultRole = null;
		Query q = this.em.createQuery(String.format("SELECT r FROM Role r WHERE r.text45 = '%s'", s));
		List results = q.getResultList();
		if(!results.isEmpty())
		{
			resultRole = (Role)results.get(0);
		}
		
		return resultRole;
	}

	@Override
	public List<Role> findAll(String condition) {
		String s = " SELECT r FROM Role r ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
}