package general.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.tables.SalePlan;
import general.dao.DAOException;
import general.dao.SalePlanDao;

@Service("salePlanService")
public class SalePlanServiceImpl implements SalePlanService {
	@Autowired
	private SalePlanDao dao;

	@Override
	public void createSalePlan(SalePlan sp) throws DAOException {
		String error = validateSP(sp, true);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		dao.create(sp);
	}
	
	private String validateSP(SalePlan sp, boolean isNew){
		String error = "";
		if(sp.getBranch_id() == 0){
			error += "Поле Филиал обязательно для заполнение" + "\n";
		}
		if(sp.getPlan_count() == 0){
			error += "Поле План должно быть больше 0" + "\n";
		}
		
		if(isNew){
			sp.setCreated_date(Calendar.getInstance().getTime());
		}
		sp.setUpdated_date(Calendar.getInstance().getTime());
		return error;
	}
	
	@Override
	public void updateSalePlan(SalePlan sp) throws DAOException {
		// TODO Auto-generated method stub
		String error = validateSP(sp, false);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		dao.update(sp);
	}
	
}
