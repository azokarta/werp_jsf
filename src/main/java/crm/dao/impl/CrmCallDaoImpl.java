package crm.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import crm.dao.CrmCallDao;
import crm.tables.CrmCall;
import general.Validation;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.impl.GenericDaoImpl;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;

@Component("crmCallDao")
public class CrmCallDaoImpl extends GenericDaoImpl<CrmCall>implements CrmCallDao {

	@Autowired
	SalaryDao salaryDao;

	@Autowired
	PyramidDao pyramidDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmCall> findAllByContext(String context, Long contextId) {
		String s = "SELECT c FROM CrmCall c " + " LEFT JOIN fetch c.caller  "
				+ " WHERE c.context = :c1 AND c.contextId = :c2 ";
		Query q = em.createQuery(s);
		q.setParameter("c1", context);
		q.setParameter("c2", contextId);
		List<CrmCall> result = q.getResultList();

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmCall> findAllWithDetails(String bukrs, List<String> branchIds, Long managerId, Date fromDate,
			Date toDate, Integer resultId) {
		String s = "SELECT c FROM CrmCall c LEFT JOIN fetch c.caller ";
		String cond = "";
		String pyrCond = "";
		if (!Validation.isEmptyString(bukrs)) {
			cond += " c.bukrs = " + bukrs;
			pyrCond += " bukrs = " + bukrs;
		}

		if (!Validation.isEmptyCollection(branchIds)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " c.branchId IN(" + String.join(",", branchIds) + ") ";
			pyrCond += (pyrCond.length() > 0 ? " AND " : " ") + " branch_id(" + String.join(",", branchIds) + ") ";
		}

		if (!Validation.isEmptyLong(managerId)) {
			List<Salary> demosecs = pyramidDao.findAllDemosecsByBranchManagerId(bukrs, new Long(branchIds.get(0)),
					managerId);
			if (demosecs.size() > 0) {
				String[] ids = new String[demosecs.size()];
				for (int c = 0; c < demosecs.size(); c++) {
					ids[c] = demosecs.get(c).getStaff_id().toString();
				}

				cond += (cond.length() > 0 ? "  AND " : " ") + " c.callerId IN(" + String.join(",", ids) + ") ";
			}

			pyrCond += (pyrCond.length() > 0 ? " AND " : " ") + " staff_id = " + managerId;
		}

		if (resultId != null && resultId > 0) {
			cond += (cond.length() > 0 ? " AND " : " ") + " c.resultId = " + resultId;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (fromDate != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " c.dateTime >= '" + sdf.format(fromDate) + "' ";
		}

		if (toDate != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " c.dateTime <= '" + sdf.format(toDate) + "' ";
		}

		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s, CrmCall.class);
		return q.getResultList();
		// List<CrmCall> out = q.getResultList();
		//
		// Map<Long, Staff> managersMap = new HashMap<>();
		// for(Pyramid p:pyramidDao.findAllWithStaff(pyrCond)){
		// managersMap.put(p.getPyramid_id(), p.getStaff());
		// }
		// for(CrmCall cc:out){
		// cc.setManager(managersMap.get(cc.getCaller()));
		// }
		// return null;
	}

}
