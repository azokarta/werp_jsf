package general.services; 
import org.springframework.transaction.annotation.Transactional;
 


import general.dao.DAOException;
import general.tables.StaffExpence;

public interface StaffExpenceService{
	@Transactional 
	void create( StaffExpence s) throws DAOException;
	
	@Transactional
	void delete(StaffExpence se) throws DAOException;
}