package general.services;

import java.util.List;

import general.Validation;
import general.dao.DAOException;
import general.dao.PositionDao;
import general.tables.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("positionService")
public class PositionServiceImpl implements PositionService {

	@Autowired
	PositionDao posDao;

	@Override
	public void create(Position position) throws DAOException {
		String error = validate(position, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		posDao.create(position);
	}

	private String validate(Position pos, boolean isNew) {
		String error = "";

		if (Validation.isEmptyString(pos.getText())) {
			error += "Названия обязательно для заполнения";
		} else {
			pos.setText(pos.getText().trim());
			if (Validation.isEmptyString(pos.getText())) {
				error += "Названия обязательно для заполнения";
			} else if (isNew) {
				List<Position> l = posDao.findAll(" text = '" + pos.getText()
						+ "' ");
				if (l.size() > 0) {
					error += "Позиция с таким названием уже имеется в базе";
				}
			}
		}

		return error;
	}

	@Override
	public void update(Position position) throws DAOException {
		String error = validate(position, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		posDao.update(position);
	}

}