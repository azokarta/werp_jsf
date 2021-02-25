package general.dao.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.Query;

import general.GeneralUtil;
import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao.SalaryDao;
import general.tables.MatnrList;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.search.SalarySearch;

import org.springframework.stereotype.Component;

@Component("salaryDao")
public class SalaryDaoImpl extends GenericDaoImpl<Salary>implements SalaryDao {

	public List<Salary> findByStaffId(Long a_staff_id) {
		Query query = this.em.createQuery("select s FROM Salary s where s.staff_id= :staff_id order by salary_id asc");
		query.setParameter("staff_id", a_staff_id);
		List<Salary> sal = query.getResultList();
		return sal;
	}

	public Long countSalaryById(Long a_salary_id) {
		Query query = this.em.createQuery("select COUNT(s.salary_id) FROM Salary s where s.salary_id = :salary_id");
		query.setParameter("salary_id", a_salary_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public List<Salary> findByBukrs(Date a_firstDay, Date a_lastDay, String a_bukrs) {
		Query query = this.em.createQuery("select sal " + " FROM  Salary sal, Staff stf" + " where "
				+ " sal.beg_date <= :a_lastDay and sal.end_date >=:a_firstDay"
//				+ " and sal.waers in ('USD','AZN','TJS') "
				+ " and sal.bukrs = :a_bukrs and sal.staff_id = stf.staff_id order by sal.staff_id ASC");
		query.setParameter("a_firstDay", a_firstDay);
		query.setParameter("a_lastDay", a_lastDay);
		query.setParameter("a_bukrs", a_bukrs);
		List<Salary> sal = query.getResultList();
		return sal;
	}

	public List<Salary> findByBukrs2(Date a_firstDay, Date a_lastDay, String a_bukrs) {
		Query query = this.em.createQuery("select sal " + " FROM  Salary sal, Staff stf" + " where "
				+ "sal.salary_id = some( select max(sal2.salary_id) " + " from Salary sal2 where"
				+ " sal.position_id <> 1  and sal.beg_date <= :a_lastDay and sal.end_date >=:a_firstDay"
				+ " and sal.bukrs = :a_bukrs" + " group by sal2.staff_id)"
				+ "  and sal.staff_id = stf.staff_id order by sal.staff_id ASC");
		query.setParameter("a_firstDay", a_firstDay);
		query.setParameter("a_lastDay", a_lastDay);
		query.setParameter("a_bukrs", a_bukrs);
		List<Salary> sal = query.getResultList();
		return sal;
	}

	public List<Salary> findDynamic(String a_dynamic) {
		Calendar curDate = Calendar.getInstance();
		a_dynamic = "select sal " + " FROM  Salary sal" + " where "
				+ "sal.salary_id = some( select max(sal2.salary_id) " + "from Salary sal2  where sal2.end_date >='"
				+ GeneralUtil.getSQLDate(curDate) + "' group by sal2.staff_id)" + a_dynamic
				+ " order by sal.staff_id ASC";
		// System.out.println(a_dynamic);
		Query query = this.em.createQuery(a_dynamic);
		List<Salary> sal = query.getResultList();
		return sal;
	}

	public List<Salary> findDynamic2(String a_dynamic) {
		Calendar curDate = Calendar.getInstance();
		a_dynamic = "select sal " + " FROM  Salary sal where " + "sal.end_date >='" + GeneralUtil.getSQLDate(curDate)
				+ "' " + " and sal.beg_date <='" + GeneralUtil.getSQLDate(curDate) + "' " + a_dynamic
				+ " order by sal.staff_id ASC";
		System.out.println(a_dynamic);
		Query query = this.em.createQuery(a_dynamic);
		List<Salary> sal = query.getResultList();
		return sal;
	}

	public List<Object[]> findDynamic3(String a_dynamic) {
		Calendar curDate = Calendar.getInstance();
		a_dynamic = "select st.staff_id," + " LISTAGG(p.text, ', ') WITHIN GROUP (ORDER BY p.text) position_name,"
				+ " st.customer_id," + " st.iin_bin," + " st.lastname," + " st.firstname," + " st.middlename"
				+ " from staff st, salary sal,position p "
				+ " where st.staff_id=sal.staff_id and sal.position_id=p.position_id" + " and sal.end_date >='"
				+ GeneralUtil.getSQLDate(curDate) + "' " + " and sal.beg_date <='" + GeneralUtil.getSQLDate(curDate)
				+ "' " + a_dynamic
				+ " group by st.staff_id, st.customer_id, st.iin_bin, st.lastname, st.firstname, st.middlename order by st.staff_id ASC";
		Query query = this.em.createNativeQuery(a_dynamic);
		List<Object[]> results = query.getResultList();
		return results;
	}

	public List<Object[]> findDynamicFired(String a_dynamic) {
		Calendar curDate = Calendar.getInstance();
		a_dynamic = "select st.staff_id," + " LISTAGG(p.text, ', ') WITHIN GROUP (ORDER BY p.text) position_name,"
				+ " st.customer_id," + " st.iin_bin," + " st.lastname," + " st.firstname," + " st.middlename"
				+ " from staff st, salary sal,position p "
				+ " where st.staff_id=sal.staff_id and sal.position_id=p.position_id" + a_dynamic
				+ " group by st.staff_id, st.customer_id, st.iin_bin, st.lastname, st.firstname, st.middlename order by st.staff_id ASC";
		Query query = this.em.createNativeQuery(a_dynamic);
		List<Object[]> results = query.getResultList();
		return results;
	}

	public Salary findOne(String column, Long value) {
		Query query = this.em.createQuery(String.format("SELECT sal FROM Salary sal WHERE %s = :value", column));
		query.setParameter("value", value);
		return (Salary) query.getSingleResult();
	}

	public Salary findCurrentOne(Staff s) {
		try {
			Query query = this.em
					.createQuery("SELECT sal FROM Salary sal WHERE sal.staff_id = :value ORDER BY beg_date DESC ")
					.setMaxResults(1);
			query.setParameter("value", s.getStaff_id());
			return (Salary) query.getSingleResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	public List<Salary> findTest() {
		List<Salary> l_sal = new ArrayList<Salary>();
		Query query = this.em.createQuery(
				"select sal.staff_id, sal.salary_id " + " FROM  Salary sal" + " where " + "sal.staff_id = 3");

		List<Object[]> results = query.getResultList();

		for (Object[] result : results) {
			Salary wa_salary = new Salary();
			wa_salary.setStaff_id((long) result[0]);
			wa_salary.setSalary_id((long) result[1]);
			l_sal.add(wa_salary);
		}

		return l_sal;
	}

	@Override
	public List<Salary> findAll(String condition) {
		String s = " SELECT s FROM Salary s";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}

		Query q = this.em.createQuery(s);
		List<Salary> l = q.getResultList();
		return l;
	}

	@Override
	public void updateCurrentSalaryEndDate(Long staffId, java.util.Date endDate) {
		Query q = this.em.createQuery(
				"UPDATE Salary s SET s.end_date = :ed WHERE staff_id = :stf_id AND end_date = '2099-01-01'");
		q.setParameter("ed", endDate);
		q.setParameter("stf_id", staffId);
		q.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllCurrentWithStaff(String cond) {
		String s = " SELECT s1,s2 FROM Salary s1 INNER JOIN s1.p_staff s2 WHERE s1.beg_date <= '"
				+ Helper.getCurrentDateForDb() + "' AND s1.end_date >= '" + Helper.getCurrentDateForDb() + "' ";
		if (cond.length() > 0) {
			s += " AND " + cond;
		}

		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<Salary> out = new ArrayList<Salary>();
		for (Object[] o : result) {
			Salary s1 = (Salary) o[0];
			Staff s2 = (Staff) o[1];
			if (s2 == null) {
				continue;
				// s1.setP_staff(new Staff());
			} else {
				s1.setP_staff(s2);
				s1.setStaffName(s2.getLF());
			}

			out.add(s1);
		}
		return out;
	}

	@Override
	public List<Salary> findAllWithStaff(String cond, int first, int pageSize) {
		String s = " SELECT s1,s2 FROM Salary s1 INNER JOIN s1.p_staff s2 ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		// System.out.println("SAL COND: " + cond);
		Query query = this.em.createQuery(s);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);

		List<Object[]> result = query.getResultList();
		List<Salary> out = new ArrayList<Salary>();
		for (Object[] o : result) {
			Salary s1 = (Salary) o[0];
			Staff s2 = (Staff) o[1];
			if (s2 == null) {
				continue;
				// s1.setP_staff(new Staff());
			} else {
				s1.setP_staff(s2);
			}

			out.add(s1);
		}
		return out;
	}

	@Override
	public List<Salary> findAllWithStaff(String cond) {
		String s = " SELECT s1,s2 FROM Salary s1 INNER JOIN s1.p_staff s2 ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		// System.out.println("SAL COND: " + cond);
		Query query = this.em.createQuery(s);

		List<Object[]> result = query.getResultList();
		List<Salary> out = new ArrayList<Salary>();
		for (Object[] o : result) {
			Salary s1 = (Salary) o[0];
			Staff s2 = (Staff) o[1];
			if (s2 == null) {
				continue;
				// s1.setP_staff(new Staff());
			} else {
				s1.setP_staff(s2);
			}

			out.add(s1);
		}
		return out;
	}

	@Override
	public int getRowCountWithStaff(String condition) {
		String s = "select COUNT(*) FROM Salary s1 LEFT JOIN s1.p_staff s2 ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllByPositionId(Long positionId) {
		String s = " SELECT s1,s2 FROM Salary s1 INNER JOIN s1.p_staff s2 WHERE s1.position_id = " + positionId;
		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<Salary> out = new ArrayList<Salary>();
		for (Object[] o : result) {
			Salary s1 = (Salary) o[0];
			Staff s2 = (Staff) o[1];
			if (s2 == null) {
				continue;
				// s1.setP_staff(new Staff());
			} else {
				s1.setP_staff(s2);
			}

			out.add(s1);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllCurrent(String cond) {
		String s = " SELECT s FROM Salary s WHERE beg_date <= '" + Helper.getCurrentDateForDb() + "' AND end_date >= '"
				+ Helper.getCurrentDateForDb() + "' ";
		if (cond.length() > 0) {
			s += " AND " + cond;
		}

		Query query = this.em.createQuery(s);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAllSalaryWithStaffForReport(String bukrs, Long branchId, Long positionId,
			Long departmentId, Long staffId, String currency) {
		String s = " SELECT s1.salary_id, s1.bukrs, s1.branch_id, s1.staff_id, s1.amount,s2.firstname,s2.lastname,s2.middlename, s1.waers,s1.position_id,s1.beg_date FROM salary s1, staff s2 WHERE s1.staff_id=s2.staff_id ";
		s += " AND s1.amount IS NOT NULL AND s1.amount > 0 ";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		s += " AND s1.beg_date <= '" + sdf.format(cal.getTime()).toString() + "' ";
		s += " AND s1.end_date >  '" + sdf.format(cal.getTime()).toString() + "' ";
		if (!Validation.isEmptyString(bukrs)) {
			s += " AND s1.bukrs = '" + bukrs + "' ";
		}

		if (!Validation.isEmptyLong(branchId)) {
			s += " AND s1.branch_id = " + branchId;
		}

		if (!Validation.isEmptyLong(positionId)) {
			s += " AND s1.position_id = " + positionId;
		}

		if (!Validation.isEmptyLong(staffId)) {
			s += " AND s1.staff_id = " + staffId;
		}

		if (!Validation.isEmptyLong(departmentId)) {
			s += " AND s1.department_id = " + departmentId;
		}

		if (!Validation.isEmptyString(currency)) {
			s += " AND s1.waers = '" + currency + "' ";
		}

		Query q = em.createNativeQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllOnDismiss(SalarySearch searchModel) {
		String s = " SELECT s1,s2 FROM Salary s1, Staff s2 WHERE s1.staff_id=s2.staff_id AND s2.onDismiss = 1 AND "
				+ "s1.end_date=(SELECT MAX(end_date) FROM Salary t WHERE t.staff_id=s2.staff_id) ";
		if (!Validation.isEmptyString(searchModel.getBukrs())) {
			s += " AND s1.bukrs = " + searchModel.getBukrs();
		}

		if (!Validation.isEmptyLong(searchModel.getBranch_id())) {
			s += " AND s1.branch_id = " + searchModel.getBranch_id();
		}

		if (!Validation.isEmptyCollection(searchModel.getBranchIds())) {
			s += " AND s1.branch_id IN(" + String.join(",", searchModel.getBranchIds()) + ") ";
		}

		if (!Validation.isEmptyString(searchModel.getFirstname())) {
			s += " AND s2.firstname LIKE '%" + searchModel.getFirstname() + "%' ";
		}

		if (!Validation.isEmptyString(searchModel.getLastname())) {
			s += " AND s2.lastname LIKE '%" + searchModel.getLastname() + "%' ";
		}

		Query q = em.createQuery(s);
		List<Object[]> result = q.getResultList();
		List<Salary> out = new ArrayList<>();

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
	public Salary findLastSalary(Long staffId) {
		String s = String.format("SELECT s FROM Salary s WHERE staff_id = %d ORDER BY end_date DESC ", staffId);
		Query q = em.createQuery(s);
		q.setMaxResults(1);
		List<Salary> result = q.getResultList();
		if (Validation.isEmptyCollection(result)) {
			return null;
		}

		return result.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllForHrRep5(String bukrs, List<Long> branchIds, Long positionId)
			throws DAOException, ParseException {
		String s = "SELECT salary_id,staff_id,position_id,department_id,beg_date,end_date FROM salary "
				+ " WHERE staff_id IN(SELECT staff_id FROM salary WHERE end_date >= '%s' ) AND beg_date <= '%s' ";
		if (!Validation.isEmptyString(bukrs)) {
			s += " AND bukrs = " + bukrs;
		}

		if (!Validation.isEmptyCollection(branchIds)) {
			List<String> strBrIds = new ArrayList<>();
			for (Long brId : branchIds) {
				strBrIds.add(brId.toString());
			}
			s += " AND branch_id IN(" + String.join(",", strBrIds) + ") ";
		}

		// if (!Validation.isEmptyLong(positionId)) {
		// s += " AND position_id = " + positionId;
		// }
		s += String.format(" AND position_id IN(%d,%d,%d,%d) ", Position.DEALER_POSITION_ID,
				Position.MANAGER_POSITION_ID, Position.STAZHER_DEALER_POSITION_ID, Position.DIRECTOR_POSITION_ID);
		// s += " AND staff_id IN(13249) ";

		Query q = em.createNativeQuery(String.format(s, Helper.getCurrentDateForDb(), Helper.getCurrentDateForDb()));
		List<Object[]> result = q.getResultList();
		List<Salary> out = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Object[] o : result) {
			Salary sal = new Salary();
			sal.setSalary_id(Long.parseLong(String.valueOf(o[0])));
			sal.setStaff_id(Long.parseLong(String.valueOf(o[1])));
			sal.setPosition_id(Long.parseLong(String.valueOf(o[2])));
			try {
				sal.setDepartment_id(Long.parseLong(String.valueOf(o[3])));
			} catch (NumberFormatException e) {
				continue;
			}
			sal.setBeg_date(sdf.parse(String.valueOf(o[4])));
			sal.setEnd_date(sdf.parse(String.valueOf(o[5])));
			out.add(sal);
			// System.out.println(o[0] + " " + o[1]);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Staff> findAllForHrRep6(String bukrs, List<Long> branchIds, Long positionId, Long departmentId)
			throws DAOException, ParseException {
		String s1 = "SELECT sal.department_id,sal.staff_id AS stf_id FROM salary sal WHERE "
				+ " department_id > 0 AND department_id IS NOT NULL AND sal.beg_date <= '%s' AND sal.end_date >= '%s' ";
		s1 = String.format(s1, Helper.getCurrentDateForDb(), Helper.getCurrentDateForDb());

		if (!Validation.isEmptyString(bukrs)) {
			s1 += " AND bukrs = " + bukrs;
		}

		if (!Validation.isEmptyCollection(branchIds)) {
			List<String> strBrIds = new ArrayList<>();
			for (Long l : branchIds) {
				strBrIds.add(l.toString());
			}

			s1 += " AND branch_id IN(" + String.join(",", strBrIds) + ") ";
		}

		// if (!Validation.isEmptyLong(branchId)) {
		// s1 += " AND branch_id = " + branchId;
		// }

		if (!Validation.isEmptyLong(positionId)) {
			s1 += " AND position_id = " + positionId;
		}

		if (!Validation.isEmptyLong(departmentId)) {
			s1 += " AND department_id = " + departmentId;
		}

		String s = "SELECT t1.staff_id,t1.firstname,t1.lastname,t2.salary_id,t2.bukrs,t2.branch_id,t2.department_id,t2.position_id,t2.beg_date,t2.end_date"
				+ " FROM staff t1, salary t2, (%s) t3 "
				+ " WHERE t1.staff_id=t2.staff_id AND t2.department_id=t3.department_id AND t2.staff_id = t3.stf_id "
				+ " AND t2.beg_date <= '%s' ";
		// if (!Validation.isEmptyLong(positionId)) {
		// s += " AND t2.position_id=" + positionId;
		// }

		Query q = em.createNativeQuery(String.format(s, s1, Helper.getCurrentDateForDb()));
		List<Object[]> result = q.getResultList();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Long, Staff> stfMap = new HashMap<>();
		List<Staff> out = new ArrayList<>();
		for (Object[] o : result) {
			Salary sal = new Salary();
			sal.setStaff_id(Long.parseLong(String.valueOf(o[0])));
			sal.setSalary_id(Long.parseLong(String.valueOf(o[3])));
			sal.setBukrs(String.valueOf(o[4]));
			sal.setBranch_id(Long.parseLong(String.valueOf(o[5])));
			sal.setDepartment_id(Long.parseLong(String.valueOf(o[6])));
			sal.setPosition_id(Long.parseLong(String.valueOf(o[7])));
			sal.setBeg_date(sdf.parse(String.valueOf(o[8])));
			sal.setEnd_date(sdf.parse(String.valueOf(o[9])));

			Staff stf = new Staff();
			if (stfMap.containsKey(sal.getStaff_id())) {
				stf = stfMap.get(sal.getStaff_id());
			} else {
				stf.setStaff_id(sal.getStaff_id());
				stf.setFirstname(String.valueOf(o[1]));
				stf.setLastname(String.valueOf(o[2]));
				stf.setSalaries(new ArrayList<>());
			}

			boolean exists = false;
			for (Salary ss : stf.getSalaries()) {
				if (ss.getSalary_id().equals(sal.getSalary_id())) {
					exists = true;
				}
			}
			if (!exists) {
				stf.getSalaries().add(sal);
			}
			stfMap.put(stf.getStaff_id(), stf);
		}

		for (Entry<Long, Staff> e : stfMap.entrySet()) {
			out.add(e.getValue());
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Salary> findAllByBranchAndPositionId(Long branchId, Long positionId) {
		String s = " SELECT s1,s2 FROM Salary s1 INNER JOIN s1.p_staff s2 WHERE s1.position_id = " + positionId
				+ " AND s1.branch_id = " + branchId;
		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<Salary> out = new ArrayList<Salary>();
		for (Object[] o : result) {
			Salary s1 = (Salary) o[0];
			Staff s2 = (Staff) o[1];
			if (s2 == null) {
				continue;
				// s1.setP_staff(new Staff());
			} else {
				s1.setP_staff(s2);
			}

			out.add(s1);
		}
		return out;
	}

	/*
	 * public List<Salary> findDynamic_oldWages(String a_dynamic) { Calendar
	 * curDate = Calendar.getInstance(); a_dynamic = "select sal " +
	 * " FROM  Salary sal" + " where " +
	 * "sal.salary_id = some( select max(sal2.salary_id) " +
	 * "from Salary sal2  where sal2.beg_date<='" +
	 * GeneralUtil.getSQLDate(curDate) + "'  group by sal2.staff_id)" +
	 * a_dynamic + " order by sal.beg_date DESC"; //
	 * System.out.println(a_dynamic); Query query =
	 * this.em.createQuery(a_dynamic); List<Salary> sal = query.getResultList();
	 * return sal; }
	 */
}