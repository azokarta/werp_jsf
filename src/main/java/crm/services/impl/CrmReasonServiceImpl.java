package crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import crm.tables.CrmReason;
import crm.dao.CrmReasonDao;
import crm.services.CrmReasonService;
import crm.validators.Validator;
import general.dao.DAOException;

@Service("crmReasonService")
public class CrmReasonServiceImpl implements CrmReasonService {

	@Autowired
	CrmReasonDao reasonDao;

	@Autowired
	@Qualifier("crmReasonValidator")
	Validator reasonValidator;

	@Override
	public CrmReason save(CrmReason entity, Long userId) throws DAOException {
		validate(entity, userId);

		if (entity.isNew()) {
			reasonDao.create(entity);
		} else {
			reasonDao.update(entity);
		}
		return entity;
	}

	@Override
	public void validate(CrmReason entity, Long userId) throws DAOException {
		if (!reasonValidator.isValid(entity)) {
			throw new DAOException(reasonValidator.getError());
		}
	}

}
