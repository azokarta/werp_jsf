package general.services;
 
import java.util.Calendar;

import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.ExchangeRateDao; 
import general.tables.ExchangeRate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("accountingOtherService")
public class AccountingOtherServiceImpl implements AccountingOtherService{
	@Autowired
    private ExchangeRateDao erDao;
	
	public void createNewExchangeRate(ExchangeRate a_er) throws DAOException{
		try{
			Calendar cal = Calendar.getInstance();
			a_er.setExrate_date(cal.getTime());
			if (a_er.getSecondary_currency().equals("0") || a_er.getSecondary_currency().equals(null)){
				throw new DAOException("Выберите валюту"); 
			}
			
			if (a_er.getType()!=1 && a_er.getType()!=2)
			{
				throw new DAOException("Выберите тип курса"); 
			}
			
			if (a_er.getType()==2 && (a_er.getBukrs()==null || a_er.getBukrs().length()<1))
			{
				throw new DAOException("Выберите компанию.");
			}
			else if (a_er.getType()==1)
			{
				a_er.setBukrs(null);
			}
			
			if (a_er.getSc_value()<0 || a_er.getSc_value()==0){
				throw new DAOException("Значение не может быть 0 или меньше"); 
			}
			a_er.setExrate_date(GeneralUtil.removeTime(a_er.getExrate_date()));
			erDao.create(a_er);
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}	
}
