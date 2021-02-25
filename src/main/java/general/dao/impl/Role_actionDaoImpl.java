package general.dao.impl;

import java.util.List;

import general.dao.Role_actionDao;
import general.tables.Role_action;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("role_actionDao")
public class Role_actionDaoImpl extends GenericDaoImpl<Role_action> implements
		Role_actionDao {
	public List<Role_action> getByRoleId(Long role_id) {
		Query query = this.em
				.createQuery("select r FROM Role_action r where r.role_id= :role_id");
		query.setParameter("role_id", role_id);
		List<Role_action> role_action = query.getResultList();
		return role_action;
	}

	@Override
	public void deleteByRoleId(Long roleId) {
		Query q = this.em
				.createQuery("DELETE FROM Role_action r WHERE r.role_id = :role_id ");
		q.setParameter("role_id", roleId);
		q.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Role_action> getUserRoleActions(Long roleId) {
		String subQuery = " SELECT role_id FROM USER_ROLE WHERE user_id = "
				+ roleId;
		String s = " SELECT * FROM role_action r WHERE r.role_id IN(" + subQuery
				+ ") ";
		Query q = em.createNativeQuery(s, Role_action.class);
		List<Role_action> out = q.getResultList();
		//System.out.println("OOO: " + out.size());
		return out;
	}
}
