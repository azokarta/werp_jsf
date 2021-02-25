package general.dao;

import java.util.List;
import java.util.Map;

import general.tables.HrDoc;
import general.tables.Salary;

public interface HrDocDao extends GenericDao<HrDoc>{
	public List<HrDoc> findAll(String cond);
	
	HrDoc findWithDetail(Long id);
	
	public int getRowCount(String condition);
	
	public List<HrDoc> findAllLazy(String cond,int first, int pageSize);
	
	/**
	 * 
	 * @param salaryId
	 * @return - Возвращает STAFF_ID менеджера сотрудника
	 */
	Long getStaffParentManagerId(Salary salary);
	
	Map<Long, Salary> getUserSalaryMap(String[] branchIds, Long positionId);
	
	Long getMaxRegNumber(int typeId);
}
