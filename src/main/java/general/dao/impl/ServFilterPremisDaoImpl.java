package general.dao.impl;

import general.dao.ServFilterPremisDao;
import general.tables.ServFilterPremis;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("servFilterPremisDao")
public class ServFilterPremisDaoImpl  extends GenericDaoImpl<ServFilterPremis> implements ServFilterPremisDao {

	public List<ServFilterPremis> findAll() {
		Query query = this.em
				.createQuery("select c FROM ServFilterPremis c"); 
		List<ServFilterPremis> l_city = query.getResultList();
		return l_city;
	}
	
	@Override
	public List<ServFilterPremis> findAll(String condition) {
		String s = " SELECT c FROM ServFilterPremis c WHERE " + condition;
		
		Query query = this.em
				.createQuery(s); 
		List<ServFilterPremis> l_city = query.getResultList();
		return l_city;
	}
	
	@Override
	public List<ServFilterPremis> findAllByBukrsAndCountry(String in_bukrs, Long in_cId) {
		String s = " SELECT c FROM ServFilterPremis c WHERE "
				+ "c.country_id = " + in_cId
				+ " and c.bukrs = " + in_bukrs
				+ " and c.date_start = some ("
				+ " SELECT max(cc.date_start)"
				+ " FROM ServFilterPremis cc"
				+ ")";
		Query query = this.em
				.createQuery(s); 
		List<ServFilterPremis> l_city = query.getResultList();
		return l_city;
	}
	
}
