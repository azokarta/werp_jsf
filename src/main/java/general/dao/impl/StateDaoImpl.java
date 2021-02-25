package general.dao.impl;


import java.util.List;

import javax.persistence.Query;

import general.dao.StateDao;
import general.tables.State;

import org.springframework.stereotype.Component;

@Component("stateDao")
public class StateDaoImpl extends GenericDaoImpl<State> implements StateDao{

	public List<State> findAll() {
		Query query = this.em
				.createQuery("select c FROM State c"); 
		List<State> l_state = query.getResultList();
		return l_state;
	}

	@Override
	public List<State> findAll(String condition) {
		String s = " SELECT c FROM State c ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query query = this.em
				.createQuery(s); 
		List<State> l_state = query.getResultList();
		return l_state;
	}
}
