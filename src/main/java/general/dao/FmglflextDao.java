package general.dao;

import java.util.List;


import general.tables.Fmglflext;

public interface FmglflextDao  extends GenericDao<Fmglflext>{
	public Fmglflext findByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk);
	public Long countByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk);
	public List<Fmglflext> findByBukrsGjahrHkont(String a_bukrs,int a_gjahr,String a_hkont);
	public List<Fmglflext> dynamicSearch(String a_dynamicWhere);
	public List<Fmglflext> findByBukrsGjahrHkontList(String a_bukrs,int a_gjahr,List<String> al_hkont);
	public List<Fmglflext> getBalanceByBukrsGjahrHkontList(String a_bukrs,int a_gjahr,List<String> al_hkont, String a_fields);
	public List<Fmglflext> getBalanceByBukrsGjahr(String a_bukrs,int a_gjahr, String a_fields);
	public double findByBukrsGjahrAmount(String a_bukrs,int a_gjahr,String a_hkont);
	public List<Object[]> getBalanceFrep6(String a_bukrs,int a_gjahr,String a_whereClause,String a_fields);
	public List<Fmglflext> findAll(String a_bukrs);
	public List<Fmglflext> findAll(String a_bukrs,int a_gjahr);
}
