package general.services;


import general.dao.DAOException;
import general.tables.City;

import org.springframework.transaction.annotation.Transactional;

public interface CityService {
	
	@Transactional
	void create(City city) throws DAOException;
	
	@Transactional
	void update(City city) throws DAOException;
}
