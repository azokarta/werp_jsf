package general.services.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.DAOException;
import general.dao.HrDocActionLogDao;
import general.tables.HrDocActionLog;

@Service("hrDocActionLogService")
public class HrDocActionLogServiceImpl implements HrDocActionLogService {

	@Autowired
	HrDocActionLogDao logDao;

	/**
	 * Добавляем действия пользователя, Действия ПРОСМОТР добавляется только
	 * один раз
	 */
	@Override
	public void create(HrDocActionLog log, Long userId) throws DAOException {
		if (HrDocActionLog.ACTION_VIEW.equals(log.getActionId())) {
			List<HrDocActionLog> l = logDao.findAll(String.format(" action_id = %d AND doc_id = %d AND created_by=%d ",
					HrDocActionLog.ACTION_VIEW, log.getDocId(), log.getCreatedBy()));
			if (Validation.isEmptyCollection(l)) {
				logDao.create(log);
			}
		} else {
			logDao.create(log);
		}

	}

}
