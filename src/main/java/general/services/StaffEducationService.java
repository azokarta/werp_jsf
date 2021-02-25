package general.services; 
import org.springframework.transaction.annotation.Transactional;
 




import general.dao.DAOException;
import general.tables.StaffEducation;

public interface StaffEducationService{
	@Transactional 
	void create( StaffEducation e) throws DAOException;
	
	@Transactional 
	void update( StaffEducation e) throws DAOException;
	
	@Transactional 
	void delete( StaffEducation e) throws DAOException;
}