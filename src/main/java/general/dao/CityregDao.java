package general.dao;

import java.util.List;

import general.tables.Cityreg;

public interface CityregDao extends GenericDao<Cityreg> {
	public List<Cityreg> findAll();
}
