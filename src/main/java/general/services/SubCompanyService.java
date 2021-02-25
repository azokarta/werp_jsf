package general.services; 

import org.springframework.transaction.annotation.Transactional;
import general.dao.DAOException; 
import general.tables.SubCompany;

public interface SubCompanyService{
	@Transactional 
	void create( SubCompany sc) throws DAOException;

	@Transactional
	void update( SubCompany sc ) throws DAOException;
}