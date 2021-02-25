package crm.dao;

import java.util.List;

import crm.tables.CrmRelative;
import general.dao.GenericDao;

public interface CrmRelativeDao extends GenericDao<CrmRelative> {
	List<CrmRelative> findAll(String cond);
}
