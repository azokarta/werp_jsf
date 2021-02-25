package general.dao;

import java.util.List;

import general.tables.HrDocumentRoute;

public interface HrDocumentRouteDao extends GenericDao<HrDocumentRoute>{
	public List<HrDocumentRoute> findAll(String cond);
}
