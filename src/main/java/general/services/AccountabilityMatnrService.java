package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.MatnrList;
import general.tables.MatnrMovement;

import org.springframework.transaction.annotation.Transactional;

public interface AccountabilityMatnrService {
	
	@Transactional
	void create(List<MatnrList> mList, MatnrMovement movement) throws DAOException;
	
	@Transactional
	void delete(MatnrList ml) throws DAOException;
}
