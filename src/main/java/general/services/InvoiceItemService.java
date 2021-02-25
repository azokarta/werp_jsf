package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.InvoiceItem;

public interface InvoiceItemService {
	
	@Transactional
	public void create(List<InvoiceItem> items) throws DAOException;
}