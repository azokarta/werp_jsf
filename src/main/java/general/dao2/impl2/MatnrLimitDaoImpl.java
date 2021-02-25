package general.dao2.impl2;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.impl.GenericDaoImpl;
import general.dao2.MatnrLimitDao;
import general.tables2.MatnrLimit;
import general.tables2.MatnrLimitItem;

@Component("matnrLimitDao")
public class MatnrLimitDaoImpl extends GenericDaoImpl<MatnrLimit>implements MatnrLimitDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrLimit> findAll(String bukrs, Long branchId, Long positionId) {
		String s = "SELECT ml FROM MatnrLimit ml LEFT JOIN fetch ml.creator";
		String cond = "";
		if (!Validation.isEmptyString(bukrs)) {
			cond = " ml.bukrs = " + bukrs;
		}

		if (!Validation.isEmptyLong(branchId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " ml.branchId = " + branchId;
		}

		if (!Validation.isEmptyLong(positionId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " ml.positionId = " + positionId;
		}

		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrLimit> findAll(String bukrs, List<String> branchIds, Long positionId) {
		String s = "SELECT ml FROM MatnrLimit ml LEFT JOIN fetch ml.creator";
		String cond = "";
		if (!Validation.isEmptyString(bukrs)) {
			cond = " ml.bukrs = " + bukrs;
		}

		if (!Validation.isEmptyCollection(branchIds)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " ml.branchId IN(" + String.join(",", branchIds) + ") ";
		}

		if (!Validation.isEmptyLong(positionId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " ml.positionId = " + positionId;
		}

		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MatnrLimit findWithDetail(Long id) {
		MatnrLimit ml = super.find(id);
		if (ml != null) {
			String s = "SELECT i FROM MatnrLimitItem i WHERE ml_id = " + id;
			Query q = em.createQuery(s, MatnrLimitItem.class);
			ml.setMatnrLimitItems(q.getResultList());
		}
		return ml;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrLimit> findAllWithItems(String bukrs, Long branchId, Long positionId) {
		String s = "SELECT ml FROM MatnrLimit ml LEFT JOIN fetch ml.matnrLimitItems";
		String cond = "";
		if (!Validation.isEmptyString(bukrs)) {
			cond = " ml.bukrs = " + bukrs;
		}

		if (!Validation.isEmptyLong(branchId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " ml.branchId = " + branchId;
		}

		if (!Validation.isEmptyLong(positionId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " ml.positionId = " + positionId;
		}

		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

}
