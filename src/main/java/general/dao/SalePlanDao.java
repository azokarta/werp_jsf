package general.dao;

import java.util.List;

import general.tables.SalePlan;

public interface SalePlanDao extends GenericDao<SalePlan> {
	SalePlan findCurrentSP(Long branchId);
	List<SalePlan> findAllByBranchId(Long branchId);
	List<SalePlan> findAll(String condition);
	public List<SalePlan> findAllByBukrs(String a_bukrs);
	public SalePlan findByBranchAndMonthYear(Long branchId, int inMonth, int inYear, Long inBa, byte inType);
}
