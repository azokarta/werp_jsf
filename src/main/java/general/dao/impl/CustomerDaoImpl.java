package general.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import general.GeneralUtil;
import general.Validation;
import general.output.tables.Podotchet;
import general.tables.ContractType;
import general.tables.Customer;
import general.tables.report.CustomerReport;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;

@Component("customerDao")
public class CustomerDaoImpl extends GenericDaoImpl<Customer> implements CustomerDao {

	@Autowired
	ContractTypeDao conTypeDao;

	public Customer loadUserByUsername(String username) {
		Query query = this.em.createQuery("select name FROM Customer u where u.iin_bin= :username");
		query.setParameter("username", username);
		List<Customer> users = query.getResultList();
		if (users != null && users.size() == 1) {
			return users.get(0);
		}
		return null;
	}

	public Long countCustomerbyIinBin(String a_iin_bin) {
		Query query = this.em.createQuery("select COUNT(c.customer_id) FROM Customer c where c.iin_bin= :iin_bin");
		query.setParameter("iin_bin", a_iin_bin);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public Long countCustomerbyIinBinNotId(Long a_customer_id, String a_iin_bin) {
		Query query = this.em.createQuery(
				"select COUNT(c) FROM Customer c where c.iin_bin= :iin_bin and c.customer_id <> :customer_id");
		query.setParameter("iin_bin", a_iin_bin);
		query.setParameter("customer_id", a_customer_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public Customer findByIinBin(String a_iin_bin) {
		Query query = this.em.createQuery("select c FROM Customer c where c.iin_bin= :iin_bin");
		query.setParameter("iin_bin", a_iin_bin);
		Customer cus = (Customer) query.getSingleResult();
		return cus;
	}

	public Long countCustomerbyId(Long a_customer_id) {
		Query query = this.em
				.createQuery("select COUNT(c.customer_id) FROM Customer c where c.customer_id = :customer_id");
		query.setParameter("customer_id", a_customer_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public List<Customer> dynamicFindCustomers(String a_dynamicWhere) {

		Query query = this.em.createQuery("select c FROM Customer c where " + a_dynamicWhere);
		query.setMaxResults(20);
		List<Customer> cus = query.getResultList();
		return cus;
	}

	public List<Podotchet> dynamicFindCustomersPodotchet(String a_dynamicWhere) {

		Query query = this.em.createQuery("select c.customer_id" + ", c.firstname" + ", c.lastname" + ", c.middlename"
				+ ", c.name" + ", c.fiz_yur" + ", c.iin_bin FROM Customer c where " + a_dynamicWhere);
		query.setMaxResults(20);
		List<Podotchet> rt_list = new ArrayList<Podotchet>();

		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {

			Podotchet rt = new Podotchet();
			rt.setCustomer_id((long) result[0]);
			if (result[1] != null)
				rt.setFirstname(String.valueOf(result[1]));
			if (result[2] != null)
				rt.setLastname(String.valueOf(result[2]));
			if (result[3] != null)
				rt.setMiddlename(String.valueOf(result[3]));
			if (result[4] != null)
				rt.setName(String.valueOf(result[4]));
			rt.setFiz_yur((int) result[5]);
			rt.setIin_bin(String.valueOf(result[6]));
			rt_list.add(rt);
		}
		return rt_list;
	}

	public Long countCustomerEmployee(Long a_customer_id) {
		Query query = this.em
				.createQuery("select count(cus.customer_id) " 
						+ "FROM  Salary sal, Staff stf, Customer cus "
						+ "where sal.salary_id =  some ( " 
							+ "SELECT max(sal2.salary_id) " + "FROM Salary sal2 "
							+ "where sal2.staff_id = sal.staff_id " + "group by sal2.staff_id " + ") "
						+ "and sal.staff_id = stf.staff_id " + "and cus.staff_id = stf.staff_id "
						+ "and sal.position_id <> 1 " 
						+ "and cus.customer_id = :customer_id");
		query.setParameter("customer_id", a_customer_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}
	
	public Long countCustomerEmployeeByDate(Long a_customer_id, Date inDate) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Query query = this.em
			.createQuery("select count(cus.customer_id) " 
					+ "FROM  Salary sal, Staff stf, Customer cus "
					+ "where sal.salary_id =  some ( " 
						+ "SELECT max(sal2.salary_id) " + "FROM Salary sal2 "
						+ "where sal2.staff_id = sal.staff_id " + "group by sal2.staff_id " + ") "
					+ "and sal.staff_id = stf.staff_id " + "and cus.staff_id = stf.staff_id "
					+ "and sal.position_id <> 1 " 
					+ "and '" + formatter.format(inDate) + "' between sal.beg_date and sal.end_date "
					+ "and cus.customer_id = :customer_id");
		query.setParameter("customer_id", a_customer_id);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	public Customer getById(Long customer_id) {
		Query query = this.em.createQuery("select c FROM Customer c where c.customer_id= :customer_id");
		query.setParameter("customer_id", customer_id);
		Customer cus = (Customer) query.getSingleResult();
		return cus;
	}

	@Override
	public Customer findByStaffId(Long staffId) {
		Query q = this.em.createQuery("SELECT c FROM Customer c WHERE c.staff_id = :staff_id");
		q.setParameter("staff_id", staffId);
		List<Customer> l = q.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@Override
	public Long getCusIdByStaffId(Long staffId) {
		Query q = this.em.createQuery("SELECT c.customer_id FROM Customer c WHERE c.staff_id = :staff_id");
		q.setParameter("staff_id", staffId);
		List<Long> l = q.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> findAll(String condition) {
		String s = " SELECT c FROM Customer c ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		// q.setFirstResult(0);
		// q.setMaxResults(100);
		List<Customer> l = q.getResultList();
		return l;
	}

	@Override
	public List<Customer> findAll(String condition, int first, int max) {
		String s = " SELECT c FROM Customer c ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(max);
		List<Customer> l = q.getResultList();
		return l;
	}

	@Override
	public int getRowCount(String condition) {
		String s = "select COUNT(c.customer_id) FROM Customer c ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@Override
	public Map<Long, Customer> getMappedList(String cond) {
		Map<Long, Customer> out = new HashMap<Long, Customer>();
		for (Customer c : findAll(cond)) {
			out.put(c.getId(), c);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAnalyticsRep1Data(String bukrs, Long branchId, int day, int month) {

		String cond1 = "";
		if (!Validation.isEmptyString(bukrs)) {
			cond1 = " bukrs = '" + bukrs + "' ";
		}
		if (!Validation.isEmptyLong(branchId)) {
			cond1 += (cond1.length() > 0 ? " AND " : " ") + " branch_id = " + branchId;
		}

		String dm = (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;

		String cond = "";
		if (cond1.length() > 0) {
			cond += " AND to_char(birthday,'mm-dd') = '%s' AND " + cond1;
		} else {
			cond = " AND to_char(birthday,'mm-dd') = '%s' ";
		}
		
		String subQuery = " (SELECT customer_id, wm_concat(tel_mob1 || ',' || tel_mob2) AS phone FROM address GROUP BY customer_id) a ";
		String s = String
				.format(" SELECT c.customer_id, c.firstname, c.middlename, c.lastname,c.birthday, ct.contract_id,"
						//+ " (SELECT tel_mob1 || ',' || tel_mob2 FROM address WHERE customer_id = c.customer_id) AS phone, "
						+ "ct.contract_type_id, ct.contract_date FROM customer c, contract ct"
						+ " WHERE c.customer_id = ct.customer_id " + cond + " ", dm);
		s = "SELECT c1.*,a.phone FROM (" + s + ") c1 LEFT JOIN "
				+ "  " + subQuery + ""
						+ " ON c1.customer_id=a.customer_id ";
			
		//System.out.println("DD: " + s);
		Query q = em.createNativeQuery(s);
		return q.getResultList();
	}

}