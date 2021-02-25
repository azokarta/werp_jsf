package general.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import general.dao.WerksBranchDao;
import general.tables.Branch;
import general.tables.Werks;
import general.tables.WerksBranch;

import org.springframework.stereotype.Component;

@Component("werksBranchDao")
public class WerksBranchDaoImpl extends GenericDaoImpl<WerksBranch> implements
		WerksBranchDao {

	@SuppressWarnings("unchecked")
	public List<WerksBranch> findAll(String condition) {
		String s = " SELECT wb FROM WerksBranch wb ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		// System.out.println("COND: " + s);
		Query query = this.em.createQuery(s);
		List<WerksBranch> l = query.getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	public List<Werks> findAllWerksByBranch(Long branchId) {
		String s = " SELECT w FROM Werks w, WerksBranch wb where wb.werks = w.werks and wb.branch_id = "
				+ branchId;
		// System.out.println("COND: " + s);
		Query query = this.em.createQuery(s);
		List<Werks> l = query.getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findAllBranchesByWerks(Long werks) {
		String s = " SELECT b FROM Branch b, WerksBranch wb where wb.branch_id = b.branch_id and wb.werks = "
				+ werks;
		Query query = this.em.createQuery(s);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Werks> findAllWerksByBranch2(Long branchId) {
		String subQueryString = " SELECT branch_id FROM branch START WITH branch_id = " + branchId + " CONNECT BY PRIOR branch_id=parent_branch_id ";
		String queryString = " SELECT DISTINCT w.werks, w.bukrs AS bukrs,w.text45 AS text45 FROM werks_type w, werks_branch wb where wb.werks = w.werks and wb.branch_id IN( " + subQueryString + ") ";
		Query q = em.createNativeQuery(queryString);
		List<Object[]> r = q.getResultList();
		List<Werks> out = new ArrayList<Werks>();
		for(Object[] o:r){
			BigDecimal bi = (BigDecimal)o[0];
			Werks w = new Werks();
			w.setWerks(bi.longValue());
			w.setBukrs((String)o[1]);
			w.setText45((String)o[2]);
			out.add(w);
		}
		return out;
	}
}
