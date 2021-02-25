package general.dao;

import java.util.List;

import general.tables.MatnrMovement;

public interface MatnrMovementDao extends GenericDao<MatnrMovement> {
	
	List<MatnrMovement> findAll(String condition);
}
