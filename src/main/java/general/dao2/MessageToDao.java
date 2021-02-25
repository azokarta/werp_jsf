package general.dao2;

import java.util.List;

import general.dao.GenericDao;

import general.tables2.MessageTo;

public interface MessageToDao extends GenericDao<MessageTo> {
	public int updateReadBool(List<Long> a_mess_to_id, int a_read_bool);
}