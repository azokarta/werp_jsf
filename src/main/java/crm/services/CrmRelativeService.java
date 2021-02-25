package crm.services;

import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmRelative;
import general.dao.DAOException;

public interface CrmRelativeService {

	@Transactional
	CrmRelative save(CrmRelative entity, Long userId) throws DAOException;

	void validate(CrmRelative entity, Long userId) throws DAOException;
}
