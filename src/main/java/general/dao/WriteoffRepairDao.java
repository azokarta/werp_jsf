package general.dao;

import java.util.List;

import general.tables.WriteoffRepair;

public interface WriteoffRepairDao extends GenericDao<WriteoffRepair>{
	
	WriteoffRepair findWithDetail(Long id);
	
	List<WriteoffRepair> findAll(String cond);
}
