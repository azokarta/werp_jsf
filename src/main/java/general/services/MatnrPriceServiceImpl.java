package general.services;

import java.util.Calendar;
import java.util.List;

import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrPriceArcDao;
import general.dao.MatnrPriceDao;
import general.tables.MatnrPrice;
import general.tables.MatnrPriceArc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("matnrPriceService")
public class MatnrPriceServiceImpl implements MatnrPriceService {

	@Autowired
	MatnrPriceDao mpDao;

	@Autowired
	MatnrPriceArcDao mpaDao;

	@Override
	public void create(MatnrPrice mp) throws DAOException {
		String error = this.validate(mp, true);
		String cond = String
				.format("matnr = '%s' AND customer_id = %d AND bukrs = '%s' AND country_id = %d AND waers = '%s' ",
						mp.getMatnr(), mp.getCustomer_id(), mp.getBukrs(),
						mp.getCountry_id(),mp.getWaers());
		List<MatnrPrice> oldPrices = mpDao.findAll(cond);
		if(oldPrices.size() > 0){
			error += "Цена материала с такими параметрами добавлена ранее. Чтобы изменить цену, просто отредактируйте -_- " + "\n";
		}
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		mpDao.create(mp);
	}

	private String validate(MatnrPrice mp, boolean isNew) {
		String error = "";
		if (Validation.isEmptyString(mp.getBukrs())) {
			error += "Выберите компанию" + "\n";
		}

		if (mp.getCountry_id() == null || mp.getCountry_id() == 0) {
			error += "Выберите страну" + "\n";
		}

		if (mp.getCustomer_id() == null || mp.getCustomer_id() == 0) {
			error += "Выберите поставщика" + "\n";
		}

		if (mp.getMatnr() == null || mp.getMatnr() == 0) {
			error += "Выберите материал" + "\n";
		}

		if (mp.getPrice() == null || mp.getPrice() == 0) {
			error += "Цена должна быть больше нуля" + "\n";
		}

		if (Validation.isEmptyString(mp.getWaers())) {
			error += "Выберите Валюту" + "\n";
		}

		if (isNew) {
			mp.setCreated_date(Calendar.getInstance().getTime());
		}
		mp.setUpdated_date(Calendar.getInstance().getTime());

		return error;
	}

	@Override
	public void update(MatnrPrice mp) throws DAOException {
		String error = this.validate(mp, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		MatnrPrice oldPrice = mpDao.find(mp.getMp_id());
		if (oldPrice != null) {
			if (oldPrice.isSameMatnr(mp)) {
				if (!oldPrice.getPrice().equals(mp.getPrice())) {
					MatnrPriceArc arcMp = new MatnrPriceArc();
					arcMp.setBukrs(oldPrice.getBukrs());
					arcMp.setCountry_id(oldPrice.getCountry_id());
					arcMp.setCreated_by(mp.getUpdated_by());
					arcMp.setCreated_date(mp.getUpdated_date());
					arcMp.setCustomer_id(oldPrice.getCustomer_id());
					arcMp.setMatnr(oldPrice.getMatnr());
					arcMp.setPrice(oldPrice.getPrice());
					arcMp.setUpdated_by(mp.getUpdated_by());
					arcMp.setUpdated_date(mp.getUpdated_date());
					arcMp.setWaers(oldPrice.getWaers());
					mpaDao.create(arcMp);
				}
			}
		}

		mpDao.update(mp);
	}

}