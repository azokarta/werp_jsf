package general.dao;



import java.util.List;
import java.util.Map;

import general.tables.HrDocTransfer;
import general.tables.HrDocTransferApprover;
import general.tables.HrDocTransferItem;
import general.tables.Pyramid;
import general.tables.Salary;

public interface HrDocTransferDao extends GenericDao<HrDocTransfer>{
	
	HrDocTransfer findWithDetail(Long id);
	
	Map<Long, Salary> getUserSalaryMap(String[] branchIds, Long positionId);
	
	List<HrDocTransfer> findAll(String cond);
	
	List<HrDocTransferItem> findAllItems(Long docId);
	
	List<HrDocTransferApprover> findAllApprovers(Long docId);
	
	/**
	 * 
	 * @param salaryId
	 * @return - Возвращает STAFF_ID менеджера сотрудника
	 */
	Long getStaffParentManagerId(Salary salary);
}
