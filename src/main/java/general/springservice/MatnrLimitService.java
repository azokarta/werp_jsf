package general.springservice;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables2.MatnrLimit;

public interface MatnrLimitService {
	@Transactional
	public void create(MatnrLimit ml) throws DAOException;
	
	@Transactional
	public void create(List<MatnrLimit> mlList) throws DAOException;

	@Transactional
	public void update(MatnrLimit ml) throws DAOException;

	@Transactional
	public void delete(Long id) throws DAOException;
	
	@Transactional
	public void delete(List<Long> ids) throws DAOException;
}
