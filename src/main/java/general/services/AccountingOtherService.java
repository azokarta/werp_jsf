package general.services;

import general.dao.DAOException; 
import general.tables.ExchangeRate; 
import org.springframework.transaction.annotation.Transactional;

public interface AccountingOtherService {
	@Transactional
	void createNewExchangeRate(ExchangeRate a_er) throws DAOException;
}
