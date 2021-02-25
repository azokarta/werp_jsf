package general.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import general.dao.InvoiceItemDao;
import general.output.tables.InvoiceItemWithMatnr;
import general.tables.Invoice;
import general.tables.InvoiceItem;

import org.springframework.stereotype.Component;

@Component("invoiceItemDao")
public class InvoiceItemDaoImpl extends GenericDaoImpl<InvoiceItem>implements InvoiceItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> findAll(String c) {
		String s = " SELECT i FROM InvoiceItem i ";
		if (c.length() > 0) {
			s += " WHERE " + c;
		}

		Query query = this.em.createQuery(s);
		return query.getResultList();
	}

	public List<InvoiceItemWithMatnr> findWithMatnr(String c) {
		String s = " SELECT 1," + "sum(ml.menge)," + "m.matnr," + "m.type," + "m.text45,"
				+ "m.parent_matnr FROM MatnrList ml,Matnr m where ml.matnr=m.matnr and ml.awkey is null ";
		if (c.length() > 0) {
			s += " and " + c;
		}
		s = s + " group by m.matnr,m.type,m.text45,m.parent_matnr";
		// System.out.println(s);
		List<InvoiceItemWithMatnr> l_ilif = new ArrayList<InvoiceItemWithMatnr>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			InvoiceItemWithMatnr wa_ilif = new InvoiceItemWithMatnr();
			if (result[0] != null) {
				wa_ilif.setInvoice_item_id(0L);
			}
			if (result[1] != null) {
				wa_ilif.setMenge((double) result[1]);
			}
			if (result[2] != null) {
				wa_ilif.setMatnr((long) result[2]);
			}

			if (result[3] != null) {
				wa_ilif.setMatnr_type((int) result[3]);
			}

			if (result[4] != null) {
				wa_ilif.setMatnr_name(String.valueOf(result[4]));
			}

			if (result[5] != null) {
				wa_ilif.setParent_matnr((long) result[5]);
			}

			l_ilif.add(wa_ilif);
		}

		return l_ilif;
	}

	@Override
	public void deleteByCondition(String cond) {
		String s = " DELETE FROM InvoiceItem WHERE " + cond;
		em.createQuery(s).executeUpdate();
	}

	@Override
	public List<InvoiceItem> findQuantitySumGrouppedByMatnr(String condition) {
		String s = " SELECT sum(quantity), matnr FROM InvoiceItem ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		s += " GROUP BY matnr ";

		List<InvoiceItem> out = new ArrayList<InvoiceItem>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			InvoiceItem ii = new InvoiceItem();
			ii.setQuantity((Double) result[0]);
			ii.setMatnr((Long) result[1]);
			out.add(ii);
		}

		return out;
	}

	@Override
	public void updateByQuery(String query) {
		String s = " UPDATE InvoiceItem " + query;
		em.createQuery(s).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> findAllForRep1(String barcode) {
		String s = " SELECT i FROM InvoiceItem i WHERE barcode = :b";
		Query q = em.createQuery(s);
		q.setParameter("b", barcode);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> findStaffItems(Long staffId) {
		String s = " SELECT i FROM InvoiceItem i WHERE invoice_id IN( SELECT id FROM Invoice WHERE responsible_id = "
				+ staffId + ") ";
		Query q = em.createQuery(s, InvoiceItem.class);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getWerksBalance(Long werks, Long matnr, Date toDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String temp = "( (to_werks = %1$d AND type_id = " + Invoice.TYPE_POSTING_IN + " ) ";
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_SEND + " ) ";// Otpravka
		temp += "OR (to_werks = %1$d AND type_id = " + Invoice.TYPE_POSTING + " ) ";// Oprihodovania
		temp += "OR (to_werks = %1$d AND type_id = " + Invoice.TYPE_RETURN + " ) ";// Vozvrat
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_WRITEOFF + " ) ";// Spsisanie
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_WRITEOFF_LOSS + " ) ";// Spsisanie
		temp += ") ";
		String s = String.format(
				"SELECT ii.quantity, i.type_id,i.from_werks,i.to_werks,i.responsible_id FROM invoice_table_item1 ii, invoice_table i"
						+ " WHERE ii.invoice_id = i.id AND i.invoice_date < '" + sdf.format(toDate).toString() + "' "
						+ " AND " + temp + " AND ii.matnr = %2$d AND i.status_id = %3$d ",
				werks, matnr, Invoice.STATUS_DONE);
		Query q = em.createNativeQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> getWerksItemsWithInvoices(Long werks, Long matnr,Date fromDate, Date toDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = " SELECT ii, i FROM InvoiceItem ii, Invoice i WHERE ii.invoice_id = i.id AND ";
		s += String.format(
				" (from_werks = %1$d OR to_werks = %1$d) AND ii.matnr = %2$d AND status_id IN(%3$d,%4$d,%5$d) ",
				werks, matnr, Invoice.STATUS_DONE,Invoice.STATUS_MOVING,Invoice.STATUS_ARCHIVE);
		
		if(fromDate != null){
			s += " AND i.invoice_date >= '" + sdf.format(fromDate).toString() + "' ";
		}
		
		if(toDate != null){
			s += " AND i.invoice_date <= '" + sdf.format(toDate).toString() + "' ";
		}
		
		Query q = em.createQuery(s);
		List<Object[]> result = q.getResultList();
		List<InvoiceItem> out = new ArrayList<>();
		for (Object[] o : result) {
			InvoiceItem ii = (InvoiceItem) o[0];
			Invoice i = (Invoice) o[1];
			ii.setInvoiceObject(i);
			out.add(ii);
		}

		return out;
	}
}
