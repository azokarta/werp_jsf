package general.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import general.dao.PaymentScheduleDao;
import general.tables.PaymentSchedule;
import reports.dms.contract.ContractPaymentGraphic;

import org.springframework.stereotype.Component;

@Component("paymentScheduleDao")
public class PaymentScheduleDaoImpl extends GenericDaoImpl<PaymentSchedule>
		implements PaymentScheduleDao {

	@Override
	public List<PaymentSchedule> findAll() {
		Query q = this.em
				.createQuery("Select ps FROM PaymentSchedule ps order by ps.payment_date");
		return q.getResultList();
	}

	@Override
	public List<PaymentSchedule> findAll(String condition,String a_bukrs) {
		String s = "SELECT ps FROM PaymentSchedule ps";
		if (condition.length() > 0) {
			s += " WHERE bukrs="+a_bukrs +" and " + condition;
		}
		Query q = this.em.createQuery(s);
		List<PaymentSchedule> l = q.getResultList();
		return l;
	}
	
	@Override
	public List<ContractPaymentGraphic> findAllTest(String condition) {
		List<ContractPaymentGraphic> l_cpg = new ArrayList<ContractPaymentGraphic>();
		String s = "SELECT ct.contract_number,"
				+ " ct.contract_id,"
				+ "	cs.firstname,"
				+ " cs.lastname,"
				+ " cs.middlename,"
				+ " ps.payment_date,"
				+ " ps.sum,"
				+ " ps.paid "
				+ "FROM PaymentSchedule ps, Contract ct, Customer cs where "
				+ "cs.customer_id=ct.customer_id and ps.awkey=ct.awkey  "
				+ "and ct.contract_status_id in (1) "+condition;
		
		Query query = this.em.createQuery(s);
		List<Object[]> results = query.getResultList();
		int i = 0;
    	for (Object[] result : results) {
    		ContractPaymentGraphic wa_cpg = new ContractPaymentGraphic();
    		if (i == 0) {
    		wa_cpg.getContract().setContract_number((long) result[0]);
    		wa_cpg.getContract().setContract_id((long) result[1]);
    		wa_cpg.getCustomer().setFirstname(String.valueOf(result[2]));
    		wa_cpg.getCustomer().setLastname(String.valueOf(result[3]));
    		wa_cpg.getCustomer().setMiddlename(String.valueOf(result[4]));
    		}
    		
    		PaymentSchedule wa_ps = new PaymentSchedule();
    		wa_ps.setPayment_date((Date) result[5]);
    		wa_ps.setSum((double) result[6]);
    		wa_ps.setPaid((double) result[7]);
    		wa_cpg.getPsL().add(wa_ps);
    		l_cpg.add(wa_cpg);
    		i++;    		
    	  }		
		return l_cpg;
	}

	@Override
	public int updateDynamicSinglePS(Long a_ps_id, String a_bukrs,
			String a_dynamicWhere) {
		Query query = this.em.createQuery("update PaymentSchedule set "
				+ a_dynamicWhere + " where payment_schedule_id = " + a_ps_id
				+ " and bukrs = '" + a_bukrs + "'");
		return query.executeUpdate();
	}

	@Override
	public int deleteByAwkey(Long a_awkey, String a_bukrs) {
		Query query = this.em
				.createQuery("delete FROM PaymentSchedule ps where awkey = :awkey and bukrs = :bukrs");
		query.setParameter("awkey", a_awkey);
		query.setParameter("bukrs", a_bukrs);
		return query.executeUpdate();
	}

	@Override 
	public List<PaymentSchedule> findAllByAwkey(Long awkey, String a_bukrs) {
		List<PaymentSchedule> l = new ArrayList<PaymentSchedule>();
		String s = "SELECT ps FROM PaymentSchedule ps ";
		if(awkey != null && awkey > 0){
			s += " WHERE awkey = " + awkey;			
			s += " and bukrs = "  + a_bukrs;
			s += " order by ps.payment_date";
			Query q = this.em.createQuery(s);
			l = q.getResultList();
		}
		return l;
	}

	@Override
	public List<PaymentSchedule> findAllByAwkeyOrderByPaymentDate(Long awkey, String a_bukrs) {
		Query q = this.em.createQuery("SELECT ps FROM PaymentSchedule ps WHERE awkey = " + awkey + " and bukrs = "  + a_bukrs + " ORDER BY ps.payment_date ASC");
		return q.getResultList();
	}
	
	@Override
	public List<PaymentSchedule> findAllByAwkeyOrderById(Long awkey, String a_bukrs) {
		Query q = this.em.createQuery("SELECT ps FROM PaymentSchedule ps WHERE awkey = " + awkey + " and bukrs = "  + a_bukrs + " ORDER BY ps.payment_schedule_id ASC");
		return q.getResultList();
	}
	
	
	public int countAll(String a_dynamicWhere) {
		Long count;
		String s = "SELECT count(ps.payment_schedule_id) FROM PaymentSchedule ps";
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