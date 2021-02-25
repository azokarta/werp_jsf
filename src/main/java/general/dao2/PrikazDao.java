package general.dao2;

import java.util.List;

import general.dao.DAOException;
import general.dao.GenericDao;
import general.tables2.Prikaz;

public interface PrikazDao extends GenericDao<Prikaz>{
	public List<Object[]> findListByUser(Long a_userId, String al_status, String a_dynClause) throws DAOException;
	public List<Prikaz> findAll(String condition);
	public List<Object[]> findListByUserReadyToAgree(Long a_userId) throws DAOException;
}
