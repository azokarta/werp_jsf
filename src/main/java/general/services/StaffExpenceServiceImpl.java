package general.services;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.tables.StaffExpence;
import general.dao.DAOException;
import general.dao.StaffExpenceDao;

@Service("staffExpenceService")
public class StaffExpenceServiceImpl implements StaffExpenceService {
	@Autowired
	private StaffExpenceDao dao;

	@Override
	public void create(StaffExpence s) throws DAOException {
		String error = this.validate(s);
		if(error.length() > 0){
			throw new DAOException(error);
		}

		dao.create(s);
	}
	
	private String validate(StaffExpence s){
		String error = "";
		if(Validation.isEmptyString(s.getBukrs())){
			error += "Выберите компанию \n";
		}
		if(s.getStaff_id() == 0L){
			error += "Сотрудник не выбран \n";
		}
		if(s.getEt_id() == 0L){
			error += "Тип не выбран \n";
		}
		
		if(s.getCurrency().length() == 0){
			error += "Валюта не выбран \n";
		}
		
		if(s.getSum() == 0D){
			error += "Сумма должна быть больше 0 \n";
		}
		
		
		s.setCreated_date(Calendar.getInstance().getTime());
		s.setUpdated_date(Calendar.getInstance().getTime());
		return error;
	}

	@Override
	public void delete(StaffExpence se) throws DAOException {
		se.setIs_deleted(1);
		se.setDeleted_date(Calendar.getInstance().getTime());
		dao.update(se);
	}
}
