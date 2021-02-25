package general.dao2.impl2;



import general.dao.impl.GenericDaoImpl;
import general.dao2.MessageToDao;
import general.tables2.MessageTo;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("messageToDao")
public class MessageToDaoImpl extends GenericDaoImpl<MessageTo> implements MessageToDao{

	public int updateReadBool(List<Long> a_mess_to_id, int a_read_bool)
	{
		String queryClause = "";
		String a_dynamicWhere = "";
		if (a_mess_to_id!=null && a_mess_to_id.size()>0)
		{
			
			for(Long wa:a_mess_to_id)
			{
				if(a_dynamicWhere.length()==0) a_dynamicWhere = a_dynamicWhere + wa;
				else a_dynamicWhere = a_dynamicWhere + "," + wa;
			}
		}
		queryClause = "update MessageTo set read_bool="+a_read_bool+" where message_to_id in("+a_dynamicWhere+")";
		Query query = this.em.createQuery(queryClause);
		return query.executeUpdate();
	}
}