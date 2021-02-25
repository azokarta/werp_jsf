package general.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import general.Helper;
import general.Validation;
import general.tables.Salary;
import general.tables.Staff;
import general.dao.DAOException;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UpdFileDao;

@Component("staffDao")
public class StaffDaoImpl extends GenericDaoImpl<Staff>implements StaffDao {
	@Autowired
	SalaryDao salaryDao;

	@Autowired
	UpdFileDao fileDao;

	public Long countStaffbyIinBin(String a_iin_bin) {
		Query query = this.em.createQuery("select COUNT(s.staff_id) FROM Staff s where s.iin_bin= :iin_bin");
		query.setParameter("iin_bin", a_iin_bin);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public Staff findByIinBin(String a_iin_bin) {
		Query query = this.em.createQuery("select s FROM Staff s where s.iin_bin= :iin_bin");
		query.setParameter("iin_bin", a_iin_bin);
		Staff stf = (Staff) query.getSingleResult();
		return stf;
	}

	public Staff findByCustomerId(Long a_customer_id) {
		try {
			Query query = this.em.createQuery("select s FROM Staff s where s.customer_id= :customer_id");
			query.setParameter("customer_id", a_customer_id);
			Staff stf = (Staff) query.getSingleResult();
			return stf;
		} catch (NoResultException nre) {
			// Ignore this because as per your logic this is ok!
			return null;
		}

	}

	public Long countStaffByIinBinNotId(Long a_staff_id, String a_iin_bin) {
		Query query = this.em.createQuery(
				"select COUNT(c.staff_id) FROM Staff c where c.iin_bin= :iin_bin and c.staff_id <> :staff_id");
		query.setParameter("iin_bin", a_iin_bin);
		query.setParameter("staff_id", a_staff_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public Long countStaffbyId(Long a_staff_id) {
		Query query = this.em.createQuery("select COUNT(c.staff_id) FROM Staff c where c.staff_id = :staff_id");
		query.setParameter("staff_id", a_staff_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public List<Staff> dynamicFindStaffSalary(String a_dynamicWhere) {
		/*
		 * Single position Query query = this.em .createQuery("select stf "+
		 * " FROM  Salary sal, Staff stf"+ " where sal.salary_id = some ("+
		 * " SELECT max(sal2.salary_id)"+ " FROM Salary sal2"+
		 * " where sal2.staff_id = sal.staff_id"+ " group by sal2.staff_id"+
		 * " )"+ " and sal.staff_id = stf.staff_id "+ a_dynamicWhere);
		 */

		// For multiple positions
		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		Calendar today = Calendar.getInstance();
		Query query = this.em.createQuery("select stf " + " FROM  Salary sal, Staff stf" + " where sal.end_date > '"
				+ format1.format(today.getTime()) + "' and sal.staff_id = stf.staff_id " + a_dynamicWhere);

		// query.setMaxResults(50);
		List<Staff> stf = query.getResultList();
		return stf;
	}

	public List<Staff> dynamicFindStaffSalary2(String a_dynamicWhere) {
		String s = "select stf2 from Staff stf2 where stf2.staff_id in (select stf.staff_id "
				+ " FROM  Staff stf, Salary sal" + " where stf.staff_id=sal.staff_id " + a_dynamicWhere;
		s = s + " group by stf.staff_id  ) order by stf2.lastname, stf2.firstname, stf2.middlename";
		//
		System.out.println(s);
		Query query = this.em.createQuery(s);

		List<Staff> l_stf = query.getResultList();// new ArrayList<Staff>();

		/*
		 * List<Object[]> results = query.getResultList(); for (Object[] result
		 * : results) { Staff wa_staff = new Staff();
		 * wa_staff.setStaff_id((long) result[0]);
		 * wa_staff.setFirstname(String.valueOf(result[1]));
		 * l_stf.add(wa_staff); }
		 */

		return l_stf;
	}

	public List<Staff> findAll() {
		Query query = this.em.createQuery("select s FROM Staff s");

		List<Staff> stf = query.getResultList();
		return stf;
	}

	public List<Staff> findByFIO(String f, String m, String l) {
		if (f.length() == 0 && l.length() == 0 && m.length() == 0) {
			throw new DAOException("FIO Error");
		}
		String q = "";
		if (f.length() > 0) {
			q += " firstname LIKE :f ";
		}

		if (m.length() > 0) {
			q += (q.length() > 0) ? " AND " : " ";
			q += " middlename LIKE :m ";
		}

		if (l.length() > 0) {
			q += (q.length() > 0) ? " AND " : " ";
			q += " middlename LIKE :l ";
		}

		Query query = this.em.createQuery("select s FROM Staff s where " + q);
		if (f.length() > 0) {
			query.setParameter("f", "%" + f + "%");
		}
		if (m.length() > 0) {
			query.setParameter("m", "%" + m + "%");
		}
		if (l.length() > 0) {
			query.setParameter("l", "%" + l + "%");
		}

		query.setMaxResults(50);
		List<Staff> stf = query.getResultList();
		return stf;
	}

	@Override
	public List<Staff> findAll(String condition) {
		String s = " SELECT s FROM Staff s";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}

		Query q = this.em.createQuery(s);
		List<Staff> l = q.getResultList();
		return l;
	}

	@Override
	public int getRowCount(String condition) {
		String s = "select COUNT(s.staff_id) FROM Staff s ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@Override
	public List<Staff> findAllLazy(String condition, int first, int pageSize) {
		String s = " SELECT s FROM Staff s";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<Staff> l = q.getResultList();
		String[] staffIds = new String[l.size()];
		for (int i = 0; i < l.size(); i++) {
			staffIds[i] = l.get(i).getStaff_id().toString();
		}
		if (staffIds.length > 0) {
			String cond = String.format(" staff_id IN(%s) ", "'" + String.join("','", staffIds) + "'");
			List<Salary> temp = salaryDao.findAll(cond);
			Map<Long, List<Salary>> salaryMap = new HashMap<Long, List<Salary>>();
			for (Salary sal : temp) {
				if (!salaryMap.containsKey(sal.getStaff_id())) {
					salaryMap.put(sal.getStaff_id(), new ArrayList<Salary>());
				}

				salaryMap.get(sal.getStaff_id()).add(sal);
			}

			for (Staff stf : l) {
				if (salaryMap.containsKey(stf.getStaff_id())) {
					stf.setSalaries(salaryMap.get(stf.getStaff_id()));
				}
			}
		}
		return l;
	}

	@Override
	public Map<Long, Staff> getMappedList(String cond) {
		Map<Long, Staff> map = new HashMap<Long, Staff>();
		for (Staff stf : findAll(cond)) {
			map.put(stf.getStaff_id(), stf);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Staff findWithDetail(Long id) {
		String s = " SELECT s FROM Staff s LEFT JOIN fetch s.expences e LEFT JOIN fetch s.educations ed WHERE s.id = "
				+ id;
		Query q = em.createQuery(s);
		List<Staff> l = q.getResultList();
		Staff stf = null;
		if (l.size() > 0) {
			stf = l.get(0);

			if (!Validation.isEmptyLong(stf.getImage_id())) {
				stf.setImage(fileDao.find(stf.getImage_id()));
			}
			
			if(!Validation.isEmptyLong(stf.getTsStaffId())){
				stf.setScout(find(stf.getTsStaffId()));
			}
		}

		return stf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Staff> findAllDissmissedLazy(String condition, int first, int pageSize) {
		String subQuery = " SELECT staff_id FROM Salary sal WHERE sal.end_date >= '" + Helper.getCurrentDateForDb()
				+ "' ";
		String s = "SELECT s FROM Staff s WHERE staff_id NOT IN(" + subQuery + ") ";
		if (!Validation.isEmptyString(condition)) {
			s += " AND " + condition;
		}
		Query q = em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		return q.getResultList();
	}

	@Override
	public int getRowCountDissmissed(String condition) {
		String subQuery = " SELECT staff_id FROM Salary sal WHERE sal.end_date >= '" + Helper.getCurrentDateForDb()
				+ "' ";
		String s = "select COUNT(s.staff_id) FROM Staff s WHERE staff_id NOT IN(" + subQuery + ") ";
		if (!Validation.isEmptyString(condition)) {
			s += " AND " + condition;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Staff> findAllCurrentLazy(String condition, String cond2, int first, int pageSize) {
		String subQuery = " SELECT staff_id FROM Salary sal WHERE sal.end_date >= '" + Helper.getCurrentDateForDb()
				+ "' ";
		if (!Validation.isEmptyString(cond2)) {
			subQuery += " AND " + cond2;
		}
		String s = "SELECT s FROM Staff s WHERE staff_id IN(" + subQuery + ") ";
		if (!Validation.isEmptyString(condition)) {
			s += " AND " + condition;
		}
		Query q = em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		return q.getResultList();
	}

	@Override
	public int getRowCountCurrent(String condition, String cond2) {
		String subQuery = " SELECT staff_id FROM Salary sal WHERE sal.end_date >= '" + Helper.getCurrentDateForDb()
				+ "' ";
		if (!Validation.isEmptyString(cond2)) {
			subQuery += " AND " + cond2;
		}
		String s = "select COUNT(s.staff_id) FROM Staff s WHERE staff_id IN(" + subQuery + ") ";
		if (!Validation.isEmptyString(condition)) {
			s += " AND " + condition;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	public List<Object[]> getFiredList(Long a_branch_id) throws DAOException {
		try {

			Query query = this.em.createNativeQuery(
					"select s.staff_id,initcap(s.lastname)||' '||initcap(s.firstname)||' '||initcap(s.middlename) as fio from salary sa, staff s"
							+ " where sa.staff_id=s.staff_id" + " and sa.payroll_date>=to_date(SYSDATE,'YYYY-MM-DD')"
							+ " and sa.branch_id=" + a_branch_id);

			List<Object[]> results = query.getResultList();
			return results;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}
}