package general.services;

import general.Validation;
import general.dao.DAOException;
import general.dao.WerksDao;
import general.tables.Werks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("werksService")
public class WerksServiceImpl implements WerksService {
	
	@Autowired
	WerksDao dao;

	@Override
	public void create(Werks w) throws DAOException {
		String e = validate(w);
		if(e.length() > 0){
			throw new DAOException(e);
		}
		
		dao.create(w);
	}
	
	private String validate(Werks w){
		String error = "";
		if(Validation.isEmptyString(w.getText45())){
			error += "Поле Название обязательно для заполнения " + "\n";
		}
		
		if(Validation.isEmptyString(w.getBukrs())){
			error += "Поле Компания обязательно для заполнения " + "\n";
		}
		
		return error;
	}

	@Override
	public void update(Werks w) throws DAOException {
		String e = validate(w);
		if(e.length() > 0){
			throw new DAOException(e);
		}
		
		dao.update(w);
	}
}
