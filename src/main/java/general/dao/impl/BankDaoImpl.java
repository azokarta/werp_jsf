package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.BankDao;
import general.tables.Bank;

import org.springframework.stereotype.Component;

@Component("bankDao")
public class BankDaoImpl extends GenericDaoImpl<Bank> implements BankDao{

	public List<Bank> findAll() {
		Query query = this.em
				.createQuery("SELECT b FROM Bank b"); 
		List<Bank> l = query.getResultList();
		return l;
	}
	
	public List<Bank> findAll(String condition) {
		String q = " SELECT b FROM Bank b";
		if(condition.length() > 0){
			q += " WHERE " + condition;
		}
		Query query = this.em.createQuery(q);
		List<Bank> l = query.getResultList();
		return l;
	}
}
