package general.dao;

import java.util.List;

import general.tables.Fmglflext2;

public interface Fmglflext2Dao extends GenericDao<Fmglflext2>{
	public Fmglflext2 findByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk, Long a_branch_id);
	public Long countByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk, Long a_branch_id);
	public List<Fmglflext2> findAll(String a_bukrs);
	public List<Fmglflext2> findAll(String a_bukrs,int a_gjahr);
}
