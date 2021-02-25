package general.dao;

import general.tables.ServConMatnrWar;

import java.util.List;

public interface ServConMatnrWarDao extends GenericDao<ServConMatnrWar> {
	public List<ServConMatnrWar> findAll();
	public List<ServConMatnrWar> findAll(String condition);
	public List<ServConMatnrWar> findAllByContractId(Long conId);
	public List<ServConMatnrWar> findAllByServId(Long servId);
}
