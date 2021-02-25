package general.services;

import general.dao.DAOException;
import general.tables.InvoiceDeliveryTerm;

import org.springframework.transaction.annotation.Transactional;

public interface IDTService {
	@Transactional
	void create(InvoiceDeliveryTerm idt) throws DAOException;
	
	@Transactional
	void update(InvoiceDeliveryTerm idt) throws DAOException;
}
