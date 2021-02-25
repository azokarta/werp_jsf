package crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import crm.tables.CrmRelative;
import crm.dao.CrmRelativeDao;
import crm.services.CrmRelativeService;
import crm.validators.Validator;
import general.dao.DAOException;

@Service("crmRelativeService")
public class CrmRelativeServiceImpl implements CrmRelativeService {

	@Autowired
	CrmRelativeDao relativeDao;

	@Autowired
	@Qualifier("crmRelativeValidator")
	Validator relativeValidator;

	@Override
	public CrmRelative save(CrmRelative entity, Long userId) throws DAOException {
		validate(entity, userId);

		if (entity.isNew()) {
			relativeDao.create(entity);
		} else {
			relativeDao.update(entity);
		}
		return entity;
	}

	@Override
	public void validate(CrmRelative entity, Long userId) throws DAOException {
		if (!relativeValidator.isValid(entity)) {
			throw new DAOException(relativeValidator.getError());
		}
	}

}
