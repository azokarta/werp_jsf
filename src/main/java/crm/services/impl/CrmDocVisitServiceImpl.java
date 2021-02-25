package crm.services.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import crm.dao.CrmDocVisitDao;
import crm.services.CrmDocDemoService;
import crm.services.CrmDocVisitService;
import crm.tables.CrmDocVisit;
import crm.validators.Validator;
import general.dao.DAOException;

@Service("crmDocVisitService")
public class CrmDocVisitServiceImpl implements CrmDocVisitService {

	@Autowired
	CrmDocVisitDao visitDao;

	@Autowired
	CrmDocDemoService demoService;

	@Autowired
	@Qualifier("crmVisitValidator")
	Validator visitValidator;

	@Override
	public CrmDocVisit save(CrmDocVisit entity, Long userId) {
		validate(entity, userId);
		if (entity.isNew()) {
			visitDao.create(entity);
		} else {
			visitDao.update(entity);
		}

		return entity;
	}

	private void validate(CrmDocVisit entity, Long userId) throws DAOException {
		if (entity.isNew()) {
			entity.setCreatedAt(Calendar.getInstance().getTime());
			entity.setCreatedBy(userId);
		}

		entity.setUpdatedBy(userId);
		entity.setUpdatedAt(Calendar.getInstance().getTime());

		if (!visitValidator.isValid(entity)) {
			throw new DAOException(visitValidator.getError());
		}
	}
}
