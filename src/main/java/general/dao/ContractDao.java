package general.dao;

import general.output.tables.FacmassinOutputTable;
import general.tables.Contract;
import general.tables.report.SalesReportOutput;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import dms.contract.ContractDetails;

public interface ContractDao extends GenericDao<Contract>{
	public Long countContractByNumber(Long a_contract_number);
	public Contract findByContractNumber(Long a_contract_number); 
	public Contract findByTovarSN(String tovarSN); 
	public Contract getByContractId(Long a_contract_id);
	public List<Contract> dynamicFindContracts(String a_dynamicWhere);
	public int updateDynamicSingleCon(Long a_con_num, String a_dynamicWhere);
	//public List<Contract> findCollectorAmount(String a_bukrs, Date a_begDate,Date a_endDate);
	public Map<Long,Contract> findCollectedAmount(String a_bukrs, Date a_begDate,Date a_endDate);
	//public Map<Long,Contract> findCollectedAmountClosedContracts(String a_bukrs, Date a_begDate, Date a_endDate);
	public int updateDynamicSingleConPaid(Long a_con_num, double a_dmbtr);
	public List<ContractDetails> findAll(String condition,int first,int max);
	public int getRowCount(String condition);
	public List<FacmassinOutputTable> dynamicFindFacmassin(String a_dynamicWhere, String a_date) throws DAOException;
	
	public List<SalesReportOutput> getSalesReportBranch(String queryScript, String table) throws DAOException;
	public List<SalesReportOutput> getSalesReportGroup(String queryScript, String table) throws DAOException;
	public List<SalesReportOutput> getSalesReportStaff(String queryScript, String table) throws DAOException;

	public boolean checkPyramid(String inBukrs, java.util.Date inDate) throws DAOException;
}
