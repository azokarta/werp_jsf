package crm.dao;

import java.util.List;

import crm.tables.CrmDocDemo;
import crm.tables.search.CrmDocDemoSearch;
import general.dao.GenericDao;

public interface CrmDocDemoDao extends GenericDao<CrmDocDemo> {

	List<CrmDocDemo> findAll(CrmDocDemoSearch searchModel);
	
	List<CrmDocDemo> findAllSoldDemos(CrmDocDemoSearch searchModel);
	
	List<CrmDocDemo> findAllByCondition(String cond);

	List<CrmDocDemo> findAllForFutureTab(CrmDocDemoSearch searchModel);

	List<CrmDocDemo> findAllRecoDemos(Long recoId);
	
	CrmDocDemo findByContractNumber(Long contractNumber);
}
