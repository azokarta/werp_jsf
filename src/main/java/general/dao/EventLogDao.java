package general.dao;

import java.util.List;

import general.tables.EventLog;

public interface EventLogDao extends GenericDao<EventLog>{
	public List<EventLog> findAll(String cond);

	public int getRowCount(String condition);
	
	public List<EventLog> findAllLazy(String condition, int first, int pageSize);
}
