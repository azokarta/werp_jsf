package general.dao.impl;

import general.Helper;
import general.Validation;
import general.dao.HrDocActionLogDao;
import general.dao.HrDocApproverDao;
import general.dao.HrDocDao;
import general.dao.HrDocItemDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.HrDoc;
import general.tables.HrDocActionLog;
import general.tables.HrDocApprover;
import general.tables.HrDocItem;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("hrDocDao")
public class HrDocDaoImpl extends GenericDaoImpl<HrDoc>implements HrDocDao {

	@Autowired
	StaffDao stfDao;

	@Autowired
	HrDocItemDao hdItemDao;

	@Autowired
	HrDocApproverDao hdApproverDao;

	@Autowired
	UserDao userDao;

	@Autowired
	SalaryDao salDao;

	@Autowired
	HrDocActionLogDao logDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDoc> findAll(String cond) {
		String s = " SELECT h FROM HrDoc h ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public HrDoc findWithDetail(Long id) {
		HrDoc doc = super.find(id);
		if (doc != null) {
			Map<Long, Staff> stfMap = stfDao.getMappedList("");
			Map<Long, Salary> salaryMap = new HashMap<>();
			List<HrDocItem> items = hdItemDao.findAll(" doc_id = " + doc.getId());
			List<User> userList = userDao.findAll("");
			Map<Long, User> userMap = new HashMap<>();
			for (User u : userList) {
				userMap.put(u.getUser_id(), u);
			}

			List<HrDocActionLog> logs = logDao.findAll(" doc_id = " + doc.getId() + " ORDER BY id DESC ");
			for (HrDocActionLog log : logs) {
				User logUser = userMap.get(log.getCreatedBy());
				if (logUser == null) {
					log.setCreator(new Staff());
				} else {
					Staff stf = stfMap.get(logUser.getStaff_id());
					log.setCreator(stf == null ? new Staff() : stf);
				}
			}

			doc.setLogs(logs);

			if (items.size() > 0) {
				String[] salaryIds = new String[items.size()];
				for (int k = 0; k < items.size(); k++) {
					if (!Validation.isEmptyLong(items.get(k).getSalaryId())) {
						salaryIds[k] = items.get(k).getSalaryId().toString();
					}
				}

				salaryMap = getSalaryMap(salaryIds);
			}
			for (HrDocItem it : items) {
				Salary sal;
				if ((sal = salaryMap.get(it.getSalaryId())) != null) {
					it.setSalary(sal);
				}
				it.setStaff(stfMap.get(it.getStaffId()));
				it.setManager(stfMap.get(it.getManagerId()));
				it.setOldManager(stfMap.get(it.getOldManagerId()));
				it.setHrDoc(doc);
			}

			doc.setHrDocItems(items);

			List<HrDocApprover> appList = hdApproverDao.findAll(" doc_id =  " + doc.getId() + " ORDER BY id ASC ");

			for (HrDocApprover app : appList) {
				// app.setStaffId(app.);
				// r.setStaff(stfMap.get(r.getStaffId()));
				// r.setHrDocument(doc);
				app.setHrDoc(doc);
			}

			doc.setHrDocApprovers(appList);

			User user = userDao.findWithStaff(doc.getCreatedBy());
			if (user != null) {
				doc.setCreator(user.getStaff());
			}

			User user2 = userDao.findWithStaff(doc.getResponsibleId());
			if (user2 != null) {
				doc.setResponsible(user2.getStaff());
			}
		}

		return doc;
	}

	private Map<Long, Salary> getSalaryMap(String[] ids) {
		List<Salary> l = salDao.findAll(" salary_id IN(" + String.join(",", ids) + ") ");
		Map<Long, Salary> out = new HashMap<>();
		for (Salary s : l) {
			out.put(s.getSalary_id(), s);
		}

		return out;
	}

	@Override
	public int getRowCount(String condition) {
		String s = " SELECT COUNT(id) FROM HrDoc ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}

		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDoc> findAllLazy(String cond, int first, int pageSize) {
		String s = " SELECT d FROM HrDoc d ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<HrDoc> l = q.getResultList();
		return l;
	}

	@Override
	public Long getStaffParentManagerId(Salary salary) {

		String s = " SELECT staff_id FROM pyramid WHERE pyramid_id IN("
				+ " SELECT parent_pyramid_id FROM pyramid WHERE position_id = %d "
				+ "	AND staff_id = %d AND branch_id = %d )";
		Query q = em.createNativeQuery(
				String.format(s, salary.getPosition_id(), salary.getStaff_id(), salary.getBranch_id()));
		try{
			Object out = q.getSingleResult();
			if(out == null){
				return 0L;
			}
			try {
				return Long.valueOf(out.toString());
			} catch (NumberFormatException e) {
				return 0L;
			}
		}catch(NoResultException e){
			return 0L;
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, Salary> getUserSalaryMap(String[] branchIds, Long positionId) {
		Map<Long, Salary> out = new HashMap<>();
		if (branchIds.length > 0) {
			String s = " SELECT u, s, stf FROM User u, Salary s, Staff stf WHERE s.staff_id = u.staff_id AND s.staff_id=stf.staff_id AND ";
			s += String.format(" s.branch_id IN(%s)", String.join(",", branchIds));
			s += " AND s.position_id = 10 "; // 10 - position id Директора
			s += " AND s.beg_date <= '" + Helper.getCurrentDateForDb() + "' AND s.end_date >= '"
					+ Helper.getCurrentDateForDb() + "' ";

			Query q = em.createQuery(s);
			List<Object[]> result = q.getResultList();
			for (Object[] o : result) {
				User user = (User) o[0];
				Salary salary = (Salary) o[1];
				Staff staff = (Staff) o[2];
				salary.setP_staff(staff);
				out.put(user.getUser_id(), salary);
			}
		}
		return out;
	}

	@Override
	public Long getMaxRegNumber(int typeId) {
		String s = " SELECT MAX(regNumber) FROM HrDoc WHERE type_id = " + typeId;
		Query query = this.em.createQuery(s);
		return (Long) query.getSingleResult();
	}

	@Override
	public HrDoc create(HrDoc t) {
		Long maxRegNumber = 0L;
		try {
			maxRegNumber = Long.valueOf(getMaxRegNumber(t.getTypeId()));
		} catch (Exception e) {
			maxRegNumber = 0L;
		}
		t.setRegNumber(maxRegNumber + 1);
		return super.create(t);
	}
}