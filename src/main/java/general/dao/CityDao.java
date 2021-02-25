package general.dao;

import java.util.List;

import general.tables.City;

public interface CityDao extends GenericDao<City> {
	public List<City> findAll();
	public List<City> findAll(String condition);
}
