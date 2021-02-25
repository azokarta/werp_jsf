package general.services;


import general.Validation;
import general.dao.CityDao;
import general.dao.DAOException;
import general.tables.City;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cityService")
public class CityServiceImpl implements
		CityService {

	@Autowired CityDao cDao;
	
	@Override
	public void create(City city) throws DAOException {
		String e = validate(city);
		if(e.length() > 0){
			throw new DAOException(e);
		}
		
		cDao.create(city);
	}
	
	private String validate(City c){
		String error = "";
		
		if(Validation.isEmptyString(c.getName())){
			error += "Название не может быть пустым \n";
		}
		
		if(Validation.isEmptyLong(c.getCountryid())){
			error += "Выберите страну \n";
		}
		
		if(Validation.isEmptyLong(c.getStateid())){
			error += "Выберите область \n";
		}
		
		return error;
	}

	@Override
	public void update(City city) throws DAOException {
		String e = validate(city);
		if(e.length() > 0){
			throw new DAOException(e);
		}
		
		cDao.update(city);
	}

}