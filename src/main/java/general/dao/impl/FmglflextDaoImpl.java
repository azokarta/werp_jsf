package general.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.FmglflextDao;
import general.tables.Fmglflext; 
@Component("fmglflextDao")
public class FmglflextDaoImpl extends GenericDaoImpl<Fmglflext> implements FmglflextDao{
	public Fmglflext findByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk){
    	Query query = this.em
                .createQuery("select f FROM Fmglflext f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr"
                		+ " and f.hkont = :hkont"
                		+ " and f.drcrk = :drcrk");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", a_hkont);  
        query.setParameter("drcrk", a_drcrk);   
        Fmglflext fgl = (Fmglflext) query.getSingleResult();
    	return fgl;
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
	public List<Fmglflext> findByBukrsGjahrHkont(String a_bukrs,int a_gjahr,String a_hkont){
    	Query query = this.em
                .createQuery("select f FROM Fmglflext f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr"
                		+ " and f.hkont = :hkont");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", a_hkont);     
        List<Fmglflext> fgl =  query.getResultList(); 
    	return fgl;
    }	
	public List<Fmglflext> findByBukrsGjahrHkontList(String a_bukrs,int a_gjahr,List<String> al_hkont){
		String select="";
		select = "select f FROM Fmglflext f where f.bukrs= :bukrs"
        		+ " and f.gjahr = :gjahr"
        		+ " and f.hkont IN (:hkont)";
    	Query query = this.em
                .createQuery(select);
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", al_hkont);     
        List<Fmglflext> fgl =  query.getResultList(); 
    	return fgl;
    }
	public List<Fmglflext> dynamicSearch(String a_dynamicWhere){
    	Query query = this.em
                .createQuery("select f FROM Fmglflext f where " + a_dynamicWhere);
        List<Fmglflext> fgl =  query.getResultList(); 
    	return fgl;
    }
	//  LEFT JOIN
	//on h.hkont = f.hkont and h.bukrs = f.bukrs
	public List<Fmglflext> getDailyFinDocGroupedByHkont(String a_bukrs, List<String> al_hkont){
		String select = "select sum(CASE WHEN (d.shkzg = 'S') THEN d.amount ELSE (d.amount * -1) END) as beg_amount, d.hkont, d.waers "
				+ " FROM daily_fin_doc d  "
				+ " where d.bukrs= :bukrs and  d.hkont IN (:hkont)  group by d.bukrs, d.hkont, d.waers";

		Query query = this.em.createNativeQuery(select);
		query.setParameter("bukrs", a_bukrs);
		query.setParameter("hkont", al_hkont);
		List<Fmglflext> fgl =  new ArrayList<Fmglflext>();

//		System.out.println("ffffffffffffffffffffffffffffffffff");
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			Fmglflext wa_fgl = new Fmglflext();
//			System.out.println(result[0]);
			BigDecimal bd = (BigDecimal) result[0];
			if (result[0]==null) wa_fgl.setBeg_amount((double) 0); else wa_fgl.setBeg_amount(bd.doubleValue());

//			System.out.println("sdjdjdjdjdjdjdjdjdjdjdjdjdjdjdjdjdjdjdjdjdj");
			wa_fgl.setHkont(String.valueOf(result[1]));
			if (result[2]==null) wa_fgl.setWaers("USD"); else wa_fgl.setWaers(String.valueOf(result[2]));

			fgl.add(wa_fgl);
		}

		return fgl;
	}

	public List<Fmglflext> getBalanceByBukrsGjahrHkontList(String a_bukrs,int a_gjahr,List<String> al_hkont, String a_fields){
		String select = "select sum(CASE WHEN (f.drcrk = 'S') THEN ("+a_fields+") ELSE (("+a_fields+") * -1) END) as beg_amount, f.hkont, f.waers "
				+ "FROM Fmglflext f  "
				+ "where f.bukrs= :bukrs and  f.hkont IN (:hkont) and f.gjahr = :gjahr group by f.hkont,f.waers,f.bukrs";
		
    	Query query = this.em.createQuery(select);
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", al_hkont);  
        List<Fmglflext> fgl =  new ArrayList<Fmglflext>(); 
        
        List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Fmglflext wa_fgl = new Fmglflext();
    		if (result[0]==null) wa_fgl.setBeg_amount((double) 0); else wa_fgl.setBeg_amount((double) result[0]);     		
    		wa_fgl.setHkont(String.valueOf(result[1]));
    		if (result[2]==null) wa_fgl.setWaers("USD"); else wa_fgl.setWaers(String.valueOf(result[2]));

    		fgl.add(wa_fgl);
    	  }
        
    	return fgl;
    }
	public List<Fmglflext> getBalanceByBukrsGjahr(String a_bukrs,int a_gjahr, String a_fields){
		String select = "select sum(CASE WHEN (f.drcrk = 'S') THEN ("+a_fields+") ELSE (("+a_fields+") * -1) END) as beg_amount, f.hkont, f.waers "
				+ "FROM Fmglflext f "
				+ "where f.bukrs= :bukrs and f.gjahr = :gjahr group by f.hkont,f.waers,f.bukrs";

    	Query query = this.em.createQuery(select);
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);      
        List<Fmglflext> fgl =  new ArrayList<Fmglflext>(); 
        
        List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Fmglflext wa_fgl = new Fmglflext();
    		if (result[0]==null) wa_fgl.setBeg_amount((double) 0); else wa_fgl.setBeg_amount((double) result[0]);     		
    		wa_fgl.setHkont(String.valueOf(result[1]));
    		if (result[2]==null) wa_fgl.setWaers("USD"); else wa_fgl.setWaers(String.valueOf(result[2]));
    		
    		fgl.add(wa_fgl);
    	  }
        
    	return fgl;
    }
	public double findByBukrsGjahrAmount(String a_bukrs,int a_gjahr,String a_hkont){
		String select="";
		select = "select sum(case f.drcrk when 'S' then f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9+f.month10+f.month11+f.month12 "+
				 "else (f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9+f.month10+f.month11+f.month12)*-1 end ) amount "+
				 "from fmglflext f where f.bukrs= :bukrs "+
        		 " and f.gjahr = :gjahr "+
        		 " and f.hkont = :hkont ";
    	Query query = this.em
                .createNativeQuery(select);
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", a_hkont);
        double summa = 0;
        if (query.getSingleResult()!=null)
        {
        	summa = Double.parseDouble(String.valueOf(query.getSingleResult())); 
        }
    	return summa;
    }
	
	public List<Object[]> getBalanceFrep6(String a_bukrs,int a_gjahr,String a_whereClause,String a_fields){
		String select="";
		select = "select a2.text45 city,a2.waers,a2.hkont,a2.usd,a2.kzt,a2.uzs,a2.kgs,a2.azn,a2.myr,a2.rn,s2.text45 hkontname from ("
					+" select a1.*,rownum rn from"
					+" (select "
					+" case when par.text45='Istanbul HQ' then 'ГЛ.ОФИС-AURA' else par.text45 end text45"
					+" ,f.waers,f.hkont,"
					+" sum(CASE WHEN (f.drcrk = 'S' and f.waers='USD') THEN ("+a_fields+")" 
					+" when (f.drcrk = 'H' and f.waers='USD') then (("+a_fields+") * -1) else 0 END) as USD"
					+" ,sum(CASE WHEN (f.drcrk = 'S' and f.waers='KZT') THEN ("+a_fields+") "
					+" when (f.drcrk = 'H' and f.waers='KZT') then (("+a_fields+") * -1)  else 0 END) as KZT"
					+" ,sum(CASE WHEN (f.drcrk = 'S' and f.waers='UZS') THEN ("+a_fields+") "
					+" when (f.drcrk = 'H' and f.waers='UZS') then (("+a_fields+") * -1)  else 0 END) as UZS"
					+" ,sum(CASE WHEN (f.drcrk = 'S' and f.waers='KGS') THEN ("+a_fields+") "
					+" when (f.drcrk = 'H' and f.waers='KGS') then (("+a_fields+") * -1)  else 0 END) as KGS"
					+" ,sum(CASE WHEN (f.drcrk = 'S' and f.waers='AZN') THEN ("+a_fields+") "
					+" when (f.drcrk = 'H' and f.waers='AZN') then (("+a_fields+") * -1)  else 0 END) as AZN"
					+" ,sum(CASE WHEN (f.drcrk = 'S' and f.waers='MYR') THEN ("+a_fields+") "
					+" when (f.drcrk = 'H' and f.waers='MYR') then (("+a_fields+") * -1)  else 0 END) as MYR"			
					+" from Fmglflext f, branch b, skat s, branch par"
					+" where s.hkont=f.hkont and s.bukrs=f.bukrs and s.branch_id=b.branch_id"
					+" and b.parent_branch_id=par.branch_id"
					+" and f.gjahr=:gjahr and b.bukrs=:bukrs"
					+ a_whereClause
					+" group by ROLLUP(par.text45, f.waers,f.hkont )"
					+" order by par.text45, f.waers,f.hkont) a1) a2 left join skat s2 on a2.hkont=s2.hkont and s2.bukrs=:bukrs"
					+" order by rn";
    	Query query = this.em
                .createNativeQuery(select);
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        List<Object[]> results = query.getResultList();
        return results;
    }
	
	public List<Fmglflext> findAll(String a_bukrs){
    	Query query = this.em
                .createQuery("select f FROM Fmglflext f where f.bukrs= :bukrs order by gjahr");
        query.setParameter("bukrs", a_bukrs);
        List<Fmglflext> fgl =  query.getResultList(); 
    	return fgl;
    }
	public List<Fmglflext> findAll(String a_bukrs,int a_gjahr){
		Query query = this.em
                .createQuery("select f FROM Fmglflext f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr order by gjahr");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);   
        List<Fmglflext> fgl =  query.getResultList(); 
    	return fgl;
    }
}
