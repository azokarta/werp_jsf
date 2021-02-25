package general.dao;

import java.util.List;

import general.tables.Request;

public interface RequestDao extends GenericDao<Request> {
	public List<Request> findAll(String condition);
	
}
