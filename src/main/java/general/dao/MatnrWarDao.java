package general.dao;

import general.tables.MatnrWar;

import java.util.List;

public interface MatnrWarDao extends GenericDao<MatnrWar>{
	public List<MatnrWar> findAll();
	public List<MatnrWar> findAll(String condition);
	public MatnrWar findByMatnr(Long a_matnrId);	
}
