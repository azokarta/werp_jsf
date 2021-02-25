package general.dao;

import java.util.List;

import general.tables.ActionType;

public interface ActionTypeDao extends GenericDao<ActionType>{
	public List<ActionType> findAll();
}
