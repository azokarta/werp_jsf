package crm.dao;

import java.util.Date;
import java.util.List;


import crm.tables.CrmCall;
import general.dao.GenericDao;

public interface CrmCallDao extends GenericDao<CrmCall> {
	List<CrmCall> findAllByContext(String context, Long contextId);
	
	List<CrmCall> findAllWithDetails(String bukrs,List<String> branchIds,Long managerId,Date fromDate,Date toDate,Integer resultId);
}
