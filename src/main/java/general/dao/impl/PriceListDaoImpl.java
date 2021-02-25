package general.dao.impl;

import general.dao.DAOException;
import general.dao.PriceListDao;
import general.tables.Matnr;
import general.tables.PriceList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("priceListDao")
public class PriceListDaoImpl extends GenericDaoImpl<PriceList> implements PriceListDao {

	public List<PriceList> dynamicFindPriceList(String a_dynamicWhere) {
		String s = " SELECT p FROM PriceList p ";
		if (a_dynamicWhere.length() > 0) {
			s += " WHERE " + a_dynamicWhere;
		}
		// System.out.println("Query: " + s);
		Query query = this.em.createQuery(s);
		List<PriceList> l_priceList = query.getResultList();
		return l_priceList;
	}

	public List<PriceList> findMatnrWithLastAmounts2(String a_bukrs, String a_waers) {
		Query query = this.em.createQuery("select p" + " FROM  PriceList p" + " where  p.price_list_id = some ("
				+ " SELECT max(p2.price_list_id)" + " FROM PriceList p2" + " group by p2.bukrs, p2.matnr, p2.waers"
				+ " )"
				+ " and   (p.customer_id = 0 or p.customer_id is null) and p.bukrs = :bukrs and p.waers = :waers");

		query.setParameter("bukrs", a_bukrs);
		query.setParameter("waers", a_waers);

		List<PriceList> l_priceList = query.getResultList();
		return l_priceList;
	}

	@Override
	public List<PriceList> findAll(String condition) {
		String s = "SELECT p FROM PriceList p ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public List<PriceList> findAll() {
		String s = "SELECT p FROM PriceList p ";
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public List<PriceList> findAllQuery(String a_query) {
		String qq = a_query;
		Query q = this.em.createQuery(qq);
		return q.getResultList();
	}

	@Override
	public List<PriceList> findAllWithMatnr(String cond) {
		String s = "SELECT p,m FROM PriceList p LEFT JOIN p.rmatnr m  ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		// System.out.println(s);
		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<PriceList> out = new ArrayList<PriceList>();
		for (Object[] o : result) {
			PriceList p = (PriceList) o[0];
			Matnr m = (Matnr) o[1];
			if (m == null) {
				continue;
			} else {
				p.setRmatnr(m);
			}

			out.add(p);
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PriceList> findAllLast() {
		String s = " SELECT DISTINCT p FROM PriceList p ORDER BY p.from_date DESC";
		Query query = this.em.createQuery(s);
		return query.getResultList();
	}
	
	
	public List<Object[]> findMatnrWithLastAmounts(String a_bukrs, String a_waers, String a_lang) throws DAOException
	{	
		try
		{
			String matnrcol = "";
			
			if (a_lang.equals("en")) matnrcol = "m.name_en";
			else if (a_lang.equals("tr")) matnrcol = "m.name_tr";
			else matnrcol = "m.text45";
			
//			Query query = this.em.createNativeQuery("select p.matnr,p.waers,p.price,"+matnrcol+",m.code FROM  Price_list p,Matnr m" + " where  p.price_list_id = some ("
//					+ " SELECT max(p2.price_list_id)" + " FROM Price_list p2" + " group by p2.bukrs, p2.matnr, p2.waers"
//					+ " )"
//					+ " and   (p.customer_id = 0 or p.customer_id is null) and p.bukrs = :bukrs and p.waers = :waers and m.matnr=p.matnr and m.type<>1");
			
			Query query = this.em.createNativeQuery("select p.matnr,p.waers,p.price,"+matnrcol+",m.code FROM  Price_list p,Matnr m" + " where  "
					+ " p.active=1 and   (p.customer_id = 0 or p.customer_id is null) and p.bukrs = :bukrs and p.waers = :waers and m.matnr=p.matnr and m.type<>1");

			query.setParameter("bukrs", a_bukrs);
			query.setParameter("waers", a_waers);

			List<Object[]> results = query.getResultList();
			return results;
			
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
}
