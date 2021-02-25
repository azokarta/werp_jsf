package general.services;

import general.Validation;
import general.dao.DAOException;
import general.dao.StateDao;
import general.tables.State;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("stateService")
public class StateServiceImpl implements StateService {

	@Autowired
	StateDao stateDao;

	@Override
	public void create(State s) throws DAOException {
		String error = validate(s, true);

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		stateDao.create(s);
	}

	private String validate(State s, boolean isNew) {
		String error = "";

		if (Validation.isEmptyLong(s.getCountryid())) {
			error += "Выберите страну \n";
		}

		if (Validation.isEmptyString(s.getStatename())) {
			error += "Напишите название \n";
		}

		return error;
	}

	@Override
	public void update(State s) throws DAOException {
		String error = validate(s, false);

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		stateDao.update(s);
	}
}