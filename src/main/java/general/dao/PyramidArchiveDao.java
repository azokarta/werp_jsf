package general.dao;

import java.util.List;

import general.tables.PyramidArchive;

public interface PyramidArchiveDao extends GenericDao<PyramidArchive>{

	public List<PyramidArchive> dynamicFindPyramid(String a_dynamicWhere);
	public PyramidArchive findRoot(String bukrs);
	public List<PyramidArchive> getRootList(String condition);
	
	public PyramidArchive findOne(String column,Long val);
	public PyramidArchive find(Long id, int year, int month);
	
	public List<PyramidArchive> findByBukrsBranchPosition(int year,int month,String bukrs, Long branchId, Long positionId);
	
	public PyramidArchive findPyramid(int year,int month,String bukrs, Long branchId, Long staffId, Long positionId);
	
	public List<PyramidArchive> findAllWithStaff(String cond);
}
