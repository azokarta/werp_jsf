package general.dao;

import java.util.List;

import general.tables.HrDocActionLog;

public interface HrDocActionLogDao extends GenericDao<HrDocActionLog>{
	
	public List<HrDocActionLog> findAll(String cond);
}
