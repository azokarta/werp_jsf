package general.services;

import general.dao.DAOException;
import general.tables.Bank;

import org.springframework.transaction.annotation.Transactional;

public interface BankService {
	@Transactional
	void create(Bank b) throws DAOException;
	
	@Transactional
	void update(Bank b) throws DAOException;
}
