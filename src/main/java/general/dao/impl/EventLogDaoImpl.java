package general.dao.impl;

import general.Validation;
import general.dao.EventLogDao;
import general.tables.EventLog;
import general.tables.MatnrList;
import general.tables.Staff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("eventLogDao")
public class EventLogDaoImpl extends GenericDaoImpl<EventLog> implements EventLogDao{
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventLog> findAll(String cond) {
		String s = "SELECT e FROM EventLog e ";
		if(cond.length() > 0){
			s += " WHERE " + cond;
		}
				
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
	
	@Override
	public int getRowCount(String condition) {
		String s = " select COUNT(e.id) FROM EventLog e ";
		if(!Validation.isEmptyString(condition)){
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);        
        return ((Long) query.getSingleResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventLog> findAllLazy(String condition, int first, int pageSize) {
		String s = " SELECT "
				+ "e.id, e.msg, e.datetime, e.transaction_id, e.staff_id, e.bukrs, e.type, "
				+ "s.firstname,s.lastname,s.middlename FROM EventLog e LEFT JOIN e.staff s ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		System.out.println(s);
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<Object[]> result = q.getResultList();
		List<EventLog> out = new ArrayList<EventLog>();
		System.out.println("RES: " + result.get(0)[1]);
		for(Object[] o:result){
			EventLog el = new EventLog();
			el.setId((Long)o[0]);
			el.setMsg((String)o[1]);
			el.setDatetime((Timestamp)o[2]);
			el.setTransaction_id((Long)o[3]);
			el.setStaff_id((Long)o[4]);
			el.setBukrs((String)o[5]);
			el.setType((Integer)o[6]);
			
			Staff stf = new Staff();
			stf.setStaff_id(el.getStaff_id());
			stf.setFirstname((String)o[7]);
			stf.setLastname((String)o[8]);
			stf.setMiddlename((String)o[9]);
			el.setStaff(stf);
			
			out.add(el);
		}
		return out;
	}
}
