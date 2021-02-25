package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.ExpenceTypeDao;
import general.tables.ExpenceType;

@Component("expenceTypeDao")

public class ExpenceTypeDaoImpl extends GenericDaoImpl<ExpenceType> implements ExpenceTypeDao {

	@Override
	public List<ExpenceType> findAll() {
		Query q = this.em.createQuery("SELECT et FROM ExpenceType et");
		List<ExpenceType> l = q.getResultList();
		return l;
	}
}
