package general.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;

@Component("matnrListDao")
public class MatnrListDaoImpl extends GenericDaoImpl<MatnrList>implements MatnrListDao {

	@Autowired
	MatnrDao matnrDao;

	public List<MatnrList> dynamicFind(String a_dynamicWhere) {
		String s = "select matnr, sum(menge), sum(dmbtr), werks FROM MatnrList b WHERE status != '"
				+ MatnrList.STATUS_SOLD + "' ";
		if (a_dynamicWhere.length() > 0) {
			s += " AND " + a_dynamicWhere;
		}
		s += " group by matnr, werks";
		List<MatnrList> l_ml = new ArrayList<MatnrList>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			MatnrList wa_ml = new MatnrList();
			// wa_ml.setMatnr_list_id((Long)result[0]);
			wa_ml.setMatnr((long) result[0]);
			wa_ml.setMenge((double) result[1]);
			wa_ml.setDmbtr((double) result[2]);
			wa_ml.setWerks((long) result[3]);
			l_ml.add(wa_ml);
		}
		return l_ml;
	}

	public List<MatnrList> dynamicFind2(String a_dynamicWhere) {
		List<MatnrList> l_ml = new ArrayList<MatnrList>();
		Query query = this.em.createQuery(
				"select c FROM MatnrList c where status != '" + MatnrList.STATUS_SOLD + "' AND " + a_dynamicWhere);
		l_ml = query.getResultList();
		/*
		 * Query query = this.em .createQuery(
		 * "select matnr, menge, dmbtr FROM MatnrList b where "
		 * +a_dynamicWhere); List<Object[]> results = query.getResultList(); for
		 * (Object[] result : results) { MatnrList wa_ml = new MatnrList();
		 * wa_ml.setMatnr((long) result[0]); wa_ml.setMenge((double) result[1]);
		 * wa_ml.setDmbtr((double) result[2]); l_ml.add(wa_ml); }
		 */
		return l_ml;
	}

	public List<MatnrList> dynamicFind3(String a_dynamicWhere) {
		List<MatnrList> l_ml = new ArrayList<MatnrList>();
		String s = "select c FROM MatnrList c where status != '" + MatnrList.STATUS_SOLD + "'";
		if (a_dynamicWhere.length() > 0) {
			s += " AND " + a_dynamicWhere;
		}
		Query query = this.em.createQuery(s);
		l_ml = query.getResultList();
		return l_ml;
	}

	@Override
	public void updateStatus(String status, String condition) throws DAOException {
		String s = " UPDATE MatnrList m SET m.status = '" + status + "' WHERE " + condition;
		this.em.createQuery(s).executeUpdate();

	}

	@Override
	public void updateWerks(Long newWerks, String condition) throws DAOException {
		String s = " UPDATE MatnrList m SET m.werks = " + newWerks + " WHERE " + condition;
		this.em.createQuery(s).executeUpdate();
	}

	@Override
	public void updateMatnrs(String query) throws DAOException {
		if (query.length() == 0) {
			throw new DAOException("ERROR");
		}
		this.em.createQuery("UPDATE MatnrList m " + query).executeUpdate();
	}

	@Override
	public int findCount(Long matnr, Long werks) {
		// TODO Auto-generated method stub
		Query q = em.createQuery(
				String.format(" SELECT COUNT(*) FROM MatnrList WHERE matnr = %d AND werks = %d AND status != '%s' ",
						matnr, werks, MatnrList.STATUS_SOLD));
		return ((Long) q.getSingleResult()).intValue();
	}

	@Override
	public List<MatnrList> findAll(String cond, int max) {
		List<MatnrList> l_ml = new ArrayList<MatnrList>();
		String s = "select c FROM MatnrList c where status != '" + MatnrList.STATUS_SOLD + "'";
		if (cond.length() > 0) {
			s += " AND " + cond;
		}
		Query query = this.em.createQuery(s);
		if (max > 0) {
			query.setMaxResults(max);
		}
		l_ml = query.getResultList();
		return l_ml;
	}

	@Override
	public List<MatnrList> findAllWithStaff(String cond) {
		String s = " SELECT m1, m2 FROM MatnrList m1 LEFT JOIN m1.staff m2 ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<MatnrList> out = new ArrayList<MatnrList>();
		for (Object[] o : result) {
			MatnrList m1 = (MatnrList) o[0];
			Staff m2 = (Staff) o[1];
			if (m2 == null) {
				m1.setStaff(new Staff());
			} else {
				m1.setStaff(m2);
			}

			out.add(m1);
		}
		return out;
	}

	@Override
	public List<MatnrList> getGrouppedList(String condition) {
		String s = "SELECT SUM(menge),matnr,status FROM MatnrList b WHERE status != '" + MatnrList.STATUS_SOLD + "' ";
		if (condition.length() > 0) {
			s += " AND " + condition;
		}
		s += " GROUP BY matnr,status ";
		List<MatnrList> out = new ArrayList<MatnrList>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			MatnrList ml = new MatnrList();
			ml.setMenge((double) result[0]);
			ml.setMatnr((long) result[1]);
			ml.setStatus((String) result[2]);
			out.add(ml);
		}

		return out;
	}

	@Override
	public List<MatnrList> getGrouppedListForService(String condition) {
		String s = "SELECT SUM(menge), matnr, staff_id FROM MatnrList b WHERE status != '" + MatnrList.STATUS_SOLD
				+ "' ";
		if (condition.length() > 0) {
			s += " AND " + condition;
		}
		s += " GROUP BY staff_id, matnr";
		System.out.println("Query: " + s);
		List<MatnrList> out = new ArrayList<MatnrList>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			MatnrList ml = new MatnrList();
			ml.setMenge((double) result[0]);
			ml.setMatnr((long) result[1]);
			ml.setStaff_id((long) result[2]);
			out.add(ml);
		}

		return out;
	}

	public int updateMatnrListFkage(Long a_matnr, double a_summa, Long a_awkey, Long a_invoice_id, int a_rows)
			throws DAOException {
		try {
			String a_dynamicWhere = "update MatnrList m set dmbtr=" + a_summa + ",awkey=" + a_awkey
					+ " where awkey is null and invoice =" + a_invoice_id + " and matnr=" + a_matnr;
			System.out.println(a_dynamicWhere);
			Query query = this.em.createQuery(a_dynamicWhere);
			int row_updated = query.executeUpdate();
			if (row_updated != a_rows) {
				throw new DAOException("Количество материалов в складе и в накладной разные.");
			}
			return row_updated;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrList> findAllAccountability(String cond) {
		String s = " SELECT SUM(menge), staff_id, matnr FROM MatnrList m WHERE status = '"
				+ MatnrList.STATUS_ACCOUNTABILITY + "' ";
		if (cond.length() > 0) {
			s += " AND " + cond;
		}
		s += " GROUP BY staff_id, matnr ";
		List<MatnrList> out = new ArrayList<MatnrList>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			MatnrList ml = new MatnrList();
			ml.setMenge((Double) result[0]);
			ml.setStaff_id((Long) result[1]);
			ml.setMatnr((Long) result[2]);
			out.add(ml);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrList> findAllSold(String cond, int max) {
		List<MatnrList> l_ml = new ArrayList<MatnrList>();
		String s = "select c FROM MatnrList c where status = '" + MatnrList.STATUS_SOLD + "'";
		if (cond.length() > 0) {
			s += " AND " + cond;
		}
		Query query = this.em.createQuery(s);
		if (max > 0) {
			query.setMaxResults(max);
		}
		l_ml = query.getResultList();
		return l_ml;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrList> findAllInWerks(Long werks, String status) {
		String s = " SELECT ml,m FROM MatnrList ml, Matnr m WHERE ml.matnr=m.matnr AND ml.werks = " + werks
				+ " AND ml.status = '" + status + "' ";
		Query q = em.createQuery(s);
		List<Object[]> l = q.getResultList();
		List<MatnrList> out = new ArrayList<MatnrList>();
		Map<Long, Double> tempMap = new HashMap<Long, Double>();
		for (Object[] o : l) {
			MatnrList ml = (MatnrList) o[0];
			Matnr m = (Matnr) o[1];
			if (m != null) {
				Double menge = 0D;
				if (tempMap.get(m.getMatnr()) != null) {
					menge = tempMap.get(m.getMatnr());
				}
				menge += 1D;
				ml.setMenge(menge);
				ml.setMatnrObject(m);
				ml.setMatnrCode(m.getCode());
				ml.setMatnrName(m.getText45());
				ml.setMatnrNameEn(m.getName_en());
				ml.setMatnrNameTr(m.getName_tr());
				for (Iterator<MatnrList> it = out.iterator(); it.hasNext();) {
					MatnrList ml2 = it.next();
					if (ml2.getMatnr().equals(m.getMatnr())) {
						it.remove();
					}
				}
				out.add(ml);
				tempMap.put(m.getMatnr(), menge);
			}
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MatnrList findByBarcode(String barcode) {
		String s = " SELECT ml FROM MatnrList ml WHERE ml.barcode = '" + barcode.trim() + "' ";
		Query q = em.createQuery(s);
		List<MatnrList> l = q.getResultList();
		if (l.size() > 1) {
			throw new DAOException("Материал с номером найдено больше 1-го");
		}
		return l.size() > 0 ? l.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrList> findStaffAllMatnrList(Long staffId) {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		String s = "select matnr, sum(menge), sum(dmbtr), werks,status,barcode FROM MatnrList b WHERE status != '"
				+ MatnrList.STATUS_SOLD + "' AND staff_id = " + staffId;
		s += " group by matnr, werks,barcode,status ";
		List<MatnrList> out = new ArrayList<MatnrList>();
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			MatnrList ml = new MatnrList();
			// wa_ml.setMatnr_list_id((Long)result[0]);
			ml.setMatnr((long) result[0]);
			ml.setMenge((double) result[1]);
			ml.setDmbtr((double) result[2]);
			ml.setWerks((long) result[3]);
			ml.setStatus(String.valueOf(result[4]));
			ml.setBarcode(String.valueOf(result[5]));
			ml.setMatnrObject(matnrMap.get(ml.getMatnr()));

			out.add(ml);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrList> findAll(String cond) {
		String s = " SELECT ml FROM MatnrList ml ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query query = this.em.createQuery(s);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrList> findStaffMatnrListByMatnrWerksStatus(Long staffId, Long matnr, List<String> werksIds,
			String status) {
		String s = " SELECT ml FROM MatnrList ml WHERE staff_id=%d AND matnr=%d AND werks IN(%s) AND status = '%s' ";
		Query q = em.createQuery(String.format(s, staffId, matnr, String.join(",", werksIds), status));
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MatnrList findOneByBarcodeWerksStatus(String barcode, List<String> werksIds, String status) {
		String s = " SELECT ml FROM MatnrList ml WHERE barcode='%s' AND werks IN(%s) AND status='%s' ";
		Query query = this.em.createQuery(String.format(s, barcode, String.join(",", werksIds), status),MatnrList.class);
		List<MatnrList> result = query.getResultList();
		if(Validation.isEmptyCollection(result)){
			return null;
		}
		
		return result.get(0);
	}

}