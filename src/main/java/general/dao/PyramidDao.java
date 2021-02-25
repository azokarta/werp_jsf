package general.dao;

import java.util.List;

import general.tables.Pyramid;
import general.tables.Salary;

public interface PyramidDao extends GenericDao<Pyramid> {
	public List<Pyramid> dynamicFindPyramid(String a_dynamicWhere);

	public Pyramid findRoot(String bukrs);

	public List<Pyramid> getRootList(String bukrs, Long breachId);

	public Pyramid findOne(String column, Long val);

	public Pyramid findPyramid(String bukrs, Long branchId, Long staffId, Long positionId);

	public List<Pyramid> findByBukrsBranchPosition(String bukrs, Long branchId, Long positionId);

	public List<Salary> findAllDealersByBranchManagerId(String bukrs, Long branchId, Long managerId);

	public List<Salary> findAllDemosecsByBranchManagerId(String bukrs, Long branchId, Long managerId);

	public List<Salary> findAllManagersByBranchId(String bukrs, Long branchId);

	public List<Pyramid> findAllWithDetailsByBukrs(String bukrs);

	public List<Pyramid> findAllWithStaff(String cond);

	public Pyramid findParentPyramid(String bukrs, Long branchId, Long childStaffId, Long childStaffPositionId);
	
	List<Pyramid> findAll();
}