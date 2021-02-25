package general.dao;

import java.util.List;

import general.tables.InvoicePaymentTerm;

public interface InvoicePaymentTermDao extends GenericDao<InvoicePaymentTerm> {
	public List<InvoicePaymentTerm> findAll(String condition);
}
