package general.dao2.impl2;


import general.dao.impl.GenericDaoImpl;

import general.dao2.MessageAttachDao;
import general.tables2.MessageAttach;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("messageAttachDao")
public class MessageAttachDaoImpl extends GenericDaoImpl<MessageAttach> implements MessageAttachDao{
	@Override
	public List<MessageAttach> findAll(String condition) {
		String s = "SELECT t FROM MessageAttach t ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		List<MessageAttach> l =  query.getResultList();
		return l;
	}

}