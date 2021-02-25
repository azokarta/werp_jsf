package general.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import general.Validation;
import general.dao.MatnrDao;
import general.tables.Matnr;
import general.tables.MatnrBukrs;
import general.tables.Salary;
import general.tables.Staff;

import org.springframework.stereotype.Component;

@Component("matnrDao")
public class MatnrDaoImpl extends GenericDaoImpl<Matnr> implements MatnrDao {

	@Override
	public List<Matnr> findAll() {
		Query query = this.em.createQuery("select m FROM Matnr m");
		List<Matnr> l_matnr = query.getResultList();
		return l_matnr;
	}

	@Override
	public List<Matnr> findAll(String c) {
		String s = "select m FROM Matnr m ";
		if (c.length() > 0) {
			s += " WHERE " + c;
		}
		Query query = this.em.createQuery(s);
		List<Matnr> l_matnr = query.getResultList();
		return l_matnr;
	}

	@Override
	public List<Matnr> findAllByBukrs(String a_bukrs) {
		String qq = "Select m from Matnr m, MatnrBukrs mb" + " Where mb.bukrs = '" + a_bukrs + "'"
				+ " and mb.matnr = m.matnr";
		Query q = this.em.createQuery(qq);
		List<Matnr> ml = q.getResultList();
		return ml;
	}

	@Override
	public Map<Long, Matnr> getMappedList(String cond) {
		Map<Long, Matnr> out = new HashMap<Long, Matnr>();
		List<Matnr> l = findAll(cond);
		for (int i = 0; i < l.size(); i++) {
			out.put(l.get(i).getMatnr(), l.get(i));
		}
		return out;
	}

	@Override
	public int getRowCount(String condition) {
		String s = "select COUNT(*) FROM Matnr m ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Matnr> findAllLazy(String condition, int first, int pageSize) {
		String s = " SELECT m FROM Matnr m ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<Matnr> l = q.getResultList();

		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Matnr> findMatnrParts(Long matnr) {
		String s = "SELECT m FROM Matnr m WHERE m.matnr IN(SELECT sp FROM MatnrSparePart sp WHERE sp.tovar_id = :tovar_id)";
		Query query = em.createQuery(s);
		query.setParameter("tovar_id", matnr);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Matnr findByCode(String code) {
		String s = " SELECT m FROM Matnr m WHERE code = '" + code + "' ";
		Query q = em.createQuery(s, Matnr.class);
		List<Matnr> l = q.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}

		return null;
	}
}
