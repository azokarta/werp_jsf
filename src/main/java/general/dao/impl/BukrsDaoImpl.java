package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.BukrsDao;
import general.tables.Bukrs;

import org.springframework.stereotype.Component;

@Component("bukrsDao")
public class BukrsDaoImpl extends GenericDaoImpl<Bukrs> implements BukrsDao{

	@Override
	public List<Bukrs> findAll() {
		String q = " SELECT b FROM Bukrs b order by name";
		Query query = this.em.createQuery(q);
		List<Bukrs> l = query.getResultList();
		return l;
	}
	
}
