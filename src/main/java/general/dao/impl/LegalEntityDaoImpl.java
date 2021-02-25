package general.dao.impl;

import general.dao.LegalEntityDao;
import general.tables.LegalEntity;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("legalEntityDao")
public class LegalEntityDaoImpl extends GenericDaoImpl<LegalEntity> implements LegalEntityDao{

	public List<LegalEntity> findAll() {
		Query query = this.em
				.createQuery("SELECT b FROM LegalEntity b"); 
		List<LegalEntity> l = query.getResultList();
		return l;
	}
	
	public List<LegalEntity> findAll(String condition) {
		String q = " SELECT b FROM LegalEntity b";
		if(condition.length() > 0){
			q += " WHERE " + condition;
		}
		Query query = this.em.createQuery(q);
		List<LegalEntity> l = query.getResultList();
		return l;
	}
}
