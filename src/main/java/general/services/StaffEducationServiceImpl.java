package general.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.DAOException;
import general.dao.StaffEducationDao;
import general.tables.StaffEducation;

@Service("staffEducationService")
public class StaffEducationServiceImpl implements StaffEducationService {

	@Autowired StaffEducationDao seDao;
	
	@Override
	public void create(StaffEducation e) throws DAOException {
		// TODO Auto-generated method stub
		String error = validate(e);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		seDao.create(e);
	}
	
	private String validate(StaffEducation e){
		String error = "";
		if(Validation.isEmptyString(e.getInstitutionName())){
			error += "Поле УЗ обязательно для заполнения";
		}
		
		if(Validation.isEmptyString(e.getFaculty())){
			//error += "Поле Факультет обязательно для заполнения";
		}
		
		if(e.getBeginYear() == 0){
			error += "Поле Дата начало обязательно для заполнения";
		}
		
		if(Validation.isEmptyLong(e.getStaffId())){
			error += "Поле Сотрудник обязательно для заполнения";
		}
		
		return error;
	}

	@Override
	public void update(StaffEducation e) throws DAOException {
		String error = validate(e);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		seDao.update(e);
	}

	@Override
	public void delete(StaffEducation e) throws DAOException {
		seDao.delete(e.getSeId());
	}
	
	
	
	
}