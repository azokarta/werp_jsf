package general.dao;

import java.util.List;

import general.tables.HrDocApprover;

public interface HrDocApproverDao extends GenericDao<HrDocApprover>{
	public List<HrDocApprover> findAll(String cond);
	
}
