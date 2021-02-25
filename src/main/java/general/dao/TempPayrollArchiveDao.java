package general.dao;

import java.util.List;

import general.tables.TempPayrollArchive;


public interface TempPayrollArchiveDao extends GenericDao<TempPayrollArchive> { 
	public Long countDynamicSearch(String a_dynamicWhere);
	public List<TempPayrollArchive> dynamicSearch(String a_dynamicWhere);
	public List<TempPayrollArchive> dynamicSearchGroupByStaffBranchWaers(String a_dynamicWhere);
}