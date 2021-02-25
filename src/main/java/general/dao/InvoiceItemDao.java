package general.dao;

import java.util.Date;
import java.util.List;

import general.output.tables.InvoiceItemWithMatnr;
import general.tables.InvoiceItem;

public interface InvoiceItemDao extends GenericDao<InvoiceItem>{
	public List<InvoiceItem> findAll(String condition);
	public List<InvoiceItemWithMatnr> findWithMatnr(String c);
	
	public void deleteByCondition(String cond);
	
	public void updateByQuery(String query);
	
	public List<InvoiceItem> findQuantitySumGrouppedByMatnr(String condition);
	
	public List<InvoiceItem> findAllForRep1(String barcode);
	
	public List<InvoiceItem> findStaffItems(Long staffId);
	
	public List<Object[]> getWerksBalance(Long werks,Long matnr, Date toDate);
	
	public List<InvoiceItem> getWerksItemsWithInvoices(Long werks,Long matnr,Date fromDate, Date toDate);
}
