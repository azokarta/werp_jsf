package general.tables.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import general.tables.Contract;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;

public class LogReport6 {

	Contract contract;
	private List<Matnr> promotions;
	List<Invoice> matnrList = new ArrayList<>();
	List<Invoice> promotionDocs = new ArrayList<>();
	List<Invoice> invoiceDocs = new ArrayList<>();
	Map<Long, List<InvoiceItem>> invoiceItemsMap;

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public List<Matnr> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<Matnr> promotions) {
		this.promotions = promotions;
	}

	public List<Invoice> getInvoiceDocs() {
		return invoiceDocs;
	}

	public void setInvoiceDocs(List<Invoice> invoiceDocs) {
		this.invoiceDocs = invoiceDocs;
	}

	public Map<Long, List<InvoiceItem>> getInvoiceItemsMap() {
		return invoiceItemsMap;
	}

	public void setInvoiceItemsMap(Map<Long, List<InvoiceItem>> invoiceItemsMap) {
		this.invoiceItemsMap = invoiceItemsMap;
	}

	public List<InvoiceItem> getInvoiceItems(Long invoiceId) {
		return invoiceItemsMap.get(invoiceId);
	}

	public List<Invoice> getMatnrList() {
		return matnrList;
	}

	public void setMatnrList(List<Invoice> matnrList) {
		this.matnrList = matnrList;
	}

	public List<Invoice> getPromotionDocs() {
		return promotionDocs;
	}

	public void setPromotionDocs(List<Invoice> promotionDocs) {
		this.promotionDocs = promotionDocs;
	}

}
