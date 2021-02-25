package crm.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import crm.dao.CrmDocDemoDao;
import crm.tables.CrmDocDemo;
import crm.tables.search.CrmDocDemoSearch;
import general.Validation;
import general.dao.impl.GenericDaoImpl;

@Component("crmDocDemoDao")
public class CrmDocDemoDaoImpl extends GenericDaoImpl<CrmDocDemo>implements CrmDocDemoDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocDemo> findAllForFutureTab(CrmDocDemoSearch searchModel) {
		String cond = "";

		String s = "SELECT d FROM CrmDocDemo d LEFT JOIN fetch d.dealer ";

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fromDate = sdf.format(cal.getTime()) + " 00:00:00 ";

		cond += (cond.length() > 0 ? " AND " : "") + " dateTime >= '" + fromDate + "' ";
		s += " ORDER BY dateTime ASC ";
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocDemo> findAll(CrmDocDemoSearch searchModel) {
		String s = "SELECT d FROM CrmDocDemo d LEFT JOIN fetch d.dealer LEFT JOIN fetch d.appointer ";
		String cond = searchModel.getCondition();
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		s += " ORDER BY date_time ASC ";

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocDemo> findAllRecoDemos(Long recoId) {
		String s = "SELECT d FROM CrmDocDemo d LEFT JOIN fetch d.dealer WHERE d.recoId = :r";
		s += " ORDER BY date_time ASC ";

		Query q = em.createQuery(s);
		q.setParameter("r", recoId);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocDemo> findAllByCondition(String cond) {
		String s = "SELECT d FROM CrmDocDemo d ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocDemo> findAllSoldDemos(CrmDocDemoSearch searchModel) {
		String s = "SELECT d FROM CrmDocDemo d LEFT JOIN fetch d.dealer " + " LEFT JOIN fetch d.appointer ";
		s += " WHERE contract_number = 0 ";
		String modelCond = searchModel.getCondition();
		if (!Validation.isEmptyString(modelCond)) {
			s += " AND " + modelCond;
		}

		s += " ORDER BY date_time ASC ";

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CrmDocDemo findByContractNumber(Long contractNumber) {
		String s = "SELECT c FROM CrmDocDemo c WHERE contract_number = " + contractNumber;
		Query q = em.createQuery(s);
		List<CrmDocDemo> l = q.getResultList();
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

}
