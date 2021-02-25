package general.dao.impl;

import general.Validation;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.Bkpf;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userDao")
public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {

	@Autowired
	StaffDao staffDao;

	public User loadUserByUsername(String username) {
		Query query = this.em.createQuery("select u FROM User u where u.username= :username");
		query.setParameter("username", username);
		List<User> users = query.getResultList();
		if (users != null && users.size() == 1) {
			return users.get(0);
		}
		return null;
	}

	public User findByUsername(String a_username) {
		Query query = this.em.createQuery("select u FROM User u where u.username= :username");
		query.setParameter("username", a_username);
		User user = (User) query.getSingleResult();
		return user;
	}

	public Long countUserByUsername(String a_username) {
		Query query = this.em.createQuery("select COUNT(u) FROM User u where u.username= :username");
		query.setParameter("username", a_username);

		Long count = (Long) query.getSingleResult();
		return count;
	}

	public Long countUserbyId(Long a_user_id) {
		Query query = this.em.createQuery("select COUNT(u) FROM User u where u.user_id = :user_id");
		query.setParameter("user_id", a_user_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public String getUserFio(Long a_user_id) {
		String fio = "";
		Query query = this.em.createQuery(
				"select s.lastname,s.firstname FROM User u,Staff s where u.user_id = :user_id and u.staff_id=s.staff_id");
		query.setParameter("user_id", a_user_id);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			fio = String.valueOf(result[0]);
			fio = fio + " " + String.valueOf(result[1]);
		}
		if (results.size() == 0) {
			query = this.em.createQuery("select u.user_id, u.username FROM User u where u.user_id = :user_id");
			query.setParameter("user_id", a_user_id);
			results = query.getResultList();
			for (Object[] result : results) {
				fio = String.valueOf(result[1]);
			}
		}
		return fio;
	}

	@Override
	public List<User> findAll(String condition) {
		String s = "SELECT u FROM User u";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		List<User> l = q.getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllWithStaff() {
		String s = " SELECT u, s FROM User u LEFT JOIN u.staff s ";

		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<User> out = new ArrayList<User>();
		for (Object[] o : result) {
			User u = (User) o[0];
			Staff stf = (Staff) o[1];
			if (stf == null) {
				Staff stfNew = new Staff();
				stfNew.setFirstname(u.getUsername());
				u.setStaff(new Staff());
			} else {
				u.setStaff(stf);
			}

			out.add(u);
		}
		return out;
	}

	@Override
	public User findWithStaff(Long id) {
		User u = find(id);
		if (u != null) {
			Staff stf;
			if (Validation.isEmptyLong(u.getStaff_id())) {
				stf = new Staff();
				stf.setFirstname(u.getUsername());
			} else {
				stf = staffDao.find(u.getStaff_id());
				if (stf == null) {
					stf = new Staff();
					stf.setFirstname(u.getUsername());
				}
			}

			u.setStaff(stf);
		}
		return u;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllWithUserRoles(String condition) {
		return null;
		// String s = " SELECT u FROM User u LEFT JOIN fetch u.userRoles ul ";
		// if(!Validation.isEmptyString(condition)){
		// s += " WHERE " + condition;
		// }
		//
		// Query q = em.createQuery(s);
		//
		// return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getRep1Data(String bukrs, Long branchId, Long roleId, int isRoot, String username) {
		String s = "SELECT user_id,username,u.branch_id, u.bukrs,u.is_active, s.firstname, s.middlename,s.lastname FROM user_table u,"
				+ " staff s WHERE u.staff_id=s.staff_id ";
		if (!Validation.isEmptyString(bukrs)) {
			s += " AND u.bukrs = '" + bukrs + "' ";
		}

		if (!Validation.isEmptyLong(branchId)) {
			s += " AND u.branch_id = " + branchId + " ";
		}

		if (isRoot == 0 || isRoot == 1) {
			s += " AND is_root = " + isRoot;
		}

		if (!Validation.isEmptyString(username)) {
			s += " AND username LIKE '%" + username + "%' ";
		}

		Query q = em.createNativeQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public User findUserByStaffId(Long staffId) {
		String s = " SELECT u FROM User u WHERE u.staff_id = " + staffId;
		Query q = this.em.createQuery(s);
		List<User> l = q.getResultList();
		if(l.size() == 0){
			return null;
		}
		return l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getUsernameFio(String a_where) {
		String s = "SELECT u.user_id,u.username,initcap(s.lastname) || ' ' || initcap(s.firstname) || ' ' || initcap(s.middlename) as fio FROM user_table u,"
				+ " staff s WHERE u.staff_id=s.staff_id ";

		if (a_where!=null && a_where.length()>0)
		{
			s = s + " " +a_where;
		}
		Query q = em.createNativeQuery(s);
		return q.getResultList();
	}
	
	@Override
	public List<User> findFromMessageGroup(String condition) {
		String s = "select u from MessageGroupUser mgu,User u where mgu.user_id=u.user_id ";
		if (condition.length() > 0) {
			s = s + condition;
		}
		Query q = this.em.createQuery(s);
		List<User> l = q.getResultList();
		return l;
	}
	
	@Override
	public List<Long> findFromPosition(String condition) {
		
		List<Long> l = new ArrayList<Long>();
		String s = "select u.user_id,u.username from salary sa, staff st, user_table u where sa.staff_id=st.staff_id "
			+" and beg_date <= sysdate and end_date>= sysdate and u.staff_id=st.staff_id and u.is_active=1 "
			+" and sa.position_id in ("+condition+")"
			+" group by u.user_id,u.username ";

		Query q = this.em.createNativeQuery(s);
		List<Object[]> results = q.getResultList();
		
		for(Object[] result:results)
		{
			Long wa_long = 0L;
			wa_long = Long.parseLong(String.valueOf(result[0]));
			l.add(wa_long);
		}
		
		
		return l;
	}
	
}
