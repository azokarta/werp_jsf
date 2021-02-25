package crm.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import crm.dao.CrmDocVisitDao;
import crm.tables.CrmDocVisit;
import crm.tables.search.CrmDocVisitSearch;
import general.Validation;
import general.dao.impl.GenericDaoImpl;

@Component("crmDocVisitDao")
public class CrmDocVisitDaoImpl extends GenericDaoImpl<CrmDocVisit>implements CrmDocVisitDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmDocVisit> findAll(CrmDocVisitSearch searchModel) {
		String s = "SELECT d FROM CrmDocVisit d LEFT JOIN fetch d.visitor ";
		String cond = "";

		if (!Validation.isEmptyString(searchModel.getBukrs())) {
			cond += " d.bukrs = " + searchModel.getBukrs();
		}

		if (Validation.isEmptyLong(searchModel.getBranchId())) {

			if (!Validation.isEmptyCollection(searchModel.getBranchIds())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId IN("
						+ String.join(",", searchModel.getBranchIds()) + ") ";
			}
		} else {
			cond += (cond.length() > 0 ? " AND " : " ") + " d.branchId = " + searchModel.getBranchId();
		}

		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}

		s += " ORDER BY doc_date ASC ";

		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public List<CrmDocVisit> findAllWithDetails(CrmDocVisitSearch searchModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
