package general.dao.impl;

import java.util.List;

import javax.persistence.Query;


import general.dao.StatementDao;

import general.tables.Statement;
import general.tables.Zreport;

import org.springframework.stereotype.Component; 
@Component("statementDao")
public class StatementDaoImpl extends GenericDaoImpl<Statement> implements StatementDao {


	@Override
	public List<Statement> findAll(String condition) {
		String s = " SELECT r FROM Statement r ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
	
	@Override
	public List<Statement> findAll() {
		String s = " SELECT r FROM Statement r ";
		Query q = this.em.createQuery(s);
		
		List<Statement> l =  q.getResultList();
		return l;
		
		
	}
}