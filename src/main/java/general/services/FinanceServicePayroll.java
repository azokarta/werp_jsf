package general.services;

import java.util.List;
import java.util.Map;

import general.dao.DAOException;
import general.services.FinanceServicePayrollImpl.ExpAll;
import general.tables.Branch;
import general.tables.Payroll;
import general.tables.TempPayroll;

import org.springframework.transaction.annotation.Transactional;

public interface FinanceServicePayroll {
	@Transactional
	public boolean hrppPayrollPayments (String a_bukrs,  Long a_branch_id, Long a_customer_id, Long a_staff_id,
			String a_tcode, Long a_userId, double a_summa, String a_waers, String a_cashbankGLA,Long selectedAvansOdob_payroll_id,String a_bktxt ) throws DAOException;
	@Transactional
	public void createSalaryPayments(List<TempPayroll> al_tp, String a_bukrs, int a_gjahr, int a_monat, Long a_userId) throws DAOException;
	
	public Long insertSalaryFi(String a_bukrs,Long a_userId,Long a_branchId, String a_waers, double a_amount, double a_kursf, Long a_customer_id, String a_shkzg,List<ExpAll> l_ea, Map<Long,Branch> l_brn_map,String a_tcode,int a_type) throws DAOException;
	
	@Transactional
	public void fahrbSave(String a_bukrs,Long a_userId, Long a_branchId, int a_oper_type, Payroll a_prl);
	
	//public void insertSalaryFi_oldwages(String a_bukrs,Long a_userId,Long a_branchId, String a_waers, double a_amount, double a_kursf, Long a_customer_id, String a_shkzg) throws DAOException;
	
	//@Transactional
	//public void old_wages() throws DAOException;
}
