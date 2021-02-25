package general.dao;

import java.sql.Date;
import java.util.List;

import javax.persistence.Query;

import reports.dms.contract.ContractPaymentGraphic;
import general.tables.PaymentSchedule;
import general.tables.PaymentScheduleArc;

public interface PaymentScheduleDao extends GenericDao<PaymentSchedule> {
	List<PaymentSchedule> findAll();
	List<PaymentSchedule> findAll(String condition,String a_bukrs);
	public int updateDynamicSinglePS(Long a_ps_id,String a_bukrs, String a_dynamicWhere);
	public int deleteByAwkey(Long a_awkey, String a_bukrs);
	List<PaymentSchedule> findAllByAwkey(Long awkey, String a_bukrs);
	List<PaymentSchedule> findAllByAwkeyOrderByPaymentDate(Long awkey, String a_bukrs);
	public int countAll(String a_dynamicWhere);
	public List<ContractPaymentGraphic> findAllTest(String condition);
	public List<PaymentSchedule> findAllByAwkeyOrderById(Long awkey, String a_bukrs);
}
