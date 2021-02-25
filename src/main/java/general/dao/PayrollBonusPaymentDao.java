package general.dao;

import java.util.List;

import general.tables.PayrollBonusPayment;

public interface PayrollBonusPaymentDao extends GenericDao<PayrollBonusPayment>{
	public List<PayrollBonusPayment> findAll();
	public void deleteAll();
}
