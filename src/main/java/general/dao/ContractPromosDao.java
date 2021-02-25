package general.dao;

import java.util.List;

import general.tables.ContractPromos;

public interface ContractPromosDao extends GenericDao<ContractPromos> {
	public List<ContractPromos> findAll();
	public List<ContractPromos> findAllByContrID(Long contr_id);
	public int deleteByContractId(Long a_contractId);
	public Long findContrID(Long contr_id,Long a_matnr) ;
}
