package general.dao2;

import java.util.List;

import general.dao.DAOException;
import general.dao.GenericDao;
import general.tables2.PrikazAttach;

public interface PrikazAttachDao extends GenericDao<PrikazAttach>{
	public List<PrikazAttach> findListByUser(Long a_userId, String al_status) throws DAOException;
	public int deleteByIdPrikaz(Long a_id_prikaz) throws DAOException;
}
