package crm.services;


import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmCall;
import crm.tables.CrmDoc;
import general.dao.DAOException;

public interface CrmCallService {

	@Transactional
	CrmCall save(CrmCall entity,CrmDoc docEntity, Long userId) throws DAOException;
	
	@Transactional
	CrmCall save(CrmCall entity,Long userId) throws DAOException;
	
	void validate(CrmCall entity, Long userId) throws DAOException;
}
