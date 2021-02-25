package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.PyramidArchiveDao;
import general.tables.Pyramid;
import general.tables.PyramidArchive;

@Component("pyramidArchiveDao")
public class PyramidArchiveDaoImpl extends GenericDaoImpl<PyramidArchive>implements PyramidArchiveDao {

	public List<PyramidArchive> dynamicFindPyramid(String a_dynamicWhere) {
		String select = "select p " + " FROM  PyramidArchive p where " + a_dynamicWhere;

		Query query = this.em.createQuery(select);
		List<PyramidArchive> pyr = query.getResultList();
		return pyr;
	}

	public PyramidArchive findRoot(String bukrs) {
		Query query = this.em
				.createQuery("select p " + " FROM  PyramidArchive p where p.parent_pyramid_id = 0 AND p.bukrs = :b ");
		query.setParameter("b", bukrs);
		return (PyramidArchive) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PyramidArchive> getRootList(String condition) {

		String q = " select p FROM  PyramidArchive p where parent_pyramid_id = 0 ";
		if (condition.length() > 0) {
			q += " AND " + condition;
		}
		Query query = this.em.createQuery(q);
		return query.getResultList();
	}

	@Override
	public PyramidArchive findOne(String column, Long val) {
		Query query = this.em.createQuery(String.format("select p FROM  PyramidArchive p where p.%s = :c", column));
		query.setParameter("c", val);
		List<PyramidArchive> l = query.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PyramidArchive> findByBukrsBranchPosition(int year, int month, String bukrs, Long branchId,
			Long positionId) {
		String s = String.format(
				" SELECT p FROM PyramidArchive p WHERE year=%d AND month=%d AND bukrs = '%s' AND branch_id = %d AND position_id = %d ",
				year, month, bukrs, branchId, positionId);
		System.out.println("SSS: " + s);
		Query q = em.createQuery(s, PyramidArchive.class);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PyramidArchive findPyramid(int year, int month, String bukrs, Long branchId, Long staffId, Long positionId) {
		String s = String.format(
				" SELECT p FROM PyramidArchive p WHERE year=%d AND month=%d AND bukrs = '%s' AND branch_id = %d AND staff_id = %d AND position_id = %d ",
				year, month, bukrs, branchId, staffId, positionId);
		Query q = em.createQuery(s, PyramidArchive.class);
		List<PyramidArchive> l = q.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PyramidArchive find(Long id, int year, int month) {
		String s = " SELECT p FROM PyramidArchive p WHERE pyramid_id=%d AND year=%d AND month=%d ";
		Query q = em.createQuery(String.format(s, id, year, month));
		List<PyramidArchive> result = q.getResultList();
		if (Validation.isEmptyCollection(result)) {
			return null;
		}

		return result.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PyramidArchive> findAllWithStaff(String cond) {
		String s = " SELECT p FROM PyramidArchive p LEFT OUTER JOIN fetch p.staff s WHERE " + cond;
		Query q = em.createQuery(s);
		return q.getResultList();
	}
}
