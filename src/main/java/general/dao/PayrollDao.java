package general.dao;

import general.output.tables.FaeaOutputTable;
import general.tables.Payroll;

import java.sql.Date;
import java.util.List;

public interface PayrollDao extends GenericDao<Payroll> {
	public List<Payroll> findByStaffId(Long a_staff_id, String a_bukrs);
	public List<Payroll> findByMonatGjahr(int a_monat, int a_gjahr, String a_bukrs);
	public List<Payroll> findByBukrsBranchAll(List<Long> al_staff_id, String a_bukrs);
	public List<Payroll> findByBukrsBranchAll2(List<Long> al_staff_id, String a_bukrs);
	public Payroll dynamicFindSinglePayroll(String a_dynamicWhere);
	public List<Payroll> findAll(String condition);
	public List<Payroll> findByBukrsBranchAllDeposit(List<Long> al_staff_id, String a_bukrs);
	public List<Payroll> findByBukrsBranchAllZablok(List<Long> al_staff_id, Date a_today, String a_bukrs);
	public List<Payroll> findByBukrsBranchAllSchet(List<Long> al_staff_id, Date a_today, String a_bukrs);
	public List<Payroll> findByBukrsBranchAllPeriod(List<Long> al_staff_id, Date begDate, Date  endDate, String a_bukrs, int a_type,String a_waers);
	public List<String> findWaersByStaffId(Long a_staff_id);
	public int dynamicFindCountPayroll(String a_dynamicWhere,List<Long> al_manager_prl);
	public List<Long> dynamicFindPayrollId(String a_dynamicWhere);
	public List<Payroll> findByBukrsBranchAvansZapros(List<Long> al_staff_id, Date a_today, String a_bukrs);
	public int dynamicFindCountPayroll(String a_dynamicWhere);
	public List<FaeaOutputTable> dynamicFindFaea(String a_dynamicWhere);
	public List<Payroll> findByBukrsBranchAllDolg(List<Long> al_staff_id, Date a_today, String a_bukrs);
	public List<Payroll> findByBukrsBranchAllDoubtfulDolg(List<Long> al_staff_id, Date a_today, String a_bukrs);
	public List<Object[]> serviceZp(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public List<Object[]> nachalnikAndAnalyticZp(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public int totalContractExceptBaku(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public List<Object[]> finAgentZp(String a_bukrs, int a_gjahr, int a_monat) throws DAOException;
	public int totalContractKazakhstan(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public List<Object[]> dynamicFrep7Result(String a_bukrs,String a_where, String a_lang) throws DAOException;
	public List<Object[]> dynamicFrep7Detail(String a_bukrs,String a_where) throws DAOException;
	public Object[] totalContractGreenLight(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public List<Object[]> nachalnik2000(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public List<Object[]> finAgent70000Tenge(String a_bukrs, int a_gjahr, int a_monat,String a_waers,double a_summa ) throws DAOException;
	
	public int totalContractRoboclean(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public int totalContractCebilon(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public int totalContractTotal(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public int totalContractRobocleanExceptBaku(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public int totalContractCebilonExceptBaku(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	public int totalContractDynamic(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,List<Long> l_countryId) throws DAOException;
	public int totalContractCebilonDynamic(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,List<Long> l_countryId) throws DAOException;
	public int totalContractRobocleanDynamic(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,List<Long> l_countryId) throws DAOException;
	public List<Object[]> findMarketingTSStaff(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException;
	
	public int totalContractsByUserBranch(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay, Long a_staff_id) throws DAOException;
	
	public List<Object[]> finManagerZp(String a_bukrs, int a_gjahr, int a_monat) throws DAOException;
	
	/*
	public List<Payroll> findByBukrsBranchAllZablok_oldwages( Date a_today);
	public List<Payroll> findByBukrsBranchAllDeposit_oldwages();
	public List<Payroll> findByBukrsBranchAllSchet_oldwages( Date a_today);*/
}
