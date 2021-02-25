package general.springservice;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import dit.message.UserStaff;
import general.dao.DAOException;
import general.tables2.MessageAttach;
import general.tables2.MessageGroup;
import general.tables2.MessageGroupUser;
import general.tables2.MessageHeader;

public interface MessageSpSer {
	@Transactional
	public void createMessageGroup(MessageGroup a_mg) throws DAOException;
	
	@Transactional
	public void updateMessageGroup(MessageGroup a_mg) throws DAOException;
	
	@Transactional
	public void deleteMessageGroup(MessageGroup a_mg) throws DAOException;
	
	@Transactional
	public void createMessageGroupUser(MessageGroupUser a_mgu) throws DAOException;
	
	@Transactional
	public void deleteMessageGroupUser(MessageGroupUser a_mgu) throws DAOException;
	
	@Transactional
	public void sendMessage(List<UserStaff> al_us, MessageHeader a_mh,List<MessageAttach> al_ma, Long a_userId) throws DAOException;
	
	@Transactional
	public void markRead(List<Long> a_mess_to_id) throws DAOException;
	
	@Transactional
	public void deleteMessage(List<Long> a_mess_to_id) throws DAOException;
	
	@Transactional
	public void markUnread(List<Long> a_mess_to_id) throws DAOException;
	
}
