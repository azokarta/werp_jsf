package general.dao;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import general.tables.Salary;
import general.tables.Staff;
import general.tables.search.SalarySearch;

public interface SalaryDao extends GenericDao<Salary> {
	public List<Salary> findByStaffId(Long a_staff_id);

	public Long countSalaryById(Long a_salary_id);

	public List<Salary> findByBukrs(Date a_firstDay, Date a_lastDay, String a_bukrs);

	public Salary findOne(String column, Long value);

	public Salary findCurrentOne(Staff s);

	public List<Salary> findDynamic(String a_dynamic);

	public List<Salary> findDynamic2(String a_dynamic);

	public List<Object[]> findDynamic3(String a_dynamic);

	public List<Object[]> findDynamicFired(String a_dynamic);

	public List<Salary> findTest();

	public List<Salary> findAll(String condition);

	public List<Salary> findByBukrs2(Date a_firstDay, Date a_lastDay, String a_bukrs);

	public void updateCurrentSalaryEndDate(Long staffId, java.util.Date endDate);

	public List<Salary> findAllCurrentWithStaff(String cond);

	public List<Salary> findAllWithStaff(String cond, int first, int pageSize);

	public List<Salary> findAllWithStaff(String cond);

	public List<Salary> findAllCurrent(String cond);

	public int getRowCountWithStaff(String condition);

	public List<Salary> findAllByPositionId(Long positionId);

	public List<Salary> findAllByBranchAndPositionId(Long branchId, Long positionId);

	public List<Salary> findAllForHrRep5(String bukrs, List<Long> branchIds, Long positionId)
			throws DAOException, ParseException;

	public List<Staff> findAllForHrRep6(String bukrs, List<Long> branchIds, Long positionId, Long departmentId)
			throws DAOException, ParseException;

	/*
	 * public List<Salary> findDynamic_oldWages(String a_dynamic);
	 */

	public List<Object[]> findAllSalaryWithStaffForReport(String bukrs, Long branchId, Long positionId,
			Long departmentId, Long staffId, String currency);

	List<Salary> findAllOnDismiss(SalarySearch searchModel);

	Salary findLastSalary(Long staffId);
}
