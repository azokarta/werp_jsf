package crm.services;


import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmDocVisit;
import general.dao.DAOException;

public interface CrmDocVisitService {

	@Transactional
	CrmDocVisit save(CrmDocVisit entity, Long userId) throws DAOException;
}
