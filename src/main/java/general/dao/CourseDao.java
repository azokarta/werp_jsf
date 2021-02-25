package general.dao; 

import java.util.List;

import general.tables.Course;

public interface CourseDao extends GenericDao<Course> {
    
	public List<Course> findAll();
	public List<Course> findAll(String condition);
}
