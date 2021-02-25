package general.dao2;

import java.util.List;

import general.dao.GenericDao;
import general.tables2.MessageGroup;

public interface MessageGroupDao extends GenericDao<MessageGroup> {
	public List<MessageGroup> findAll(String condition);
}