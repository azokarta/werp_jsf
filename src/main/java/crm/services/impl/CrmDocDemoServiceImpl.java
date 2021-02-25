package crm.services.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import crm.constants.DemoConstants;
import crm.constants.RecoConstants;
import crm.dao.CrmCallDao;
import crm.dao.CrmDocDemoDao;
import crm.dao.CrmDocRecoDao;
import crm.services.CrmDocDemoService;
import crm.services.CrmDocRecoService;
import crm.tables.CrmCall;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import crm.validators.Validator;
import general.Validation;
import general.dao.DAOException;
import general.tables.Contract;
import general.tables.ContractStatus;

@Service("crmDocDemoService")
public class CrmDocDemoServiceImpl implements CrmDocDemoService {

	@Autowired
	CrmDocDemoDao demoDao;

	@Autowired
	CrmDocRecoDao recoDao;

	@Autowired
	CrmCallDao callDao;

	@Autowired
	CrmDocRecoService recoService;

	@Autowired
	@Qualifier("demoValidator")
	Validator demoValidator;

	@Override
	public CrmDocDemo save(CrmDocDemo entity, Long userId) {
		validate(entity, userId);
		if (entity.isNew()) {
			demoDao.create(entity);
			if (Validation.isEmptyLong(entity.getParentId())) {
				entity.setTreeId(entity.getId());
				demoDao.update(entity);
			}
		} else {
			demoDao.update(entity);
			// if (entity.getCrmDocReco() != null) {
			// recoService.updateAfterDemo(entity.getCrmDocReco(), entity);
			// }

			if (entity.getParentReco() != null) {
				recoService.updateAfterDemo(entity.getParentReco(), entity);
			}
		}

		return entity;
	}

	public void validate(CrmDocDemo entity, Long userId) throws DAOException {
		if (entity.isNew()) {
			entity.setCreatedAt(Calendar.getInstance().getTime());
			entity.setCreatedBy(userId);
			if (!Validation.isEmptyLong(entity.getParentId())) {
				CrmDocDemo parent = demoDao.find(entity.getParentId());
				entity.setTreeId(parent.getTreeId());
			}
		}

		// if (entity.getParentReco() != null) {
		// entity.setRecoId(entity.getCrmDocReco().getId());
		// if (entity.getCrmDocReco().getCallerIsDealer() == 1) {
		// entity.setAppointedBy(entity.getCrmDocReco().getResponsibleId());
		// } else {
		// entity.setAppointedBy(userId);
		// }
		// entity.setDealerId(entity.getCrmDocReco().getResponsibleId());
		// }

		if (!DemoConstants.RESULT_SOLD.equals(entity.getResultId())) {
			entity.setSaleDate(null);
		}

		entity.setUpdatedBy(userId);
		entity.setUpdatedAt(Calendar.getInstance().getTime());

		if (!demoValidator.isValid(entity)) {
			throw new DAOException(demoValidator.getError());
		}
	}

	@Override
	public void updateAfterRecoItemsSave(CrmDocDemo entity, List<CrmDocReco> recoItems, Long userId) {
		entity.setUpdatedAt(Calendar.getInstance().getTime());
		entity.setUpdatedBy(userId);
		entity.setRecoCount(entity.getRecoCount() + recoItems.size());
		demoDao.update(entity);
	}

	@Override
	public void contractCreated(Contract contract, CrmDocDemo demo) {
		if (demo == null) {
			throw new DAOException("Demo Is NULL!!!");
		}
		CrmCall call = callDao.find(demo.getCallId());
		if (contract.getContract_date() != null) {
			demo.setSaleDate(contract.getContract_date());
		}
		demo.setContractNumber(contract.getContract_number());
		demo.setCustomerId(contract.getCustomer_id());
		demo.setUpdatedAt(Calendar.getInstance().getTime());
		if (Validation.isEmptyLong(contract.getDemo_sc())) {
			if (call != null) {
				demo.setAppointedBy(contract.getDealer());
			}
		} else {
			demo.setAppointedBy(contract.getDemo_sc());
		}
		// if (!Validation.isEmptyLong(contract.getUpdated_by())) {
		// demo.setUpdatedBy(contract.getUpdated_by());
		// }
		CrmDocReco reco = recoDao.find(demo.getRecoId());
		if (reco != null) {
			reco.setCustomerId(contract.getCustomer_id());
			reco.setContractNumber(contract.getContract_number());
			reco.setStatusId(RecoConstants.STATUS_SOLD);
			reco.setUpdatedAt(Calendar.getInstance().getTime());
			// if (!Validation.isEmptyLong(contract.getUpdated_by())) {
			// reco.setUpdatedBy(contract.getUpdated_by());
			// }
			recoDao.update(reco);
		}

		if (call != null) {
			if (Validation.isEmptyLong(contract.getDemo_sc())) {
				call.setCallerId(contract.getDealer());
			} else {
				call.setCallerId(contract.getDemo_sc());
			}
			callDao.update(call);
		}

		demoDao.update(demo);
	}

	@Override
	public void contractUpdated(Contract contract) {
		CrmDocDemo demo = demoDao.findByContractNumber(contract.getContract_number());
		if (demo != null) {
			if (ContractStatus.STATUS_CANCELLED == contract.getContract_status_id().intValue()) {
				demo.setResultId(DemoConstants.RESULT_SOLD_CANCELLED);
			}
			demo.setUpdatedAt(Calendar.getInstance().getTime());

			if (!demo.getAppointedBy().equals(contract.getDemo_sc())) {
				if (Validation.isEmptyLong(contract.getDemo_sc())) {
					if (Validation.isEmptyLong(contract.getDealer())) {
						demo.setAppointedBy(0L);
					} else {
						demo.setAppointedBy(contract.getDealer());
					}

				} else {
					demo.setAppointedBy(contract.getDemo_sc());
				}

				CrmCall crmCall = callDao.find(demo.getCallId());
				if (crmCall != null) {
					crmCall.setCallerId(demo.getAppointedBy());
					callDao.update(crmCall);
				}
			}

			// if (!Validation.isEmptyLong(contract.getUpdated_by())) {
			// demo.setUpdatedBy(contract.getUpdated_by());
			// }
			demoDao.update(demo);

			CrmDocReco reco = recoDao.find(demo.getRecoId());
			if (reco != null) {
				if (ContractStatus.STATUS_CANCELLED == contract.getContract_status_id().intValue()) {
					reco.setStatusId(RecoConstants.STATUS_SOLD_CANCELLED);
				}

				reco.setUpdatedAt(Calendar.getInstance().getTime());
				// if (!Validation.isEmptyLong(contract.getUpdated_by())) {
				// reco.setUpdatedBy(contract.getUpdated_by());
				// }
				recoDao.update(reco);
			}
		}
	}
}
