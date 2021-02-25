package general.services;

import java.util.List;

import general.Validation;
import general.dao.DAOException;
import general.dao.TransactionDao;
import general.tables.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService{
	@Autowired
	TransactionDao dao;

	@Override
	public List<Transaction> showAllTransaction() throws DAOException {
		try {
			return dao.getAllTransaction(); 
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public Transaction getByTransactionCode(String transactionCode)
	{
		try {
			return dao.getByTransactionCode(transactionCode); 
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public void createTransaction(Transaction t) throws DAOException {
		String error = validateTransaction(t,true);
		if(error.length() > 0)
		{
			throw new DAOException(error);
		}
		
		dao.create(t);
	}
	
	private String validateTransaction(Transaction t, boolean isNew)
	{
		String error = "";
		if(Validation.isEmptyString(t.getTransaction_code()))
		{
			error += "Please, enter transaction code";
		}
		else
		{
			try{
				Transaction temp = this.getByTransactionCode(t.getTransaction_code());
				if(isNew)
				{
					error += "Record with this code exists";
				}
				else if(temp.getTransaction_id() != t.getTransaction_id())
				{
					//error += "Record with this code exists";
				}
			}
			catch(DAOException e)
			{
				
			}
		}
		
		if(Validation.isEmptyString(t.getName_ru()))
		{
			error += "Please, enter Name Ru";
		}
		
		if(Validation.isEmptyString(t.getUrl()))
		{
			error += "Please, enter url";
		}
		
		return error;
	}

	@Override
	public void updateTransaction(Transaction t) throws DAOException {
		String error = validateTransaction(t,false);
		if(error.length() > 0)
		{
			throw new DAOException(error);
		}
		
		dao.update(t);
	}
}
