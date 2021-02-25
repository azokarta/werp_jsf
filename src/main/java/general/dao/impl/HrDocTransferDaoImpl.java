package general.dao.impl;

import general.Helper;
import general.Validation;
import general.dao.HrDocTransferDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.HrDocTransfer;
import general.tables.HrDocTransferApprover;
import general.tables.HrDocTransferItem;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("hrDocTransferDao")
public class HrDocTransferDaoImpl extends GenericDaoImpl<HrDocTransfer> implements HrDocTransferDao {

	@Autowired
	UserDao userDao;

	@Autowired
	StaffDao staffDao;

	@Autowired
	SalaryDao salaryDao;

	@Override
	public HrDocTransfer findWithDetail(Long id) {
		HrDocTransfer doc = super.find(id);
		if (doc != null) {
			Set<HrDocTransferItem> items = new HashSet<>();
			for (HrDocTransferItem item : findItemsWithDetailByDocId(doc.getId())) {
				items.add(item);
			}
			doc.setHrDocTransferItems(items);

			User us = userDao.findWithStaff(doc.getCreatedBy());
			if (us != null) {
				doc.setCreator(us.getStaff());
			}

			doc.setHrDocTransferApprovers(findApproversByDocId(doc.getId()));

			User resp = userDao.findWithStaff(doc.getResponsibleId());
			if (resp != null) {
				doc.setResponsible(resp.getStaff());
			}
		}
		return doc;
	}

	@SuppressWarnings("unchecked")
	private List<HrDocTransferItem> findItemsWithDetailByDocId(Long docId) {

		Map<Long, Staff> stfMap = staffDao.getMappedList("");

		String s = "SELECT i,s1,s2 FROM HrDocTransferItem i, Salary s1, Staff s2 WHERE doc_id = " + docId;
		s += " AND i.fromSalaryId = s1.salary_id AND s1.staff_id = s2.staff_id ";
		Query q = em.createQuery(s);
		List<HrDocTransferItem> out = new ArrayList<>();
		List<Object[]> result = q.getResultList();
		for (Object[] o : result) {
			HrDocTransferItem item = (HrDocTransferItem) o[0];
			Salary sal = (Salary) o[1];
			Staff stf = (Staff) o[2];

			sal.setP_staff(stf);
			item.setFromSalary(sal);

			if (!Validation.isEmptyLong(item.getFromManagerId())) {
				item.setFromManager(stfMap.get(item.getFromManagerId()));
			}

			if (!Validation.isEmptyLong(item.getManagerId())) {
				item.setManager(stfMap.get(item.getManagerId()));
			}

			out.add(item);
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	private List<HrDocTransferApprover> findApproversByDocId(Long docId) {
		Query q = em.createQuery("SELECT a FROM HrDocTransferApprover a WHERE doc_id = " + docId);
		return q.getResultList();
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
	public Long getStaffParentManagerId(Salary salary) {

		String s = " SELECT staff_id FROM pyramid WHERE pyramid_id IN("
				+ " SELECT parent_pyramid_id FROM pyramid WHERE position_id = %d "
				+ "	AND staff_id = %d AND branch_id = %d )";
		Query q = em.createNativeQuery(
				String.format(s, salary.getPosition_id(), salary.getStaff_id(), salary.getBranch_id()));
		Object out = q.getSingleResult();

		if (out == null) {
			return 0L;
		}
		try {
			return Long.valueOf(out.toString());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocTransfer> findAll(String cond) {
		String s = " SELECT h FROM HrDocTransfer h ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocTransferItem> findAllItems(Long docId) {
		String s = "SELECT h FROM HrDocTransferItem h WHERE doc_id= " + docId;
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public List<HrDocTransferApprover> findAllApprovers(Long docId) {
		return findApproversByDocId(docId);
	}
}
