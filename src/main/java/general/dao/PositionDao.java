package general.dao;

import java.util.List;

import general.tables.Position;

public interface PositionDao extends GenericDao<Position>{
	public List<Position> findAll();
	
	public List<Position> findAll(String cond);
	public List<Position> findAllOrdered(String a_lang);
}
