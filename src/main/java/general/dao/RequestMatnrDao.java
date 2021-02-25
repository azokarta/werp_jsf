package general.dao;

import java.util.List;

import general.tables.RequestMatnr;

public interface RequestMatnrDao extends GenericDao<RequestMatnr> {
	public List<RequestMatnr> findAll(String condition);
	
	public List<RequestMatnr> findReqMatnrs(Long reqId);
}
