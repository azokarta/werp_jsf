package general.dao;

import java.util.List;



import general.output.tables.FosaBranchResultTable;
import general.output.tables.FosaResultTable;
import general.tables.TempPayroll; 
 

public interface TempPayrollDao extends GenericDao<TempPayroll> { 
	public Long countDynamicSearch(String a_dynamicWhere);
	public List<TempPayroll> dynamicSearch(String a_dynamicWhere);
	public List<TempPayroll> findAll();
	public void deleteAllByBukrs(String a_bukrs);
	public List<TempPayroll> dynamicSearchGroupByStaffBranchWaers(String a_dynamicWhere);
	public List<FosaResultTable> getSalaryByOffices(String a_bukrs,String a_branch_id,java.sql.Date a_budat_from);
	public List<FosaBranchResultTable> getSalaryByOffices2(String a_bukrs,String a_branch_id,java.sql.Date a_budat_from);
	public int updateDynamicTempPayroll(int a_approve, String a_dynamicWhere);
} 