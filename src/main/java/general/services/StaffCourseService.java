package general.services; 
import org.springframework.transaction.annotation.Transactional;
 



import general.dao.DAOException;
import general.tables.StaffCourse;

public interface StaffCourseService{
	@Transactional 
	void create( StaffCourse s) throws DAOException;
}