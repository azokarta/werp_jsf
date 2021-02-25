package crm.dao;

import java.util.List;


import crm.tables.CrmDocReco;
import crm.tables.search.CrmDocRecoSearch;
import general.dao.GenericDao;

public interface CrmDocRecoDao extends GenericDao<CrmDocReco> {
	List<CrmDocReco> findAllCurrentByCallerIsDealer(int callerIsDealer);
	
	List<CrmDocReco> findAllByCondition(String cond);

	List<CrmDocReco> findAllWithDetailsByCondition(CrmDocRecoSearch searchModel);

	List<CrmDocReco> findAllForDemosec(CrmDocRecoSearch searchModel);
	
	CrmDocReco findWithDetail(Long id);
}
