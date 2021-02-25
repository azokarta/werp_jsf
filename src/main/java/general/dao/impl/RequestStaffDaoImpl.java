package general.dao.impl;

import general.dao.RequestStaffDao;
import general.tables.RequestStaff;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("requestStaffDao")
public class RequestStaffDaoImpl extends GenericDaoImpl<RequestStaff> implements RequestStaffDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestStaff> findAll(String condition) {
		String s = " SELECT s FROM RequestStaff s ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		return query.getResultList();
	}

}
