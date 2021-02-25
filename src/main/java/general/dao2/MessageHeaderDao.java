package general.dao2;

import java.util.List;

import general.dao.DAOException;
import general.dao.GenericDao;

import general.tables2.MessageHeader;

public interface MessageHeaderDao extends GenericDao<MessageHeader> {
	public List<Object[]> findInMessage(String a_dynamicWhere) throws DAOException;
	public List<Object[]> findToMessage(String a_dynamicWhere) throws DAOException;
	public int countUnreadMessageByUser(Long a_userId) throws DAOException;
}