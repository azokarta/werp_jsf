package general.dao.impl;

import general.Validation;
import general.dao.RequestDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.Request;
import general.tables.Staff;
import general.tables.User;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("requestDao")
public class RequestDaoImpl extends GenericDaoImpl<Request> implements
		RequestDao {

	@Autowired
	StaffDao stfDao;
	
	@Autowired UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Request> findAll(String condition) {
		String s = " SELECT r FROM Request r ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public Request find(Object id) {
		// TODO Auto-generated method stub
		Request r = super.find(id);

		if (r != null) {
			if (!Validation.isEmptyLong(r.getCurrent_responsible())) {
				User u = userDao.findWithStaff(r.getCurrent_responsible());
				if(u == null){
					r.setResponsibleObject(new Staff());
				}else{
					r.setResponsibleObject(u.getStaff());
				}
			}
			
			if (!Validation.isEmptyLong(r.getCreated_by())) {
				User u2 = userDao.findWithStaff(r.getCreated_by());
				if (u2 == null) {
					r.setCreator(new Staff());
				} else {
					r.setCreator(u2.getStaff());
				}
			}
		}

		return r;
	}
}
