package general.dao.impl;

import general.Validation;
import general.dao.RequestOutDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.RequestOut;
import general.tables.Staff;
import general.tables.User;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("requestOutDao")
public class RequestOutDaoImpl extends GenericDaoImpl<RequestOut> implements
		RequestOutDao {
	
	@Autowired StaffDao stfDao;
	
	@Autowired UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestOut> findAll(String condition) {
		String s = " SELECT r FROM RequestOut r ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public RequestOut find(Object id) {
		// TODO Auto-generated method stub
		RequestOut out = super.find(id);
		if(out != null && !Validation.isEmptyLong(out.getCreated_by())){
			User u = userDao.findWithStaff(out.getCreated_by());
			if(u == null){
				out.setCreator(new Staff());
			}else{
				out.setCreator(u.getStaff());
			}
		}
		
		return out;
	}
}
