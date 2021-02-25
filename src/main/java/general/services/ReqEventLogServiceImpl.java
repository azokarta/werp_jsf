package general.services;

import general.dao.DAOException;
import general.dao.ReqEventLogDao;
import general.tables.ReqEventLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reqEventLogService")
public class ReqEventLogServiceImpl implements
		ReqEventLogService {
	
	@Autowired ReqEventLogDao relDao;

	@Override
	public void create(ReqEventLog rel) throws DAOException {
		relDao.create(rel);
	}	
}