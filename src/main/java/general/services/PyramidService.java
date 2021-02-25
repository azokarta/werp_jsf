package general.services;

import general.dao.DAOException;
import general.tables.Pyramid;
import general.tables.Salary;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface PyramidService {
	@Transactional
	List<Pyramid> dynamicSearchPyramid (Pyramid a_pyramid ) throws DAOException;
	
	Pyramid findRoot(String bukrs) throws DAOException;
	List<Pyramid> getRootList(String bukrs,Long branchId) throws DAOException;
	Pyramid findOne(String c,Long val) throws DAOException;
	
	@Transactional
	void createPyramid(Pyramid p) throws DAOException;
	
	@Transactional
	void updatePyramid(Pyramid p) throws DAOException;
	
	@Transactional
	void deletePyramid(Long id) throws DAOException;
	
	@Transactional
	void createPyramid(Pyramid p,Salary s) throws DAOException;
}
