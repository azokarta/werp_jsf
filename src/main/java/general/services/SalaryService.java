package general.services; 
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
 

import general.dao.DAOException;
import general.tables.Salary;
import general.tables.search.SalarySearch;
public interface SalaryService{
	@Transactional
	void createSalary( Salary s,Long parentPyramidId) throws DAOException;
	
	@Transactional
	void removeSalary( Salary s, Long userId) throws DAOException;
	
	@Transactional
	void updateSalary( Salary s) throws DAOException;
	
	@Deprecated
	@Transactional
	void createSalaryForMigr( Salary s) throws DAOException;
	
	@Deprecated
	@Transactional
	void createSalaryForMigr2( Salary s) throws DAOException;
	
	List<Salary> findAllOnDismiss(SalarySearch searchModel);
}