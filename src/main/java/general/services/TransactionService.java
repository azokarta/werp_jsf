package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.Transaction;

public interface TransactionService {
	@Transactional
	List<Transaction> showAllTransaction() throws DAOException;
	
	@Transactional
	Transaction getByTransactionCode(String transactionCode) throws DAOException;
	
	@Transactional
	void createTransaction(Transaction t) throws DAOException;
	
	@Transactional
	void updateTransaction(Transaction t) throws DAOException;
}
