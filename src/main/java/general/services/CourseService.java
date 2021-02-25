package general.services; 

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException; 
import general.tables.Course;

public interface CourseService{
	@Transactional 
	void create( Course c) throws DAOException;

	@Transactional
	void update( Course c ) throws DAOException;
}