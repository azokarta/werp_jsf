package general.dao;

import java.util.List;

import general.tables.Blart;

public interface BlartDao extends GenericDao<Blart> {
	public Blart getByBlart(String blart);
	public List<Blart> findAll();
}
