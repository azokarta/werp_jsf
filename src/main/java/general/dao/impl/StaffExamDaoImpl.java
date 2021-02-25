package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.StaffExamDao;
import general.tables.StaffExam;

@Component("staffExamDao")
public class StaffExamDaoImpl extends GenericDaoImpl<StaffExam> implements StaffExamDao {

	@Override
	public List<StaffExam> findAll(String condition) {
		String s = " SELECT s FROM StaffExam s ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		System.out.println(s);
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
	
}
