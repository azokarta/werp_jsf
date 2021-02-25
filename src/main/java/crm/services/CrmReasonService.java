package crm.services;

import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmReason;
import general.dao.DAOException;

public interface CrmReasonService {

	@Transactional
	CrmReason save(CrmReason entity, Long userId) throws DAOException;

	void validate(CrmReason entity, Long userId) throws DAOException;
}
