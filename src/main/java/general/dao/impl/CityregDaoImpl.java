package general.dao.impl;


import java.util.List;

import javax.persistence.Query;

import general.dao.CityregDao;
import general.tables.Cityreg;

import org.springframework.stereotype.Component;

@Component("cityregDao")
public class CityregDaoImpl extends GenericDaoImpl<Cityreg> implements CityregDao{

	public List<Cityreg> findAll() {
		Query query = this.em
				.createQuery("select c FROM Cityreg c"); 
		List<Cityreg> l_cityreg = query.getResultList();
		return l_cityreg;
	}
}
