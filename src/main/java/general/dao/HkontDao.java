package general.dao;

import java.util.List;

import general.tables.Hkont; 

public interface HkontDao extends GenericDao<Hkont>{
	public Hkont findByIds(String a_bukrs,String a_hkont);
	public Long countByIds(String a_bukrs,String a_hkont);
	public List<Hkont> findAll();
	public List<Hkont> findAll(String c);
	public List<String> findWaersByBukrsBranchId(String a_bukrs,Long a_branch_id);
	public List<Hkont> findHkontBranchTree(String a_bukrs,Long a_branch_id,String a_waers);
	public List<Object[]> getCashBankBranchAll() throws DAOException;
}
