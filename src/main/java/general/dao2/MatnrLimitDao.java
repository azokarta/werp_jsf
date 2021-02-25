package general.dao2;

import java.util.List;

import general.dao.GenericDao;
import general.tables2.MatnrLimit;

public interface MatnrLimitDao extends GenericDao<MatnrLimit> {

	List<MatnrLimit> findAll(String bukrs, Long branchId, Long positionId);
	
	List<MatnrLimit> findAll(String bukrs, List<String> branchIds, Long positionId);
	
	List<MatnrLimit> findAllWithItems(String bukrs, Long branchId, Long positionId);
	
	MatnrLimit findWithDetail(Long id);
}
