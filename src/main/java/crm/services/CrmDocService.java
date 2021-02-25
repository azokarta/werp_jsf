package crm.services;

import crm.tables.CrmDoc;
import general.dao.DAOException;

public interface CrmDocService {

	CrmDoc save(CrmDoc entity, Long userId) throws DAOException;
}
