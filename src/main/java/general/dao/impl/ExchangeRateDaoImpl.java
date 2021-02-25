package general.dao.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.tables.ExchangeRate;

import org.springframework.stereotype.Component;

@Component("exchangeRateDao")
public class ExchangeRateDaoImpl extends GenericDaoImpl<ExchangeRate> implements ExchangeRateDao{
	public List<ExchangeRate> getLastCurrencyRates(){
        Query query = this.em
                .createQuery("select er "+
                			 "FROM  ExchangeRate er "+
                			 "where er.exrate_id =  some ( "+
                			 								"SELECT max(er2.exrate_id) "+
                			 								"FROM ExchangeRate er2 "+ 
                			 								"group by er2.secondary_currency, er2.type, er2.bukrs "+
                			 							  ") order by er.type, er.bukrs, er.secondary_currency");    
         List<ExchangeRate> er =  query.getResultList();
         return er;
    } 
	public List<ExchangeRate> getLastCurrencyRates(Date a_date){
		String select = "select er "+
   			 "FROM  ExchangeRate er "+
   			 "where er.exrate_id =  some ( "+
   			 								"SELECT max(er2.exrate_id) "+
   			 								"FROM ExchangeRate er2 where er2.exrate_date <= :a_date "+ 
   			 								"group by er2.secondary_currency, er2.type, er2.bukrs "+
   			 							  ") ";
		
        Query query = this.em
                .createQuery(select);    
         query.setParameter("a_date", a_date); 
         List<ExchangeRate> er =  query.getResultList();
         return er;
    }
	
	public List<ExchangeRate> getLastCurrencyRatesDynamic(String a_dynamicWhere){
        Query query = this.em
                .createQuery("select er "+
                			 "FROM  ExchangeRate er "+
                			 "where er.exrate_id =  some ( "+
                			 								"SELECT max(er2.exrate_id) "+
                			 								"FROM ExchangeRate er2 "+ 
                			 								"group by er2.secondary_currency, er2.type, er2.bukrs "+
                			 							  ") and " + a_dynamicWhere);    
         List<ExchangeRate> er =  query.getResultList();
         return er;
    }
	
	public Map<String,ExchangeRate> getLastCurrencyRatesDynamicMap(String a_dynamicWhere){
		
		Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
        Query query = this.em
                .createQuery("select er "+
                			 "FROM  ExchangeRate er "+
                			 "where er.exrate_id =  some ( "+
                			 								"SELECT max(er2.exrate_id) "+
                			 								"FROM ExchangeRate er2 "+ 
                			 								"where "+a_dynamicWhere+
                			 								"group by er2.secondary_currency, er2.type, er2.bukrs "+
                			 							  ")");    
         List<ExchangeRate> er =  query.getResultList();
         for(ExchangeRate wa:er)
         {
        	 l_er_map.put(wa.getSecondary_currency(), wa);
         }
         return l_er_map;
    }
	
	public List<ExchangeRate> getCurrencyHistory(String a_dynamicWhere, Date a_from, Date a_to){
		a_dynamicWhere = "select er "+
   			 "FROM  ExchangeRate er "+
   			 "where er.exrate_date >= :a_from  and :a_to >= er.exrate_date " + a_dynamicWhere;
        Query query = this.em
                .createQuery(a_dynamicWhere);    
        query.setParameter("a_from", a_from); 
        query.setParameter("a_to", a_to); 
         List<ExchangeRate> er =  query.getResultList();
         return er;
    }
	
	public Long countByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk){
    	Query query = this.em
                .createQuery("select count(f.bukrs) FROM Fmglflext f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr"
                		+ " and f.hkont = :hkont"
                		+ " and f.drcrk = :drcrk");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", a_hkont);  
        query.setParameter("drcrk", a_drcrk);   
        Long count = (Long) query.getSingleResult();        
        return count;
    } 
	public int countExRateForToday(String a_waers){
    	Query query = this.em
                .createNativeQuery("select count(*) "+ 
                			 "from exchange_rate er "+ 
                			 "where er.secondary_currency='"+a_waers+"' and er.exrate_date=TO_CHAR(SYSDATE, 'YYYY-MM-DD') and er.type=1");
        int count = Integer.parseInt(String.valueOf(query.getSingleResult())) ;  
        return count;
    } 
	public ExchangeRate getLastCurrencyRate(String a_waers,int a_type){
        Query query = this.em
                .createQuery("select er "+
                			 "FROM  ExchangeRate er "+
                			 "where er.exrate_id =  some ( "+
                			 								"SELECT max(er2.exrate_id) "+
                			 								"FROM ExchangeRate er2 "+ 
                			 								"where er2.secondary_currency='"+a_waers+"' and er2.type="+a_type+" "+
                			 								"group by er2.secondary_currency, er2.type, er2.bukrs "+
                			 							  ") order by er.type, er.bukrs, er.secondary_currency");
        
        ExchangeRate er = (ExchangeRate) query.getSingleResult();
         return er;
    }
	public double getLastCurrencyRateInternal(String a_bukrs,String a_waers_osnovnoi,String a_waers_dop){
		if (a_waers_osnovnoi!=null && a_waers_osnovnoi.equals(a_waers_dop))
		return 1;
		
		if (a_waers_osnovnoi.equals("USD") && !a_waers_dop.equals("USD"))
		{
			if (a_bukrs==null || a_bukrs.length()==0 || a_bukrs.equals("0"))
			{
				throw new DAOException("Компания отсутсвует getLastCurrencyRateInternal");
			}
			Query query = this.em
	                .createQuery("select er "+
	                			 "FROM  ExchangeRate er "+
	                			 "where er.exrate_id =  some ( "+
	                			 								"SELECT max(er2.exrate_id) "+
	                			 								"FROM ExchangeRate er2 "+ 
	                			 								"where er.secondary_currency='"+a_waers_dop+"' and type=2 and bukrs='"+a_bukrs+"' "+
	                			 								"group by er2.secondary_currency, er2.type, er2.bukrs "+
	                			 							  ") order by er.type, er.bukrs, er.secondary_currency");
			ExchangeRate er = (ExchangeRate) query.getSingleResult();
			if (er!=null)
			return er.getSc_value();
			else throw new DAOException("Курс не найден getLastCurrencyRateInternal");
	         
			
		}
		else throw new DAOException("getLastCurrencyRateInternal");
		
		
        
        
        
    }
	
}
