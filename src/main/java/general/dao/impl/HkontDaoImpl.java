package general.dao.impl;
 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import general.dao.DAOException;
import general.dao.HkontDao;
import general.output.tables.Frep1OutputTable;
import general.tables.Hkont; 

import org.springframework.stereotype.Component;

@Component("hkontDao")
public class HkontDaoImpl extends GenericDaoImpl<Hkont> implements HkontDao{
	public Hkont findByIds(String a_bukrs,String a_hkont){
    	Query query = this.em
                .createQuery("select s FROM Hkont s where s.bukrs= :bukrs"
                		+ " and s.hkont= :hkont");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("hkont", a_hkont);   
        Hkont hkn = (Hkont) query.getSingleResult();
    	return hkn;
    }
	public Long countByIds(String a_bukrs,String a_hkont){
    	Query query = this.em
                .createQuery("select count(s.bukrs) FROM Hkont s where s.bukrs= :bukrs"
                		+ " and s.hkont= :hkont");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("hkont", a_hkont);   
        Long count = (Long) query.getSingleResult();        
        return count;
    }
	public List<Hkont> findAll(){
    	Query query = this.em
                .createQuery("select h FROM Hkont h order by hkont ASC");
    	List<Hkont> l_hkont =  query.getResultList();        
        return l_hkont;
    }
	public List<Hkont> findAll(String c) {
		String s = "select h FROM Hkont h";
		if(c.length() > 0){
			s += " WHERE " + c;
		}
		Query query = this.em
                .createQuery( s);
    	List<Hkont> l_hkont =  query.getResultList();        
        return l_hkont;
	}
	
	public List<String> findWaersByBukrsBranchId(String a_bukrs,Long a_branch_id){
    	Query query = this.em
                .createQuery("select h.waers FROM Hkont h where h.bukrs= :bukrs and h.branch_id= :branch_id group by h.waers");
        query.setParameter("bukrs", a_bukrs);
        query.setParameter("branch_id", a_branch_id); 
        List<String> prl = query.getResultList();
    	return prl;
    }
	public List<Hkont> findHkontBranchTree(String a_bukrs,Long a_branch_id,String a_waers){
		List<Hkont> l_hkont = new ArrayList<Hkont>();
    	Query query = this.em
                .createNativeQuery("select s.bukrs,s.hkont,s.waers,s.branch_id,s.text45 from skat s where s.branch_id in"
					+" ("
					+" select br.branch_id from branch br"
					+" where br.bukrs=:bukrs"
					+" start with br.branch_id=:branch_id"
					+" connect by prior br.branch_id=br.parent_branch_id"
					+" ) and s.waers=:waers order by s.hkont");
        query.setParameter("bukrs", a_bukrs);
        query.setParameter("waers", a_waers);
        query.setParameter("branch_id", a_branch_id); 
        List<Object[]> results = query.getResultList();
        for (Object[] result : results) {
			Hkont wa_hkont = new Hkont();
			//wa_fot.setIndex(index);
			if (result[0]!=null) wa_hkont.setBukrs(String.valueOf(result[0]));
			if (result[1]!=null) wa_hkont.setHkont(String.valueOf(result[1]));
			if (result[2]!=null) wa_hkont.setWaers(String.valueOf(result[2]));
			if (result[3]!=null) wa_hkont.setBranch_id(Long.parseLong(String.valueOf(result[3])));
			if (result[4]!=null) wa_hkont.setText45(String.valueOf(result[4]));
			l_hkont.add(wa_hkont);
			

		}
    	
    	return l_hkont;
    }
	
	public List<Object[]> getCashBankBranchAll() throws DAOException
	{	
		try
		{			
			Query query = this.em.createNativeQuery(
					"select bukrs,branch_id,hkont from cash_bank_branch c");
			List<Object[]> results = query.getResultList();
			
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
}
