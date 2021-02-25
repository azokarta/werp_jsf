package general.dao2.impl2;


import general.dao.impl.GenericDaoImpl;

import general.dao2.MessageGroupDao;
import general.tables2.MessageGroup;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("messageGroupDao")
public class MessageGroupDaoImpl extends GenericDaoImpl<MessageGroup> implements MessageGroupDao{

	@Override
	public List<MessageGroup> findAll(String condition) {
		String s = "SELECT t FROM MessageGroup t ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		List<MessageGroup> l =  query.getResultList();
		return l;
	}
}