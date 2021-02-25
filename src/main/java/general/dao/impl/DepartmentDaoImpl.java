package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.DepartmentDao;
import general.tables.Department;

import org.springframework.stereotype.Component; 
@Component("departmentDao")
public class DepartmentDaoImpl extends GenericDaoImpl<Department> implements DepartmentDao {

    @Override
	public List<Department> findAll(String condition) {
		String s = "SELECT d FROM Department d";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		List<Department> l = q.getResultList();
		return l;
	}
	
}