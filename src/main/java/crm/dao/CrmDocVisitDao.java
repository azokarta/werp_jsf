package crm.dao;

import java.util.List;

import crm.tables.CrmDocVisit;
import crm.tables.search.CrmDocVisitSearch;
import general.dao.GenericDao;

public interface CrmDocVisitDao extends GenericDao<CrmDocVisit> {

	List<CrmDocVisit> findAll(CrmDocVisitSearch searchModel);

	List<CrmDocVisit> findAllWithDetails(CrmDocVisitSearch searchModel);
}
