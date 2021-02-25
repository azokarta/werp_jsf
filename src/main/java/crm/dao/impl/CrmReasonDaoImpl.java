package crm.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import crm.dao.CrmReasonDao;
import crm.tables.CrmReason;
import general.Validation;
import general.dao.impl.GenericDaoImpl;

@Component("crmReasonDao")
public class CrmReasonDaoImpl extends GenericDaoImpl<CrmReason>implements CrmReasonDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmReason> findAll(String cond) {
		String s = "SELECT r FROM CrmReason r ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CrmReason> findAllByType(Integer typeId) {
		Query q = em.createQuery("SELECT r FROM CrmReason r WHERE typeId = :t");
		q.setParameter("t", typeId);
		
		return q.getResultList();
	}
}
