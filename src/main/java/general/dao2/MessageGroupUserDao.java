package general.dao2;

import java.util.List;


import general.dao.GenericDao;
import general.tables2.MessageGroupUser;

public interface MessageGroupUserDao extends GenericDao<MessageGroupUser> {
	public List<MessageGroupUser> findAll(String condition);
}