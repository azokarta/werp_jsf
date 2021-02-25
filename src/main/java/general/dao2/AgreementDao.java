package general.dao2;

import java.util.List;

import general.dao.DAOException;
import general.dao.GenericDao;
import general.tables2.Agreement;

public interface AgreementDao extends GenericDao<Agreement>{
	public List<Agreement> findListByUser(Long a_userId, String al_status) throws DAOException;
	public int deleteByIdPrikaz(Long a_id_prikaz) throws DAOException;
	public List<Agreement> findListByUserReadyToAgree(Long a_userId) throws DAOException;
}
