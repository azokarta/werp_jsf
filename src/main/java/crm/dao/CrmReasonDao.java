package crm.dao;

import java.util.List;

import crm.tables.CrmReason;
import general.dao.GenericDao;

public interface CrmReasonDao extends GenericDao<CrmReason> {
	List<CrmReason> findAll(String cond);

	List<CrmReason> findAllByType(Integer typeId);
}
