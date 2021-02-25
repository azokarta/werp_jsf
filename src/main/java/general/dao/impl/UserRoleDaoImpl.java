package general.dao.impl;

import general.Validation;
import general.dao.UserRoleDao;
import general.tables.UserRole;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("userRoleDao")
public class UserRoleDaoImpl extends GenericDaoImpl<UserRole> implements UserRoleDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findUserRoles(Long userId) {
		String s = " SELECT ur FROM UserRole ur WHERE ur.userId = " + userId;
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findAll(String cond) {
		String s = " SELECT ur FROM UserRole ur ";
		if(!Validation.isEmptyString(cond)){
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}
	
}
