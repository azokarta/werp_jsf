package general.services;

import java.util.Calendar;

import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrPriceArcDao;
import general.dao.MatnrPriceDao;
import general.tables.MatnrPrice;
import general.tables.MatnrPriceArc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("matnrPriceArcService")
public class MatnrPriceArcServiceImpl implements MatnrPriceArcService {

	@Autowired
	MatnrPriceArcDao mpDao;

	@Override
	public void create(MatnrPriceArc mp) throws DAOException {
		String error = this.validate(mp, true);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		mpDao.create(mp);
	}
	
	private String validate(MatnrPriceArc mp, boolean isNew){
		String error = "";
		if(Validation.isEmptyString(mp.getBukrs())){
			error += "Выберите компанию" + "\n";
		}
		
		if(mp.getCountry_id() == null || mp.getCountry_id() == 0){
			error += "Выберите страну" + "\n";
		}
		
		if(mp.getCustomer_id() == null || mp.getCustomer_id() == 0){
			error += "Выберите поставщика" + "\n";
		}
		
		if(mp.getMatnr() == null || mp.getMatnr() == 0){
			error += "Выберите материал" + "\n";
		}
		
		if(mp.getPrice() == null || mp.getPrice() == 0){
			error += "Цена должна быть больше нуля" + "\n";
		}
		
		if(Validation.isEmptyString(mp.getWaers())){
			error += "Выберите Валюту" + "\n";
		}
		
		if(isNew){
			mp.setCreated_date(Calendar.getInstance().getTime());
		}
		mp.setUpdated_date(Calendar.getInstance().getTime());
		
		return error;
	}
	
}