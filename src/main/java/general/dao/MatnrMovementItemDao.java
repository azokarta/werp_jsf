package general.dao;

import java.util.List;

import general.tables.MatnrMovementItem;

public interface MatnrMovementItemDao extends GenericDao<MatnrMovementItem> {
	List<MatnrMovementItem> findAll(String condition);
	
	List<MatnrMovementItem> findAllWithMatnrListByMmId(Long mmId);
	
	void updateByQuery(String query);
}
