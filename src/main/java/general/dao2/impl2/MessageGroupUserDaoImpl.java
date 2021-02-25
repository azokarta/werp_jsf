package general.dao2.impl2;


import general.dao.impl.GenericDaoImpl;

import general.dao2.MessageGroupUserDao;
import general.tables2.MessageGroupUser;
import javax.persistence.Query;
import java.util.List;

import org.springframework.stereotype.Component;

@Component("messageGroupUserDao")
public class MessageGroupUserDaoImpl extends GenericDaoImpl<MessageGroupUser> implements MessageGroupUserDao{

	@Override
	public List<MessageGroupUser> findAll(String condition) {
		String s = "SELECT t FROM MessageGroupUser t ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		List<MessageGroupUser> l =  query.getResultList();
		return l;
	}
}