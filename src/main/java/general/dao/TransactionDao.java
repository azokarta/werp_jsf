package general.dao;

import java.util.List;

import general.tables.Transaction;

public interface TransactionDao extends GenericDao<Transaction>{
	public List<Transaction> getAllTransaction();
	public Transaction getByTransactionCode(String transactionCode);
	
	public List<Transaction> findAll(String condition);
}
