package general.dao.impl;

import general.dao.PaymentScheduleTemporaryDao;
import general.tables.PaymentSchedule;
import general.tables.PaymentScheduleTemporary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import reports.dms.contract.ContractPaymentGraphic;

@Component("paymentScheduleTemporaryDao")
public class PaymentScheduleTemporaryDaoImpl extends GenericDaoImpl<PaymentScheduleTemporary>
		implements PaymentScheduleTemporaryDao {

	@Override
	public List<PaymentScheduleTemporary> findAll() {
		Query q = this.em
				.createQuery("Select ps FROM PaymentScheduleTemporary ps order by ps.payment_date");
		return q.getResultList();
	}

	@Override
	public List<PaymentScheduleTemporary> findAll(String condition) {
		String s = "SELECT ps FROM PaymentScheduleTemporary ps";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		List<PaymentScheduleTemporary> l = q.getResultList();
		return l;
	}
	
	public int updateDynamicSinglePS(Long a_ps_id, String a_bukrs,
			String a_dynamicWhere) {
		Query query = this.em.createQuery("update PaymentScheduleTemporary set "
				+ a_dynamicWhere + " where id = " + a_ps_id
				+ " and bukrs = '" + a_bukrs + "'");
		return query.executeUpdate();
	}

	@Override
	public int deleteByContractId(Long contractId) {
		Query query = this.em
				.createQuery("delete FROM PaymentScheduleTemporary ps where contractId = :conId");
		query.setParameter("conId", contractId);
		return query.executeUpdate();
	}

	@Override 
	public List<PaymentScheduleTemporary> findAllByContractId(Long contractId) {
		List<PaymentScheduleTemporary> l = new ArrayList<PaymentScheduleTemporary>();
		String s = "SELECT ps FROM PaymentScheduleTemporary ps ";
		if(contractId != null && contractId > 0){
			s += " WHERE ps.contractId = " + contractId;
			s += " order by ps.payment_date";
			Query q = this.em.createQuery(s);
			l = q.getResultList();
		}
		return l;
	}

	@Override
	public List<PaymentScheduleTemporary> findAllByContractIdOrderByPaymentDate(Long contractId) {
		Query q = this.em.createQuery("SELECT ps FROM PaymentScheduleTemporary ps WHERE ps.contractId = " + contractId+ " ORDER BY ps.payment_date ASC");
		return q.getResultList();
	}
	
	@Override
	public List<PaymentScheduleTemporary> findAllByContractIdOrderById(Long contractId) {
		Query q = this.em.createQuery("SELECT ps FROM PaymentScheduleTemporary ps WHERE ps.contractId = " + contractId+ " ORDER BY ps.id ASC");
		return q.getResultList();
	}
	
	public int countAll(String a_dynamicWhere) {
		Long count;
		String s = "SELECT count(ps.id) FROM PaymentScheduleTemporary ps";
		if (a_dynamicWhere.length() > 0) {
			s += " WHERE " + a_dynamicWhere;
		}
		Query q = this.em.createQuery(s);
		count = (Long) q.getSingleResult();
		if (count == null || count == 0)
		{
			return 0;
		}
		else
		{
			return (int) (long) count;
		}
	}
	
}