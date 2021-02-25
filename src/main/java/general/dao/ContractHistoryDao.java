package general.dao;

import general.tables.ContractHistory;
import general.tables.ContractType;

import java.sql.Date;
import java.util.List;

public interface ContractHistoryDao  extends GenericDao<ContractHistory>{
	public List<ContractHistory> findAll();
	public List<ContractHistory> findAllByContractID(Long a_conId);
	public ContractHistory findSinglePriceChange(Date a_lastDate, Long a_con_id);
}
