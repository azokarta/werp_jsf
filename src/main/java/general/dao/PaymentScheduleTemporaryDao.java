package general.dao;

import general.tables.PaymentScheduleTemporary;

import java.util.List;

import reports.dms.contract.ContractPaymentGraphic;

public interface PaymentScheduleTemporaryDao extends GenericDao<PaymentScheduleTemporary> {
	List<PaymentScheduleTemporary> findAll();
	List<PaymentScheduleTemporary> findAll(String condition);
	public int deleteByContractId(Long contractId);
	List<PaymentScheduleTemporary> findAllByContractId(Long contractId);
	List<PaymentScheduleTemporary> findAllByContractIdOrderByPaymentDate(Long contractId);
	public int countAll(String a_dynamicWhere);
	public List<PaymentScheduleTemporary> findAllByContractIdOrderById(Long contractId);
}
