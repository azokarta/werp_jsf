package general.dao;

import java.util.List;

import general.tables.RequestOutMatnr;

public interface RequestOutMatnrDao extends GenericDao<RequestOutMatnr> {
	public List<RequestOutMatnr> findAll(String condition);
	
	public List<RequestOutMatnr> findReqMatnrs(Long reqId);
}
