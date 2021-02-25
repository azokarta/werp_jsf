package general.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.RelatedDocsDao;
import general.dao.StaffDao;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.RelatedDocs;
import general.tables.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("invoiceDao")
public class InvoiceDaoImpl extends GenericDaoImpl<Invoice>implements InvoiceDao {

	@Autowired
	StaffDao stfDao;

	@Autowired
	RelatedDocsDao relDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Invoice> findAll(String c) {

		String s = " SELECT i FROM Invoice i ";
		if (c.length() > 0) {
			s += " WHERE " + c;
		}

		Query query = this.em.createQuery(s);
		List<Invoice> l = query.getResultList();
		return l;
	}

	@Override
	public List<Invoice> findInvoiceNotPricedFkage(String condition) {

		String s = " SELECT i2 FROM Invoice i2 where i2.id in (SELECT i.id FROM Invoice i,MatnrList m where m.invoice=i.id and m.awkey is null group by i.id) ";
		if (condition.length() > 0) {
			s += " and " + condition;
		}

		Query query = this.em.createQuery(s);
		List<Invoice> l = query.getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> findInvoiceItemsByParentInvoice(Long parentId, String childContext, Integer statusId) {
		String s = " SELECT SUM(quantity) AS quantity,matnr FROM InvoiceItem i ";
		s += " WHERE i.status_id = :status_id AND i.invoice_id IN( SELECT r.context_id FROM RelatedDocs r WHERE r.parent_id = :parent_id AND context=:context ) ) ";
		s += " GROUP BY i.matnr ";
		Query q = em.createQuery(s);
		q.setParameter("parent_id", parentId).setParameter("context", childContext).setParameter("status_id", statusId);

		List<InvoiceItem> out = new ArrayList<InvoiceItem>();
		List<Object[]> results = q.getResultList();
		for (Object[] result : results) {
			InvoiceItem ii = new InvoiceItem();
			ii.setQuantity((Double) result[0]);
			ii.setMatnr((Long) result[1]);
			out.add(ii);
		}
		return out;
	}

	@Override
	public Invoice find(Object id) {
		// TODO Auto-generated method stub
		Invoice out = super.find(id);
		if (out != null) {
			if (!Validation.isEmptyLong(out.getResponsible_id())) {
				Staff stf = stfDao.find(out.getResponsible_id());
				if (stf != null) {
					out.setResponsible(stf);
				}
			}
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> findForWerksLogReport(Long werks, Date fromDate, Date toDate) {
		// SimpleDateFormat sdf = new SimpleDateFormat();
		String[] statuses = { Invoice.STATUS_DONE.toString(), Invoice.STATUS_MOVING.toString() };
		String subQuery = String.format(
				" SELECT i.id FROM Invoice i WHERE (i.from_werks = %d OR i.to_werks = %d) AND i.status IN(%s) AND i.invoice_date > %s ",
				werks, werks, String.join(",", statuses), fromDate);
		if (toDate != null) {
			subQuery += String.format(" AND i.invoice_date < %s ", toDate);
		}
		String queryString = String.format(" SELECT ii FROM InvoiceItem ii WHERE ii.invoice_id IN( %s )", subQuery);
		Query q = em.createQuery(queryString);
		return q.getResultList();
	}

	@Override
	public void deleteByCondition(String cond) {
		String s = " DELETE FROM Invoice WHERE " + cond;
		em.createQuery(s).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Invoice findParentInvoice(Long id) {
		String s = "SELECT r FROM RelatedDocs r WHERE id IN(SELECT parent_id FROM RelatedDocs r2 WHERE r2.context_id = :c_id AND r2.context = :c) ";
		Query q = em.createQuery(s);
		q.setParameter("c_id", id);
		q.setParameter("c", Invoice.CONTEXT);

		List<RelatedDocs> rdList = q.getResultList();
		if (rdList != null && rdList.size() > 0) {
			return find(rdList.get(0).getContext_id());
		}
		return null;
	}

	@Override
	public int getRowCount(String cond) {
		String s = "SELECT COUNT(i.id) FROM Invoice i ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Invoice> findAllLazy(String condition, int first, int pageSize) {

		String s = "SELECT i FROM Invoice i ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}
		Query q = em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		return q.getResultList();
	}

	/*********** REPORTS ******************/

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findLogReport3Data(Long werks, Long staffId, Date fromDate, Date toDate, Long matnr) {

		String s = " SELECT id, invoice_date,type_id,status_id,service_number,contract_number, (SELECT SUM(quantity) FROM invoice_table_item1 WHERE invoice_id= i.id AND matnr=%d) AS q FROM invoice_table i "
				+ " WHERE id IN(SELECT invoice_id FROM invoice_table_item1 WHERE matnr=%d)" + " AND ( "
				+ "		(type_id = %d AND (status_id = %d OR status_id = %d) )" + "	OR "
				+ "		((type_id = %d OR type_id = %d OR type_id = %d ) AND status_id = %d ) " + ")";
		s = String.format(s, matnr, matnr, Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_DONE, Invoice.STATUS_NEW,
				Invoice.TYPE_WRITEOFF, Invoice.TYPE_ACCOUNTABILITY, Invoice.TYPE_ACCOUNTABILITY_RETURN,
				Invoice.STATUS_DONE);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (fromDate != null) {
			s += String.format(" AND invoice_date >= '%s' ", sdf.format(fromDate).toString());
		}

		if (toDate != null) {
			s += String.format(" AND invoice_date <= '%s' ", sdf.format(toDate).toString());
		}

		if (!Validation.isEmptyLong(staffId)) {
			s += " AND responsible_id = " + staffId;
		}

		Query q = em.createNativeQuery(s + "  ORDER BY invoice_date ASC  ");
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceItem> findLogReport3Data2(Long werks, Long staffId, Date fromDate, Date toDate, Long matnr) {

		// String s1 = " SELECT SUM(ii.quantity),ii.invoice_id FROM InvoiceItem
		// ii WHERE matnr";

		String s = " SELECT ii, i FROM InvoiceItem ii, Invoice i WHERE i.id = ii.invoice_id AND ii.matnr= " + matnr;
		// s += String.format(" AND ( from_werks = %1$d OR to_werks = %1$d )",
		// werks);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (fromDate != null) {
			s += String.format(" AND invoice_date >= '%s' ", sdf.format(fromDate).toString());
		}

		if (toDate != null) {
			s += String.format(" AND invoice_date <= '%s' ", sdf.format(toDate).toString());
		}

		if (!Validation.isEmptyLong(staffId)) {
			s += " AND responsible_id = " + staffId;
		}

		s += String.format(" AND type_id IN(%d,%d,%d,%d)", Invoice.TYPE_ACCOUNTABILITY,
				Invoice.TYPE_ACCOUNTABILITY_RETURN, Invoice.TYPE_WRITEOFF, Invoice.TYPE_WRITEOFF_DOC);

		s += "  ORDER BY invoice_date ASC, i.id ASC  ";
		// System.out.println(s);
		Query q = em.createQuery(s);

		List<InvoiceItem> out = new ArrayList<>();

		List<Object[]> result = q.getResultList();
		for (Object[] o : result) {
			InvoiceItem ii = (InvoiceItem) o[0];
			Invoice i = (Invoice) o[1];
			ii.setInvoiceObject(i);
			out.add(ii);
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAllTemp(String cond) {
		Query q = em.createNativeQuery(cond);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findLogReport4Data(Long werks, Long staffId, Date fromDate, Date toDate, Long matnr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = " SELECT i.responsible_id,ii.matnr,ii.barcode,ii.quantity,i.type_id FROM invoice_table i, invoice_table_item1 ii WHERE ii.invoice_id=i.id "
				+ " AND i.responsible_id IS NOT NULL AND i.responsible_id > 0 ";
		s += String.format(
				"AND ( ( i.type_id IN(%d, %d) AND i.from_werks = %d ) OR (i.type_id = %d AND i.to_werks = %d ) ) ",
				Invoice.TYPE_ACCOUNTABILITY, Invoice.TYPE_WRITEOFF, werks, Invoice.TYPE_ACCOUNTABILITY_RETURN, werks);

		if (fromDate != null) {
			s += String.format(" AND i.invoice_date >= '%s' ", sdf.format(fromDate).toString());
		}

		if (toDate != null) {
			s += String.format(" AND i.invoice_date <= '%s' ", sdf.format(toDate).toString());
		}

		s += " AND status_id = " + Invoice.STATUS_DONE;

		s += " ORDER BY i.invoice_date ASC ";
		Query q = em.createNativeQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<InvoiceItem>> getContractItemsMappedByType(Long contractNumber) {
		String s = "SELECT i.type_id, ii.invoice_id, ii.matnr, ii.quantity, ii.barcode, ii.done_quantity, i.status_id FROM invoice_table i, invoice_table_item1 ii"
				+ " WHERE i.id=ii.invoice_id AND i.contract_number = %d AND i.type_id IN(%d,%d,%d) AND i.status_id=%d";
		Query q = em.createNativeQuery(String.format(s, contractNumber, Invoice.TYPE_WRITEOFF,
				Invoice.TYPE_WRITEOFF_DOC, Invoice.TYPE_RETURN, Invoice.STATUS_DONE));
		List<Object[]> result = q.getResultList();
		Map<String, List<InvoiceItem>> out = new HashMap<>();
		// Map<String, Integer> tempMap = new HashMap<>();
		for (Object[] o : result) {
			Integer typeId = Integer.parseInt(String.valueOf(o[0]));
			Long invoiceId = Long.parseLong(String.valueOf(o[1]));
			Long matnr = Long.parseLong(String.valueOf(o[2]));
			Double qty = Double.parseDouble(String.valueOf(o[3]));
			String barcode = String.valueOf(o[4]);
			Double doneQty = Double.parseDouble(String.valueOf(o[5]));
			Integer statusId = Integer.parseInt(String.valueOf(o[6]));

			String tempKey = typeId + "-" + statusId;

			// Invoice in = new Invoice();
			// in.setId(invoiceId);
			// in.setStatus_id(statusId);
			InvoiceItem ii = new InvoiceItem(invoiceId, matnr, qty, barcode);
			ii.setDone_quantity(doneQty);
			// ii.setInvoiceObject(in);

			List<InvoiceItem> iiList = new ArrayList<>();
			if (out.containsKey(tempKey)) {
				iiList = out.get(tempKey);
			}
			iiList.add(ii);
			out.put(tempKey, iiList);

			// if(Validation.isEmptyString(s))
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItem> getContractWrittenOffItemsByContractNumber(Long contractNumber) {
		String s = "SELECT ii.invoice_id, ii.matnr, ii.quantity, ii.barcode,ii.ml_ids FROM invoice_table i, invoice_table_item1 ii"
				+ " WHERE i.id=ii.invoice_id AND i.contract_number = %d AND i.type_id=%d AND i.status_id=%d";
		Query q = em.createNativeQuery(String.format(s, contractNumber, Invoice.TYPE_WRITEOFF, Invoice.STATUS_DONE));

		List<Object[]> result = q.getResultList();
		List<InvoiceItem> out = new ArrayList<>();
		for (Object[] o : result) {
			Long invoiceId = Long.parseLong(String.valueOf(o[0]));
			Long matnr = Long.parseLong(String.valueOf(o[1]));
			Double qty = Double.parseDouble(String.valueOf(o[2]));
			String barcode = (o[3] == null) ? null : String.valueOf(o[3]);
			String mlIds = (o[4] == null ) ? null : String.valueOf(o[4]);
			InvoiceItem ii = new InvoiceItem(invoiceId, matnr, qty, barcode);
			ii.setMl_ids(mlIds);
			out.add(ii);
		}
		return out;
	}

	@Override
	public Long getMaxRegNumber(Integer typeId) {
		String s = " SELECT MAX(reg_number) FROM Invoice WHERE type_id = " + typeId;
		Query query = this.em.createQuery(s);
		return (Long) query.getSingleResult();
	}

	@Override
	public Invoice create(Invoice t) {
		Long maxRegNumber = 0L;
		try {
			maxRegNumber = Long.valueOf(getMaxRegNumber(t.getType_id()));
		} catch (Exception e) {
			maxRegNumber = 0L;
		}
		t.setReg_number(maxRegNumber + 1);
		return super.create(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findWerksLogData(Long werks, Date fromDate, Date toDate, Integer typeId, Long staffId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String[] statuses = { Invoice.STATUS_DONE.toString(), Invoice.STATUS_MOVING.toString() };

		String temp = "( (to_werks = %1$d AND type_id = " + Invoice.TYPE_POSTING_IN + " ) ";// Vnutr
																							// Opr
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_SEND + " ) ";// Otpravka
		temp += "OR (to_werks = %1$d AND type_id = " + Invoice.TYPE_POSTING + " ) ";// Oprihodovania
		temp += "OR (to_werks = %1$d AND type_id = " + Invoice.TYPE_RETURN + " ) ";// Vozvrat
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_ACCOUNTABILITY + " ) ";// Podotchet
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_WRITEOFF + " ) ";// Spsisanie
		temp += "OR (to_werks = %1$d AND type_id = " + Invoice.TYPE_ACCOUNTABILITY_RETURN + " ) ";// Возврат
																									// с
																									// подотчета
		temp += "OR (from_werks = %1$d AND type_id = " + Invoice.TYPE_WRITEOFF_LOSS + " ) ";
		temp += ") ";

		if (typeId != null && typeId > 0) {
			temp = " ( from_werks = %1$d OR to_werks = %1$d ) AND type_id = " + typeId + " ";
		}

		if (!Validation.isEmptyLong(staffId)) {
			temp += " AND responsible_id = " + staffId;
		}

		temp += " AND status_id IN(%2$s) AND invoice_date >= '%3$s' ";
		String addCondition = String.format(temp, werks, String.join(",", statuses), sdf.format(fromDate).toString());
		if (toDate != null) {
			addCondition += String.format(" AND invoice_date <= '%s' ", sdf.format(toDate).toString());
		}

		String s = " SELECT ii, i FROM InvoiceItem ii, Invoice i WHERE i.id=ii.invoice_id AND " + addCondition
				+ " ORDER BY invoice_date ASC ";

		Query q = em.createQuery(s);

		List<Object[]> r = q.getResultList();
		return r;
	}

	/**
	 * @param fromWerks
	 *            - Склад откуда списан материал
	 * @param contractNumber
	 *            - номер договора, по которому списан материал
	 * @param matnr
	 *            - материал
	 * @return
	 */
	@Override
	public Double findCountWrittenOffMatnrsFromContract(Long contractNumber, Long matnr) {
		String s = String.format(
				" SELECT SUM(quantity) AS quantity FROM invoice_table i, invoice_table_item1 ii WHERE i.id=ii.invoice_id AND i.type_id=%d AND i.status_id=%d AND i.contract_number=%d AND ii.matnr=%d",
				Invoice.TYPE_WRITEOFF, Invoice.STATUS_DONE, contractNumber, matnr);
		Query q = em.createNativeQuery(s);
		BigDecimal bd = (BigDecimal) q.getSingleResult();
		if (bd == null) {
			return 0D;
		}
		return bd.doubleValue();
	}
}
