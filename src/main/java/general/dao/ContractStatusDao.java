package general.dao;

import general.tables.ContractStatus;

import java.util.List;

public interface ContractStatusDao extends GenericDao<ContractStatus>{
	public List<ContractStatus> findAll();
	public List<ContractStatus> findAllActive();
}
