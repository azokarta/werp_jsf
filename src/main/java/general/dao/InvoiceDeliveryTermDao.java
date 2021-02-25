package general.dao;

import java.util.List;

import general.tables.InvoiceDeliveryTerm;

public interface InvoiceDeliveryTermDao extends GenericDao<InvoiceDeliveryTerm> {
	public List<InvoiceDeliveryTerm> findAll(String condition);
}
