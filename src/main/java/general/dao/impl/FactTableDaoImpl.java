package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.FactTableDao;
import general.tables.FactTable;

import org.springframework.stereotype.Component; 
@Component("factTableDao")
public class FactTableDaoImpl extends GenericDaoImpl<FactTable> implements FactTableDao {

	@Override
	public List<FactTable> findAll(String condition) {
		Query q = this.em.createQuery("SELECT ft FROM FactTable ft WHERE " + condition);
		List<FactTable> l = q.getResultList();
		return l;
	}
}