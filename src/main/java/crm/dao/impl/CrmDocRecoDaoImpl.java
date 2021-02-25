package crm.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import crm.constants.CommonConstants;
import crm.dao.CrmCallDao;
import crm.dao.CrmDocDemoDao;
import crm.dao.CrmDocRecoDao;
import crm.tables.CrmDocReco;
import crm.tables.CrmPhone;
import crm.tables.search.CrmDocRecoSearch;
import general.Validation;
import general.dao.StaffDao;
import general.dao.impl.GenericDaoImpl;

@Component("crmDocRecoDao")
public class CrmDocRecoDaoImpl extends GenericDaoImpl<CrmDocReco>implements CrmDocRecoDao {

	@Autowired
	StaffDao staffDao;

	@Autowired
	CrmCallDao callDao;

	@Autowired
	CrmDocDemoDao demoDao;

	@Override
	public List<CrmDocReco> findAllCurrentByCallerIsDealer(int callerIsDealer) {
		// @TODO Filter By Status
		// String s = " SELECT d FROM CrmDocReco d WHERE caller_is_dealer = :c
		// ORDER BY call_time_from DESC ";
		// Query q = em.createQuery(s);
		// q.setParameter("c", callerIsDealer);
		//
		// return q.getResultList();
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocReco> findAllWithDetailsByCondition(CrmDocRecoSearch searchModel) {
		String cond = "";
		if (!Validation.isEmptyString(searchModel.getBukrs())) {
			cond = " d.bukrs = " + searchModel.getBukrs();
		}

		if (Validation.isEmptyLong(searchModel.getBranchId())) {
			if (!Validation.isEmptyCollection(searchModel.getBranchIds())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId IN("
						+ String.join(",", searchModel.getBranchIds()) + ") ";
			}
		} else {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId = " + searchModel.getBranchId();
		}

		if (!Validation.isEmptyCollection(searchModel.getResponsibleIds())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.responsibleId IN("
					+ String.join(",", searchModel.getResponsibleIds()) + ") ";
		}

		if (!Validation.isEmptyLong(searchModel.getResponsibleId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.responsibleId = " + searchModel.getResponsibleId();
		}

		if (searchModel.getCallerIsDealer() == 1 || searchModel.getCallerIsDealer() == 0) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.callerIsDealer = " + searchModel.getCallerIsDealer();
		}

		if (!Validation.isEmptyCollection(searchModel.getStatuses())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.statusId IN("
					+ String.join(",", searchModel.getStatuses()) + ") ";
		}

		if (searchModel.getStatusId() != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.statusId=" + searchModel.getStatusId();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (searchModel.getFromDate() != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.docDate >= '" + sdf.format(searchModel.getFromDate())
					+ "' ";
		}

		if (searchModel.getToDate() != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.docDate <= '" + sdf.format(searchModel.getToDate())
					+ "' ";
		}

		String s = " SELECT d FROM CrmDocReco d LEFT JOIN fetch d.responsible r ";

		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocReco> findAllForDemosec(CrmDocRecoSearch searchModel) {
		String cond = "";
		if (!Validation.isEmptyString(searchModel.getBukrs())) {
			cond = " d.bukrs = " + searchModel.getBukrs();
		}

		if (Validation.isEmptyLong(searchModel.getBranchId())) {
			if (!Validation.isEmptyCollection(searchModel.getBranchIds())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId IN("
						+ String.join(",", searchModel.getBranchIds()) + ") ";
			}
		} else {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId = " + searchModel.getBranchId();
		}

		if (!Validation.isEmptyCollection(searchModel.getResponsibleIds())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.responsibleId IN("
					+ String.join(",", searchModel.getResponsibleIds()) + ") ";
		}

		if (!Validation.isEmptyLong(searchModel.getResponsibleId())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.responsibleId = " + searchModel.getResponsibleId();
		}

		if (searchModel.getCallerIsDealer() == 1 || searchModel.getCallerIsDealer() == 0) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.callerIsDealer = " + searchModel.getCallerIsDealer();
		}

		if (!Validation.isEmptyCollection(searchModel.getStatuses())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.statusId IN("
					+ String.join(",", searchModel.getStatuses()) + ") ";
		}

		if (searchModel.getStatusId() != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.statusId=" + searchModel.getStatusId();
		}

		String s = " SELECT d FROM CrmDocReco d LEFT JOIN fetch d.responsible r LEFT JOIN fetch d.phones ph ";

		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		s += " ORDER BY d.callDate ASC ";

		Query q = em.createQuery(s);
		List<CrmDocReco> result = q.getResultList();
		Map<Long, Integer> tempMap = new HashMap<>();
		List<CrmDocReco> out = new ArrayList<>();

		for (CrmDocReco reco : result) {
			if (!tempMap.containsKey(reco.getId())) {
				out.add(reco);
				tempMap.put(reco.getId(), 1);
			}
		}

		Collections.sort(out, new Comparator<CrmDocReco>() {

			@Override
			public int compare(CrmDocReco o1, CrmDocReco o2) {
				return o1.getCallDate().compareTo(o2.getCallDate());
			}

		});

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CrmDocReco findWithDetail(Long id) {
		String s = " SELECT d FROM CrmDocReco d LEFT JOIN fetch d.responsible r WHERE d.id = " + id;
		Query q = em.createQuery(s);
		List<CrmDocReco> result = q.getResultList();
		if (Validation.isEmptyCollection(result)) {
			return null;
		}

		CrmDocReco reco = result.get(0);
		if (reco != null) {
			reco.setOwner(staffDao.find(reco.getOwnerId()));
			reco.setCalls(callDao.findAllByContext(CommonConstants.CONTEXT_RECO, reco.getId()));
			reco.setDemos(demoDao.findAllRecoDemos(reco.getId()));
		}
		return reco;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocReco> findAllByCondition(String cond) {
		String s = " SELECT d FROM CrmDocReco d ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

}
