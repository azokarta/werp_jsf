package general.dao;

import java.util.List;

import general.tables.ReqEventLog;

public interface ReqEventLogDao extends GenericDao<ReqEventLog>{
	public List<ReqEventLog> findAll();
	
	public List<ReqEventLog> findRequestEvents(Long reqId);
}
