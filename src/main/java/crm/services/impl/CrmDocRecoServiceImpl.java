package crm.services.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import crm.constants.CallConstants;
import crm.constants.DemoConstants;
import crm.constants.RecoConstants;
import crm.dao.CrmDocRecoDao;
import crm.services.CrmDocDemoService;
import crm.services.CrmDocRecoService;
import crm.tables.CrmCall;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import crm.validators.Validator;
import general.Validation;
import general.dao.DAOException;

@Service("crmDocRecoService")
public class CrmDocRecoServiceImpl implements CrmDocRecoService {

	@Autowired
	CrmDocRecoDao recoDao;

	@Autowired
	CrmDocDemoService demoService;

	@Autowired
	@Qualifier("recoValidator")
	Validator recoValidator;

	@Override
	public CrmDocReco save(CrmDocReco entity, Long userId) {
		validate(entity, userId);
		if (entity.isNew()) {
			recoDao.create(entity);
			if (Validation.isEmptyLong(entity.getParentId())) {
				entity.setTreeId(entity.getId());
				recoDao.update(entity);
			}
		} else {
			recoDao.update(entity);
		}

		return entity;
	}

	private void validate(CrmDocReco entity, Long userId) throws DAOException {
		if (entity.isNew()) {
			entity.setOwnerId(entity.getResponsibleId());
			entity.setOwnerBranchId(entity.getBranchId());
			entity.setCreatedAt(Calendar.getInstance().getTime());
			entity.setCreatedBy(userId);
			if (!Validation.isEmptyLong(entity.getParentId())) {
				CrmDocReco parent = recoDao.find(entity.getParentId());
				entity.setTreeId(parent.getTreeId());
			}
		}

		entity.setUpdatedBy(userId);
		entity.setUpdatedAt(Calendar.getInstance().getTime());

		if (!recoValidator.isValid(entity)) {
			throw new DAOException(recoValidator.getError());
		}
	}

	@Override
	public void save(List<CrmDocReco> entities, Long userId) throws DAOException {
		for (CrmDocReco entity : entities) {
			validate(entity, userId);
		}

		for (CrmDocReco entity : entities) {
			save(entity, userId);
		}
	}

	@Override
	public void save(List<CrmDocReco> entities, CrmDocDemo demoEntity, Long userId) throws DAOException {
		for (CrmDocReco entity : entities) {
			validate(entity, userId);
		}

		for (CrmDocReco entity : entities) {
			save(entity, userId);
		}

		demoService.updateAfterRecoItemsSave(demoEntity, entities, userId);
	}

	@Override
	public void updateAfterCall(CrmDocReco entity, CrmCall callEntity) {
		entity.setUpdatedAt(callEntity.getUpdatedAt());
		entity.setUpdatedBy(callEntity.getUpdatedBy());
		if (CallConstants.RESULT_POSITIVE.equals(callEntity.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_DEMO);
		} else if (CallConstants.RESULT_RECALL.equals(callEntity.getResultId())) {
			entity.setCallDate(callEntity.getRecallDateTime());
			entity.setStatusId(RecoConstants.STATUS_RECALL);
		} else if (CallConstants.RESULT_REFUSE.equals(callEntity.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_REFUSE);
		} else if (CallConstants.RESULT_NOT_AVAILABLE.equals(callEntity.getResultId())) {
			if (callEntity.getRecallDateTime() == null) {
				entity.setStatusId(RecoConstants.STATUS_NOT_AVAILABLE);
			} else {
				entity.setStatusId(RecoConstants.STATUS_RECALL);
				entity.setCallDate(callEntity.getRecallDateTime());
			}
		}

		recoDao.update(entity);
	}

	@Override
	public void updateAfterDemo(CrmDocReco entity, CrmDocDemo demo) {
		entity.setUpdatedAt(Calendar.getInstance().getTime());
		entity.setUpdatedBy(demo.getUpdatedBy());

		if (DemoConstants.RESULT_DONE.equals(demo.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_DEMO_SHOWN);
		} else if (DemoConstants.RESULT_MINI_CONTRACT.equals(demo.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_MINI_CONTRACT);
		} else if (DemoConstants.RESULT_SOLD.equals(demo.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_SOLD);
		} else if (DemoConstants.RESULT_SOLD_CANCELLED.equals(demo.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_SOLD_CANCELLED);
		} else if (DemoConstants.RESULT_CANCELLED.equals(demo.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_CANCELLED);
		} else if (DemoConstants.RESULT_MOVED.equals(demo.getResultId())) {
			entity.setStatusId(RecoConstants.STATUS_RECALL);
		}

		recoDao.update(entity);
	}
}
