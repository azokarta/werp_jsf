package general.dao2.impl2;


import general.dao.DAOException;
import general.dao.impl.GenericDaoImpl;

import general.dao2.MessageHeaderDao;
import general.tables2.MessageHeader;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("messageHeaderDao")
public class MessageHeaderDaoImpl extends GenericDaoImpl<MessageHeader> implements MessageHeaderDao{

	public List<Object[]> findInMessage(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="select CASE WHEN TO_CHAR (SYSDATE, 'MM-DD-YYYY') = to_char(mh.mess_date, 'MM-DD-YYYY') then to_char(mh.mess_date, 'HH24:MI') else  to_char(mh.mess_date, 'DD.MM.YYYY') end, "
				+"initcap(s.lastname) || ' ' || initcap(s.firstname) || ' ' || initcap(s.middlename) "
				+",mh.mess_head_text "
			       +",mh.mess_id "
			       +",mt.mess_to "
			       +",mt.message_to_id "
			       +",mt.read_bool "
			 +"from message_header mh, message_to mt,user_table u, Staff s "
			+"where mh.mess_id=mt.mess_id and mh.mess_from=u.user_id "
			+"and u.staff_id=s.staff_id and "+ a_dynamicWhere +" order by mh.mess_date DESC";
			Query query = this.em.createNativeQuery(s1);
			List<Object[]> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public int countUnreadMessageByUser(Long a_userId) throws DAOException{
		Long count;
		Query query = this.em
				.createQuery("select count(mt.message_to_id)  from MessageTo mt where mt.read_bool=1 and mt.mess_to = "+a_userId); 
		count = (Long) query.getSingleResult();
		if (count == null || count == 0)
		{
			return 0;
		}
		else
		{
			return (int) (long) count;
		}
        
	}
	
	public List<Object[]> findToMessage(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="select CASE WHEN TO_CHAR (SYSDATE, 'MM-DD-YYYY') = to_char(mh.mess_date, 'MM-DD-YYYY') then to_char(mh.mess_date, 'HH24:MI') else  to_char(mh.mess_date, 'DD.MM.YYYY') end, "
				+"mh.mess_to_names "
				+",mh.mess_head_text ,mh.mess_id "
			 +"from message_header mh "
			+"where "+ a_dynamicWhere +" order by mh.mess_date DESC";
			Query query = this.em.createNativeQuery(s1);
			List<Object[]> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
}