package general.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import general.tables.Invoice;
import general.tables.InvoiceItem;

public interface InvoiceDao extends GenericDao<Invoice> {
	public List<Invoice> findAll(String condition);

	public List<Invoice> findInvoiceNotPricedFkage(String condition);

	public List<InvoiceItem> findInvoiceItemsByParentInvoice(Long parentId, String childContext, Integer statusId);

	public List<InvoiceItem> findForWerksLogReport(Long werks, Date fromDate, Date toDate);

	public void deleteByCondition(String cond);

	public Invoice findParentInvoice(Long id);

	public int getRowCount(String cond);

	public List<Invoice> findAllLazy(String cond, int first, int pageSize);

	public Map<String, List<InvoiceItem>> getContractItemsMappedByType(Long contractNumber);

	public List<InvoiceItem> getContractWrittenOffItemsByContractNumber(Long contractNumber);

	public Double findCountWrittenOffMatnrsFromContract(Long contractNumber, Long matnr);

	Long getMaxRegNumber(Integer typeId);

	/************** REPORTS **************************/

	public List<Object[]> findLogReport3Data(Long werks, Long staffId, Date fromDate, Date toDate, Long matnr);

	public List<InvoiceItem> findLogReport3Data2(Long werks, Long staffId, Date fromDate, Date toDate, Long matnr);

	public List<Object[]> findLogReport4Data(Long werks, Long staffId, Date fromDate, Date toDate, Long matnr);

	public List<Object[]> findAllTemp(String cond);

	public List<Object[]> findWerksLogData(Long werks, Date fromDate, Date toDate, Integer typeId, Long staffId);
}
