package general.services;

import general.dao.DAOException;
import general.tables.InvoicePaymentTerm;

import org.springframework.transaction.annotation.Transactional;

public interface IPTService {
	@Transactional
	void create(InvoicePaymentTerm ipt) throws DAOException;
	
	@Transactional
	void update(InvoicePaymentTerm ipt) throws DAOException;
}
