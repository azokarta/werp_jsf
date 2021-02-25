package general.dao.impl;
 

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.tables.Course;
import general.dao.CourseDao;

@Component("courseDao")
public class CourseDaoImpl extends GenericDaoImpl<Course> implements CourseDao {
	
	@Override
	public List<Course> findAll(){
    	Query query = this.em
                .createQuery("select c FROM Course c "); 
    	 
    	List<Course> l =  query.getResultList();
    	return l;
    }
	
	@Override
	public List<Course> findAll(String c){
		String q = "SELECT c FROM Course c";
		if(c.length() > 0){
			q += " WHERE " + c;
		}
    	Query query = this.em.createQuery(q);
    	List<Course> l =  query.getResultList();
    	return l;
    }
}