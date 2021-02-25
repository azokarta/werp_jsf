package general.dao;

import java.util.List;

import general.tables.Country;

public interface CountryDao extends GenericDao<Country> {
	public List<Country> findAll();
	public List<Country> findAll(String condition);
}
