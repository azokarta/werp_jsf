package general.dao;

import java.util.List;

import general.tables.State;

public interface StateDao extends GenericDao<State> {
	public List<State> findAll();
	
	public List<State> findAll(String condition);
}
