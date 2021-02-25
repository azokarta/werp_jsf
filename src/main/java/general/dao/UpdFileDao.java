package general.dao;

import java.util.List;

import general.tables.UpdFile;

public interface UpdFileDao extends GenericDao<UpdFile>{
	List<UpdFile> findAll(String cond);
}
