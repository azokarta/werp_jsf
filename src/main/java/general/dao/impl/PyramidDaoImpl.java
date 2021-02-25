package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.Helper;
import general.dao.PyramidDao;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;

@Component("pyramidDao")
public class PyramidDaoImpl extends GenericDaoImpl<Pyramid>implements PyramidDao {
	public List<Pyramid> dynamicFindPyramid(String a_dynamicWhere) {
		Query query = this.em.createQuery("select p " + " FROM  Pyramid p where " + a_dynamicWhere);

		List<Pyramid> pyr = query.getResultList();
		return pyr;
	}

	public Pyramid findRoot(String bukrs) {
		Query query = this.em
				.createQuery("select p " + " FROM  Pyramid p where p.parent_pyramid_id = 0 AND p.bukrs = :b");
		query.setParameter("b", bukrs);
		return (Pyramid) query.getSingleResult();
	}

	public List<Pyramid> getRootList(String bukrs, Long branchId) {
		String q = "select p FROM  Pyramid p where p.bukrs = :b AND p.parent_pyramid_id = 0 ";
		if (branchId > 0) {
			q += " AND p.branch_id = " + branchId;
		}
		Query query = this.em.createQuery(q);
		query.setParameter("b", bukrs);
		List<Pyramid> pyr = query.getResultList();
		return pyr;
	}

	@Override
	public Pyramid findOne(String column, Long val) {
		Query query = this.em.createQuery(String.format("select p FROM  Pyramid p where p.%s = :c", column));
		query.setParameter("c", val);
		List<Pyramid> l = query.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pyramid> findAllWithDetailsByBukrs(String bukrs) {
		String s = " SELECT p FROM Pyramid p LEFT OUTER JOIN fetch p.staff s WHERE p.bukrs = :b";
		Query q = em.createQuery(s);
		q.setParameter("b", bukrs);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pyramid findPyramid(String bukrs, Long branchId, Long staffId, Long positionId) {
		String s = String.format(
				" SELECT p FROM Pyramid p WHERE bukrs = '%s' AND branch_id = %d AND staff_id = %d AND position_id = %d ",
				bukrs, branchId, staffId, positionId);
		Query q = em.createQuery(s);
		List<Pyramid> l = q.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pyramid> findByBukrsBranchPosition(String bukrs, Long branchId, Long positionId) {
		String s = String.format(" SELECT p FROM Pyramid p WHERE bukrs = '%s' AND branch_id = %d AND position_id = %d ",
				bukrs, branchId, positionId);
		// System.out.println("SSS: " + s);
		Query q = em.createQuery(s, Pyramid.class);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pyramid> findAllWithStaff(String cond) {
		String s = " SELECT p FROM Pyramid p LEFT OUTER JOIN fetch p.staff s WHERE " + cond;
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllDealersByBranchManagerId(String bukrs, Long branchId, Long managerId) {
		List<Salary> out = new ArrayList<>();
		Pyramid manager = findPyramid(bukrs, branchId, managerId, Position.MANAGER_POSITION_ID);
		if (manager != null) {
			String s = " SELECT s1, s2 FROM Pyramid p, Salary s1, Staff s2 "
					+ " WHERE p.staff_id=s1.staff_id AND p.staff_id=s2.staff_id AND s1.staff_id=s2.staff_id AND p.branch_id = "
					+ branchId + " AND p.parent_pyramid_id=" + manager.getPyramid_id() + " AND p.position_id="
					+ Position.DEALER_POSITION_ID + " AND s1.beg_date <= '" + Helper.getCurrentDateForDb()
					+ "' AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "' ";
			Query q = em.createQuery(s);
			List<Object[]> result = q.getResultList();
			for (Object[] o : result) {
				Salary s1 = (Salary) o[0];
				Staff s2 = (Staff) o[1];
				s1.setP_staff(s2);
				out.add(s1);
			}
		}

		return out;
	}

	@Override
	public Pyramid findParentPyramid(String bukrs, Long branchId, Long childStaffId, Long childStaffPositionId) {
		Pyramid p = findPyramid(bukrs, branchId, childStaffId, childStaffPositionId);
		if (p != null) {
			return find(p.getParent_pyramid_id());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllDemosecsByBranchManagerId(String bukrs, Long branchId, Long managerId) {
		List<Salary> out = new ArrayList<>();
		Pyramid manager = findPyramid(bukrs, branchId, managerId, Position.MANAGER_POSITION_ID);
		if (manager != null) {
			String s = " SELECT s1, s2 FROM Pyramid p, Salary s1, Staff s2 "
					+ " WHERE p.staff_id=s1.staff_id AND p.staff_id=s2.staff_id AND s1.staff_id=s2.staff_id AND p.branch_id = "
					+ branchId + " AND p.parent_pyramid_id=" + manager.getPyramid_id() + " AND p.position_id="
					+ Position.DEMOSEC_POSITION_ID + " AND s1.beg_date <= '" + Helper.getCurrentDateForDb()
					+ "' AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "' ";
			Query q = em.createQuery(s);
			List<Object[]> result = q.getResultList();
			for (Object[] o : result) {
				Salary s1 = (Salary) o[0];
				Staff s2 = (Staff) o[1];
				s1.setP_staff(s2);
				out.add(s1);
			}
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllManagersByBranchId(String bukrs, Long branchId) {
		List<Salary> out = new ArrayList<>();
		String s = " SELECT s1, s2 FROM Pyramid p, Salary s1, Staff s2 "
				+ " WHERE p.staff_id=s1.staff_id AND p.staff_id=s2.staff_id AND s1.staff_id=s2.staff_id AND p.branch_id = "
				+ branchId + " AND p.position_id="
				+ Position.MANAGER_POSITION_ID + " AND s1.beg_date <= '" + Helper.getCurrentDateForDb()
				+ "' AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "' ";
		Query q = em.createQuery(s);
		List<Object[]> result = q.getResultList();
		for (Object[] o : result) {
			Salary s1 = (Salary) o[0];
			Staff s2 = (Staff) o[1];
			s1.setP_staff(s2);
			out.add(s1);
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pyramid> findAll() {
		Query q = em.createQuery("SELECT p FROM Pyramid p ");
		return q.getResultList();
	}
	
	
}
