package general.dao2;


import java.util.List;

import general.dao.GenericDao;
import general.tables2.MessageAttach;

public interface MessageAttachDao extends GenericDao<MessageAttach> {
	public List<MessageAttach> findAll(String condition);
}
