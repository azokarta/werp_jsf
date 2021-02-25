package general.services;

import general.dao.DAOException;
import general.tables.Department;

import org.springframework.transaction.annotation.Transactional;

public interface DepartmentService {
	
        @Transactional
	public void create(Department d) throws DAOException;
        
        @Transactional
	public void update(Department d) throws DAOException;
}
