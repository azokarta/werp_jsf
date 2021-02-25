package general.dao;

import general.tables.ContractOper;

import java.util.List;

public interface ContractOperDao extends GenericDao<ContractOper> {
	List<ContractOper> findAll();

}
