package crm.services.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import crm.dao.CrmCallDao;
import crm.services.CrmCallService;
import crm.services.CrmDocDemoService;
import crm.services.CrmDocRecoService;
import crm.tables.CrmCall;
import crm.tables.CrmDoc;
import crm.tables.CrmDocDemo;
import crm.validators.Validator;
import general.dao.DAOException;

@Service("crmCallService")
public class CrmCallServiceImpl implements CrmCallService {

	@Autowired
	CrmDocRecoService recoService;

	@Autowired
	CrmDocDemoService demoService;

	@Autowired
	@Qualifier("crmCallValidator")
	Validator callValidator;

	@Autowired
	CrmCallDao callDao;

	@Override
	public CrmCall save(CrmCall entity, CrmDoc docEntity, Long userId) throws DAOException {
		validate(entity, userId);
		CrmDocDemo demoEntity = null;
		if (docEntity instanceof CrmDocDemo) {
			demoEntity = (CrmDocDemo) docEntity;
			demoEntity.setCallDate(entity.getDateTime());
			demoService.validate(demoEntity, userId);
		}

		if (entity.isNew()) {
			callDao.create(entity);
		} else {
			callDao.update(entity);
		}

		if (demoEntity != null) {
			demoEntity.setCallId(entity.getId());
			demoService.save(demoEntity, userId);
		}

		if (entity.getCrmDocReco() != null) {
			recoService.updateAfterCall(entity.getCrmDocReco(), entity);
		}

		return entity;
	}

	public void validate(CrmCall entity, Long userId) throws DAOException {
		if (entity.isNew()) {
			entity.setCreatedAt(Calendar.getInstance().getTime());
			entity.setCreatedBy(userId);
		}

		if (entity.getCrmDocReco() != null) {
			entity.setBukrs(entity.getCrmDocReco().getBukrs());
			entity.setBranchId(entity.getCrmDocReco().getBranchId());
		}

		entity.setUpdatedAt(Calendar.getInstance().getTime());
		entity.setUpdatedBy(userId);
		if (!callValidator.isValid(entity)) {
			throw new DAOException(callValidator.getError());
		}
	}

	@Override
	public CrmCall save(CrmCall entity, Long userId) throws DAOException {
		save(entity, null, userId);
		return entity;
	}
}
