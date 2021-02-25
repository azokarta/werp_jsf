package general.services;
 
import java.util.Calendar;
import general.dao.BankDao;
import general.dao.DAOException;
import general.tables.Bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("bankService")
public class BankServiceImpl implements BankService{
	
	@Autowired
    private BankDao dao;
	

	private String validate(Bank b, boolean isNew)
	{
		String error = "";
		if(b.getName_ru().isEmpty()){
			error += "Заполниете название (Ру) \n";
		}
		
		if(b.getSwift_code().isEmpty()){
			error += "Заполните Swift Code \n";
		}
		
		if(isNew){
			b.setCreated_date(Calendar.getInstance().getTime());
		}
		b.setUpdated_date(Calendar.getInstance().getTime());
		return error;
	}
	
	@Override
	public void create(Bank b) throws DAOException {
		
		String error = validate(b, true);
		if(error.length() > 0)
		{
			throw new DAOException(error);
		}
		
		dao.create(b);
	}
	@Override
	public void update(Bank b) throws DAOException {
		String error = validate(b, false);
		if(error.length() > 0)
		{
			throw new DAOException(error);
		}
		
		dao.update(b);
	}	
}
