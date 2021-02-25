package general.dao;

import java.util.List;

import general.tables.Deposit;

public interface DepositDao extends GenericDao<Deposit>{
	public List<Deposit> findAll();
	public List<Deposit> dynamicFind(String a_dynamicWhere);
	public Long countDynamicSearch(String a_dynamicWhere);
	public int updateDynamicSingleDeposit(Long a_staff_id, String a_waers, String a_dynamicWhere);
	public Deposit dynamicFindSingle(String a_dynamicWhere);
}
