package general.springservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.DAOException;
import general.dao2.MatnrLimitDao;
import general.tables2.MatnrLimit;

@Service("matnrLimitService")
public class MatnrLimitServiceImpl implements MatnrLimitService {

	@Autowired
	MatnrLimitDao mlDao;

	@Override
	public void create(MatnrLimit ml) throws DAOException {
		validate(ml);
		mlDao.create(ml);
	}

	private void validate(MatnrLimit ml) throws DAOException {
		String error = "";
		if (Validation.isEmptyString(ml.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(ml.getBranchId())) {
			error += "Выберите филиал \n";
		}

		if (ml.getMatnrLimitItems().size() == 0) {
			error += "Выберите материалы для лимита \n";
		}

		if (ml.isNew()) {
			List<MatnrLimit> exists = mlDao.findAll(ml.getBukrs(), ml.getBranchId(), ml.getPositionId());
			for (MatnrLimit eml : exists) {
				if (ml.getPositionId().equals(eml.getPositionId())) {
					error += "В системе уже имеется данные, отредактируйте их \n";
					break;
				}
			}
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
	}

	@Override
	public void update(MatnrLimit ml) throws DAOException {
		validate(ml);
		mlDao.update(ml);
	}

	@Override
	public void delete(Long id) throws DAOException {
		mlDao.delete(id);
	}

	@Override
	public void create(List<MatnrLimit> mlList) throws DAOException {
		if (mlList.size() == 0) {
			throw new DAOException("Данные пусты");
		}
		for (MatnrLimit ml : mlList) {
			validate(ml);
			mlDao.create(ml);
		}
	}

	@Override
	public void delete(List<Long> ids) throws DAOException {
		for (Long id : ids) {
			mlDao.delete(id);
		}
	}

}
