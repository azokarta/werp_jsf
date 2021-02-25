package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.BankAccount;

import org.springframework.transaction.annotation.Transactional;

public interface BankAccountService {
	@Transactional
	void create(BankAccount ba) throws DAOException;
	
	@Transactional
	void create(List<BankAccount> baList) throws DAOException;
	
	@Transactional
	void update(BankAccount ba) throws DAOException;
	
	@Transactional
	void update(List<BankAccount> baList) throws DAOException;
}
