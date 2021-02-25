package general.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.BonusDao; 
import general.tables.Bonus; 
import general.tables.Branch;

@Component("bonusDao")
public class BonusDaoImpl extends GenericDaoImpl<Bonus> implements BonusDao{
	public List<Bonus> dynamicFindBonuses(String a_dynamicWhere){ 
    	
    	Query query = this.em
                .createQuery("select b FROM Bonus b where "+a_dynamicWhere); 
    	List<Bonus> bon =  query.getResultList();
    	return bon;
    }
	
	public List<Bonus> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM Bonus b");  
    	List<Bonus> bon =  query.getResultList();
    	return bon;
    }
	
	public Bonus dynamicFindBonus(String a_dynamicWhere)
	{ 
    	try
    	{    		
	    	Query query = this.em.createQuery("select b FROM Bonus b where "+a_dynamicWhere); 
	    	Bonus bon = (Bonus) query.getSingleResult();
	    	return bon;
    	}	
    	catch (NoResultException nre)
    	{
    		//Ignore this because as per your logic this is ok!
    		return null;
		}
    }
}
