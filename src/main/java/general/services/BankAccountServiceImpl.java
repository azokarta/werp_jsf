package general.services;
 
import java.util.Calendar;
import java.util.List;

import general.dao.BankAccountDao;
import general.dao.DAOException;
import general.tables.BankAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("bankAccountService")
public class BankAccountServiceImpl implements BankAccountService{
	
	@Autowired
    private BankAccountDao dao;

	@Override
	public void create(BankAccount ba) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	private String validate(BankAccount ba, boolean isNew){
		String error = "";
		if(ba.getBank_id() == 0L){
			error += "Выберите банк" + "\n";
		}
		
		if(ba.getCurrency().isEmpty()){
			error += "Выберите валюту" + "\n";
		}
		
		if(ba.getIban().isEmpty()){
			error += "Напишите IBAN" + "\n";
		}
		
		if(ba.getCustomer_id() == 0L){
			error += "Выберите контрагента" + "\n";
		}
		
		if(isNew){
			ba.setCreated_date(Calendar.getInstance().getTime());
		}
		
		return error;
	}
	
	@Override
	public void create(List<BankAccount> baList) throws DAOException {
		String error = "";
		for(BankAccount ba:baList){
			error = this.validate(ba, true);
			if(error.length() > 0){
				throw new DAOException(error);
			}
		}
	}

	@Override
	public void update(BankAccount ba) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(List<BankAccount> baList) throws DAOException {
		String error = "";
		for(BankAccount ba:baList){
			error = this.validate(ba, false);
			if(error.length() > 0){
				throw new DAOException(error);
			}
		}
	}	
}
