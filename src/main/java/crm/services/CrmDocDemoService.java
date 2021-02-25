package crm.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import general.dao.DAOException;
import general.tables.Contract;

public interface CrmDocDemoService {

	@Transactional
	CrmDocDemo save(CrmDocDemo entity, Long userId) throws DAOException;

	void validate(CrmDocDemo entity, Long userId) throws DAOException;

	@Transactional
	void updateAfterRecoItemsSave(CrmDocDemo entity, List<CrmDocReco> recoItems, Long userId);
	
	@Transactional
	void contractCreated(Contract contract,CrmDocDemo demo);
	
	@Transactional
	void contractUpdated(Contract contract);
}
