package general.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.Query;

import general.dao.CountryDao;
import general.tables.Bkpf;
import general.tables.Country;

import org.springframework.stereotype.Component;

@Component("countryDao")
public class CountryDaoImpl extends GenericDaoImpl<Country> implements CountryDao{

	public List<Country> findAll() {
		Query query = this.em
				.createQuery("select c FROM Country c"); 
		List<Country> l_country = query.getResultList();
		return l_country;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> findAll(String condition) {
		String s = " SELECT c FROM Country c ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query query = this.em
				.createQuery(s);
		return query.getResultList();
	}
}
