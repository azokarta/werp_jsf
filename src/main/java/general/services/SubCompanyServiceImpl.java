package general.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import general.dao.SubCompanyDao;
import general.tables.SubCompany;
import general.dao.DAOException;

@Service("subCompanyService")
public class SubCompanyServiceImpl implements SubCompanyService {
	@Autowired
	private SubCompanyDao scDao;


	@Override
	public void create(SubCompany sc) throws DAOException {
		String error = validate(sc, true);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		scDao.create(sc);
	}
	
	private String validate(SubCompany sc,boolean isNew)
	{
		String error = "";
		if(sc.getName_ru().length() == 0)
		{
			error += "Заполните наименование (рус)" + "\n";
		}
		if(isNew){
			sc.setCreated_date(Calendar.getInstance().getTime());
		}
		sc.setUpdated_date(Calendar.getInstance().getTime());
		
		return error;
	}
	
	@Override
	public void update(SubCompany sc) throws DAOException {
		String error = validate(sc, false);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		scDao.update(sc);
	}
}
