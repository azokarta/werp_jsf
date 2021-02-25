package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrListDao;
import general.dao.MatnrMovementDao;
import general.dao.MatnrMovementItemDao;
import general.tables.MatnrList;
import general.tables.MatnrMovement;
import general.tables.MatnrMovementItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountabilityMatnrService")
public class AccountabilityMatnrServiceImpl implements
		AccountabilityMatnrService {

	@Autowired
	MatnrListDao mlDao;

	@Autowired
	MatnrMovementDao mmDao;

	@Autowired
	MatnrMovementItemDao mmiDao;

	private String validate(List<MatnrList> mList,
			List<MatnrList> preparedMlList) {

		String error = "";
		List<MatnrList> tempList;
		for (MatnrList m : mList) {
			if (m.getMatnrObject() == null) {
				throw new DAOException(
						"Ошибка в коде. Обратитесь разработчику!");
			}

			tempList = new ArrayList<MatnrList>();

			String cond = String.format(
					" werks = %d AND matnr = %d AND status = '%s' ",
					m.getWerks(), m.getMatnr(), MatnrList.STATUS_RECEIVED);

			if (m.getMatnrObject().getType() == 1) { // Должны быть заводские
														// номера
				if (Validation.isEmptyString(m.getBarcode())) {
					error += "Введите баркод для материала: "
							+ m.getMatnrObject().getText45() + "\n";
				} else {
					tempList = mlDao.findAll(
							cond + " AND barcode = '" + m.getBarcode() + "' ",
							1);
					if (tempList.size() == 0) {
						error += "На складе количество материала меньше чем вы запросили "
								+ m.getMatnrObject().getText45() + "\n";
					} else {
						preparedMlList.add(tempList.get(0));
					}
				}
			} else {
				int menge = new Double(m.getMenge()).intValue();
				tempList = mlDao.findAll(cond, menge);
				if (tempList.size() < menge) {
					error += "На складе количество материала меньше чем вы запросили "
							+ m.getMatnrObject().getText45() + "\n";
				} else {
					preparedMlList.addAll(tempList);
				}
			}
		}
		return error;
	}

	@Override
	public void create(List<MatnrList> mList, MatnrMovement movement)
			throws DAOException {
		List<MatnrList> preparedMlList = new ArrayList<MatnrList>();
		String error = validate(mList, preparedMlList);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		movement.setMm_type(MatnrMovement.TYPE_ACCOUNTABILITY);
		movement.setStatus(MatnrMovement.STATUS_ACCOUNTABILITY);
		
		mmDao.create(movement);

		for (MatnrList ml : preparedMlList) {
			ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
			ml.setStaff_id(movement.getStaff_id());
			mlDao.update(ml);

			MatnrMovementItem mmi = new MatnrMovementItem();
			mmi.setMatnr(ml.getMatnr());
			mmi.setMatnr_list_id(ml.getMatnr_list_id());
			mmi.setMm_id(movement.getMm_id());
			mmi.setStatus(MatnrMovement.STATUS_ACCOUNTABILITY);
			mmiDao.create(mmi);
			
		}
	}

	@Override
	public void delete(MatnrList ml) throws DAOException {
		ml.setStaff_id(null);
		ml.setStatus(MatnrList.STATUS_RECEIVED);
		mlDao.update(ml);
		String cond = String.format(" matnr_list_id = %d AND status = '%s' ",
				ml.getMatnr_list_id(), MatnrMovement.STATUS_ACCOUNTABILITY);
		List<MatnrMovementItem> l = mmiDao.findAll(cond);
		for (MatnrMovementItem mmi : l) {
			mmi.setReceived_date(Calendar.getInstance().getTime());
			mmi.setStatus(MatnrMovement.STATUS_RECEIVED);
			mmiDao.update(mmi);
		}
		// String q = String
		// .format(" SET status = '%s', received_date = '%s' WHERE matnr_list_id = %d AND status = '%s' ",
		// MatnrMovement.STATUS_RECEIVED, Calendar.getInstance()
		// .getTime(), ml.getMatnr_list_id(),
		// MatnrMovement.STATUS_ACCOUNTABILITY);
		// mmiDao.updateByQuery(q);
	}
}