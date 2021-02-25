package general.services;

import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrWarDao;
import general.tables.MatnrWar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("matnrWarService")
public class MatnrWarServiceImpl implements MatnrWarService {

	@Autowired
	MatnrWarDao mwDao;

	@Override
	public void create(MatnrWar mw) throws DAOException {
		String error = validate(mw, true);

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		mwDao.create(mw);
	}

	private String validate(MatnrWar mw, boolean isNew) {
		String error = "";

		if (Validation.isEmptyLong(mw.getMatnr_id())) {
			error += "Выберите материал" + "\n";
		}

		if (mw.getFrom_date() == null) {
			error += "Введите время от";
		}

		return error;
	}

	@Override
	public void update(MatnrWar mw) throws DAOException {
		String error = validate(mw, false);

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		mwDao.update(mw);
	}

}