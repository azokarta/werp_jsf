package crm.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmCall;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import general.dao.DAOException;

public interface CrmDocRecoService {

	@Transactional
	CrmDocReco save(CrmDocReco entity, Long userId) throws DAOException;

	@Transactional
	void save(List<CrmDocReco> entities, Long userId) throws DAOException;

	@Transactional
	void save(List<CrmDocReco> entities, CrmDocDemo demoEntity, Long userId) throws DAOException;

	@Transactional
	void updateAfterCall(CrmDocReco entity, CrmCall callEntity);

	@Transactional
	void updateAfterDemo(CrmDocReco entity, CrmDocDemo demo);
}
