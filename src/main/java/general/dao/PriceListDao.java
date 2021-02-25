package general.dao;

import java.util.List;

import general.tables.PriceList;

public interface PriceListDao extends GenericDao<PriceList>{
	public List<PriceList> dynamicFindPriceList(String a_dynamicWhere);
	public List<PriceList> findAll(String condition);
	public List<PriceList> findAll();
	public List<PriceList> findAllLast();
	public List<PriceList> findAllQuery(String a_query);
	public List<PriceList> findMatnrWithLastAmounts2(String a_bukrs, String a_waers);
	
	public List<PriceList> findAllWithMatnr(String cond);
	public List<Object[]> findMatnrWithLastAmounts(String a_bukrs, String a_waers, String a_lang) throws DAOException;
}
