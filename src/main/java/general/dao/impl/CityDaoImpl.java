package general.dao.impl;


import java.util.List;

import javax.persistence.Query;

import general.dao.CityDao;
import general.tables.City;

import org.springframework.stereotype.Component;

@Component("cityDao")
public class CityDaoImpl extends GenericDaoImpl<City> implements CityDao{

	public List<City> findAll() {
		Query query = this.em
				.createQuery("select c FROM City c"); 
		List<City> l_city = query.getResultList();
		return l_city;
	}
	
	@Override
	public List<City> findAll(String condition) {
		String s = " SELECT c FROM City c WHERE " + condition;
		
		Query query = this.em
				.createQuery(s); 
		List<City> l_city = query.getResultList();
		return l_city;
	}
}
