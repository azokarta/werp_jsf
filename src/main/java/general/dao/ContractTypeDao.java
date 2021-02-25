package general.dao;

import java.util.List;

import general.tables.ContractType;

public interface ContractTypeDao extends GenericDao<ContractType>{
	public List<ContractType> findAll();
}
